package com.example.iothome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HumFragment extends Fragment {

    private TextView temperatureText, humidityText;
    private ToggleButton weakModeBtn, moderateModeBtn, strongModeBtn;
    private ToggleButton activeModeButton = null; // To track active mode button
    private Button humTurnOnBtn, humTurnOffBtn;
    private boolean isHumTurnedOn = false;
    private DatabaseReference humDataRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        temperatureText=view.findViewById(R.id.textTemperature);
        humidityText=view.findViewById(R.id.textHumidity);

        weakModeBtn=view.findViewById(R.id.weakModeBtn);
        moderateModeBtn=view.findViewById(R.id.moderateModeBtn);
        strongModeBtn=view.findViewById(R.id.strongModeBtn);

        humTurnOnBtn = view.findViewById(R.id.humTurnOnBtn);
        humTurnOffBtn = view.findViewById(R.id.humTurnOffBtn);

        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        humDataRef = database.getReference("humData");

        if (humDataRef != null) {
            Log.d("HumFragment", "Firebase Connected: Yes");
        } else {
            Log.d("HumFragment", "Firebase Connected: No");
        }

        // Set value event listener for airData changes
        humDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Update UI elements here based on the changes in airData
                // For example:
                String humidity = snapshot.child("humidity").getValue(String.class);
                String temperature = snapshot.child("temperature").getValue(String.class);

                temperatureText.setText(temperature+"°C");
                humidityText.setText(humidity+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving data: " + error.getMessage());
            }
        });

        weakModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHumMode("weak");
                updateButtonState(weakModeBtn);
            }
        });

        moderateModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHumMode("moderate");
                updateButtonState(moderateModeBtn);
            }
        });

        strongModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHumMode("strong");
                updateButtonState(strongModeBtn);
            }
        });

        // Set click listeners for turn on/off buttons
        humTurnOnBtn.setOnClickListener(v -> {
            humDataRef.child("humAuto").setValue(false); //자동모드 off
            // 파이어베이스의 isHumTurnedOn 값 가져오기
            humDataRef.child("isHumTurnedOn").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        isHumTurnedOn = snapshot.getValue(Boolean.class);
                        if (!isHumTurnedOn) {
                            // isHumTurnedOn가 false인 경우
                            // turnOnHum 값을 true로 업데이트
                            humDataRef.child("turnOnHum").setValue(true)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            humDataRef.child("isHumTurnedOn").setValue(true);
                                        }
                                    });
                        } else {
                            // isHumTurnedOn가 true인 경우
                            // 이미 가습기가 켜져있음을 알리는 토스트 띄우기
                            Toast.makeText(requireContext(), "이미 가습기가 켜져있습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        humTurnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humDataRef.child("humAuto").setValue(false); //자동모드 off
                // 파이어베이스의 isLivingRoomWindowClosed 값 가져오기
                humDataRef.child("isHumTurnedOn").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            isHumTurnedOn = snapshot.getValue(Boolean.class);
                            if (isHumTurnedOn) {
                                // isHumTurnedOn가 true인 경우
                                // turnOnHum 값을 false로 업데이트
                                humDataRef.child("turnOnHum").setValue(false)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                humDataRef.child("isHumTurnedOn").setValue(false);
                                            }
                                        });
                            } else {
                                // isHumTurnedOn가 false인 경우
                                // 이미 가습기가 꺼져있음을 알리는 토스트 띄우기
                                Toast.makeText(requireContext(), "이미 가습기가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void updateButtonState(ToggleButton clickedButton) {
        if (activeModeButton != null && activeModeButton != clickedButton) {
            // Reset the previously active button
            activeModeButton.setChecked(false);
        }

        // Set the clicked button as active
        activeModeButton = clickedButton;
        activeModeButton.setChecked(true);
    }

    private void updateHumMode(String mode) {
        humDataRef.child("isHumTurnedOn").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isHumTurnedOn = snapshot.getValue(Boolean.class);
                if (isHumTurnedOn != null && isHumTurnedOn) {
                    // Assuming humDataRef is my DatabaseReference for humData
                    humDataRef.child("humMode").setValue(mode);
                } else {
                    // Handle the case where air is not turned on
                    Toast.makeText(requireContext(), "가습기가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving isAirTurnedOn: " + error.getMessage());
            }
        });
    }
}