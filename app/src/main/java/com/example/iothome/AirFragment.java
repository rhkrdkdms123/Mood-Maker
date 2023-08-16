package com.example.iothome;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AirFragment extends Fragment {

    private TextView textPM0_1, textPM2_5, textPM10;
    private TextView textAirQuality;
    private ToggleButton sleepingModeBtn, weakModeBtn, moderateModeBtn, strongModeBtn, turboModeBtn;
    private ToggleButton activeModeButton = null; // To track active mode button
    private Button airTurnOnBtn, airTurnOffBtn;
    private boolean isAirTurnedOn = false;
    private DatabaseReference airDataRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_air, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textPM0_1 = view.findViewById(R.id.textPM0_1);
        textPM2_5 = view.findViewById(R.id.textPM2_5);
        textPM10 = view.findViewById(R.id.textPM10);

        textAirQuality = view.findViewById(R.id.airQualityText);

        sleepingModeBtn = view.findViewById(R.id.sleepingModeBtn);
        weakModeBtn = view.findViewById(R.id.weakModeBtn);
        moderateModeBtn = view.findViewById(R.id.moderateModeBtn);
        strongModeBtn = view.findViewById(R.id.strongModeBtn);
        turboModeBtn = view.findViewById(R.id.turboModeBtn);

        airTurnOnBtn = view.findViewById(R.id.airTurnOnBtn);
        airTurnOffBtn = view.findViewById(R.id.airTurnOffBtn);

        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        airDataRef = database.getReference("airData");

        if (airDataRef != null) {
            Log.d("AirFragment", "Firebase Connected: Yes");
        } else {
            Log.d("AirFragment", "Firebase Connected: No");
        }

        // Set value event listener for airData changes
        airDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Update UI elements here based on the changes in airData
                // For example:
                String pm0_1 = snapshot.child("PM0_1").getValue(String.class);
                String pm2_5 = snapshot.child("PM2_5").getValue(String.class);
                String pm10 = snapshot.child("PM10").getValue(String.class);

                textPM0_1.setText(pm0_1+"㎍/㎥");
                textPM2_5.setText(pm2_5+"㎍/㎥");
                textPM10.setText(pm10+"㎍/㎥");

                // Calculate air quality level
                String airQuality = evaluateAirQuality(pm2_5, pm0_1, pm10);

                // Update the TextView with the calculated air quality
                textAirQuality.setText("공기질 "+airQuality);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving data: " + error.getMessage());
            }
        });

        // Set click listeners for mode buttons
        sleepingModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWindMode("sleeping");
                updateButtonState(sleepingModeBtn);
            }
        });

        weakModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWindMode("weak");
                updateButtonState(weakModeBtn);
            }
        });

        moderateModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWindMode("moderate");
                updateButtonState(moderateModeBtn);
            }
        });

        strongModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWindMode("strong");
                updateButtonState(strongModeBtn);
            }
        });

        turboModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWindMode("turbo");
                updateButtonState(turboModeBtn);
            }
        });

        // Set click listeners for turn on/off buttons
        airTurnOnBtn.setOnClickListener(v -> {
            airDataRef.child("airAuto").setValue(false); //자동모드 off
            // 파이어베이스의 isAirTurnedOn 값 가져오기
            airDataRef.child("isAirTurnedOn").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        isAirTurnedOn = snapshot.getValue(Boolean.class);
                        if (!isAirTurnedOn) {
                            // isAirTurnedOn가 false인 경우
                            // turnOnAir 값을 true로 업데이트
                            airDataRef.child("turnOnAir").setValue(true)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            airDataRef.child("isAirTurnedOn").setValue(true);
                                        }
                                    });
                        } else {
                            // isAirTurnedOn가 true인 경우
                            // 이미 공기청정기가 켜져있음을 알리는 토스트 띄우기
                            Toast.makeText(requireContext(), "이미 공기청정기가 켜져있습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        airTurnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                airDataRef.child("airAuto").setValue(false); //자동모드 off
                // 파이어베이스의 isLivingRoomWindowClosed 값 가져오기
                airDataRef.child("isAirTurnedOn").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            isAirTurnedOn = snapshot.getValue(Boolean.class);
                            if (isAirTurnedOn) {
                                // isAirTurnedOn가 true인 경우
                                // turnOnAir 값을 false로 업데이트
                                airDataRef.child("turnOnAir").setValue(true)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                airDataRef.child("isAirTurnedOn").setValue(false);
                                            }
                                        });
                            } else {
                                // isAirTurnedOn가 false인 경우
                                // 이미 공기청정기가 꺼져있음을 알리는 토스트 띄우기
                                Toast.makeText(requireContext(), "이미 공기청정기가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private String evaluateAirQuality(String pm2_5, String pm0_1, String pm10) {
        // Define threshold values for each parameter
        int pm2_5Threshold = 15;  // Adjust these values as needed
        int pm0_1Threshold = 10;
        int pm10Threshold = 30;

        int intPM2_5=Integer.parseInt(pm2_5);
        int intPM0_1=Integer.parseInt(pm0_1);
        int intPM10=Integer.parseInt(pm10);

        // Evaluate air quality based on thresholds
        if (intPM2_5 < pm2_5Threshold && intPM0_1 < pm0_1Threshold && intPM10 < pm10Threshold) {
            return "매우좋음";
        } else if (intPM2_5 < 30 && intPM0_1 < 20 && intPM10 < 50) {
            return "좋음";
        } else if(intPM2_5 < 50 && intPM0_1 < 30 && intPM10 < 100) {
            return "보통";
        } else if(intPM2_5 < 100 && intPM0_1 < 60 && intPM10 < 150) {
            return "약간 나쁨";
        } else {
            return "매우나쁨";
        }
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

    private void updateWindMode(String mode) {
        airDataRef.child("isAirTurnedOn").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isAirTurnedOn = snapshot.getValue(Boolean.class);
                if (isAirTurnedOn != null && isAirTurnedOn) {
                    // Assuming airDataRef is my DatabaseReference for airData
                    airDataRef.child("windPower").setValue(mode);
                } else {
                    // Handle the case where air is not turned on
                    Toast.makeText(requireContext(), "공기청정기가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving isAirTurnedOn: " + error.getMessage());
            }
        });
    }
}
