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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WindowRoomFragment extends Fragment {

    private TextView rainTextView;
    private ImageView windowImageView;
    private Button openButton;
    private Button closeButton;
    private DatabaseReference windowDataRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_window_room, container, false);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rainTextView = view.findViewById(R.id.roomRainValue);
        windowImageView = view.findViewById(R.id.roomRainImage);
        openButton = view.findViewById(R.id.roomRainOpenBtn);
        closeButton = view.findViewById(R.id.roomRainCloseBtn);

        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        windowDataRef = database.getReference("windowData");

        if (windowDataRef != null) {
            Log.d("RoomFragment", "Firebase Connected: Yes");
        } else {
            Log.d("RoomFragment", "Firebase Connected: No");
        }


        // 데이터 변경 리스너
        windowDataRef.child("LivingRoomRainValue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String rainValue = snapshot.getValue(String.class);
                    Log.d("LivingRoomFragment", "Rain Value: " + rainValue); // 로그 추가

                    if (rainValue != null) {
                        if (rainValue.equals("No Rain")) {
                            rainTextView.setText("비가 오지 않음");
                        } else if (rainValue.equals("Weak Rain")) {
                            rainTextView.setText("약한 비");
                        } else if (rainValue.equals("Heavy Rain")) {
                            rainTextView.setText("강한 비");
                        } else {
                            // 기타 경우 처리
                            rainTextView.setText("비 정보 없음");
                        }
                    } else {
                        rainTextView.setText("비 정보 없음");
                    }
                } else {
                    rainTextView.setText("비 정보 없음");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RoomFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        windowDataRef.child("isRoomWindowClosed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean isWindowClosed=snapshot.getValue(Boolean.class);
                    Log.d("RoomFragment","isWindowClosed: "+isWindowClosed);

                    if(isWindowClosed) {
                        windowImageView.setImageResource(R.drawable.closed_window_image);
                    } else{
                        windowImageView.setImageResource(R.drawable.opend_window_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RoomFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        openButton.setOnClickListener(v -> {
            windowDataRef.child("windowAuto").setValue(false); //자동모드 off
            // 파이어베이스의 isRoomWindowClosed 값 가져오기
            windowDataRef.child("isRoomWindowClosed").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        boolean isWindowClosed = snapshot.getValue(Boolean.class);
                        if (isWindowClosed) {
                            // isRoomWindowClosed가 true인 경우
                            // closeRoomWindow 값을 false로 업데이트
                            windowDataRef.child("closeRoomWindow").setValue(false)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            windowDataRef.child("isRoomWindowClosed").setValue(false);
                                        }
                                    });
                        } else {
                            // isRoomWindowClosed가 false인 경우
                            // 이미 창문이 열려있음을 알리는 토스트 띄우기
                            Toast.makeText(requireContext(), "이미 창문이 열려있습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowDataRef.child("windowAuto").setValue(false); //자동모드 off
                // 파이어베이스의 isRoomWindowClosed 값 가져오기
                windowDataRef.child("isRoomWindowClosed").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            boolean isWindowClosed = snapshot.getValue(Boolean.class);
                            if (!isWindowClosed) {
                                // isRoomWindowClosed가 false인 경우
                                // closeRoomWindow 값을 true로 업데이트
                                windowDataRef.child("closeRoomWindow").setValue(true)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                windowDataRef.child("isLRoomWindowClosed").setValue(true);
                                            }
                                        });
                            } else {
                                // isRoomWindowClosed가 true인 경우
                                // 이미 창문이 닫혀있음을 알리는 토스트 띄우기
                                Toast.makeText(requireContext(), "이미 창문이 닫혀있습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}