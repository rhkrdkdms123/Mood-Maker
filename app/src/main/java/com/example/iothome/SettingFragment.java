package com.example.iothome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingFragment extends Fragment {

    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Switch livingRoomWindowAutoSwitch = view.findViewById(R.id.LivingRoomWindowAutoSwitch);
        Switch roomWindowAutoSwitch = view.findViewById(R.id.RoomWindowAutoSwitch);
        Switch airAutoSwitch = view.findViewById(R.id.AirAutoSwitch);
        Switch humAutoSwitch = view.findViewById(R.id.HumAutoSwitch);

        mDatabase.child("windowData").child("LivingRoomWindowAuto").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean livingRoomWindowAuto = snapshot.getValue(Boolean.class);
                if (livingRoomWindowAuto != null) {
                    livingRoomWindowAutoSwitch.setChecked(livingRoomWindowAuto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SettingFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        mDatabase.child("windowData").child("RoomWindowAuto").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean roomWindowAuto = snapshot.getValue(Boolean.class);
                if (roomWindowAuto != null) {
                    roomWindowAutoSwitch.setChecked(roomWindowAuto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SettingFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        mDatabase.child("airData").child("airAuto").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean airAuto = snapshot.getValue(Boolean.class);
                if (airAuto != null) {
                    airAutoSwitch.setChecked(airAuto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SettingFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        mDatabase.child("humData").child("humAuto").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean humAuto = snapshot.getValue(Boolean.class);
                if (humAuto != null) {
                    humAutoSwitch.setChecked(humAuto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SettingFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        livingRoomWindowAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                showAlertDialog("거실 창문", isChecked, "windowData/LivingRoomWindowAuto");
            }
        });

        roomWindowAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                showAlertDialog("방 창문", isChecked, "windowData/RoomWindowAuto");
            }
        });

        airAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                showAlertDialog("공기 청정기", isChecked, "airData/airAuto");
            }
        });

        humAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                showAlertDialog("가습기", isChecked, "humData/humAuto");
            }
        });

        return view;
    }

    private void updateFirebaseData(String path, boolean value) {
        mDatabase.child(path).setValue(value);
    }

    private void showAlertDialog(String itemName, boolean isChecked, String firebasePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(isChecked ? itemName + "를 자동 모드로 설정하시겠습니까?" : itemName + "를 수동 모드로 설정하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateFirebaseData(firebasePath, isChecked);
                        String toastMessage = isChecked ? itemName + "가 자동 모드로 설정되었습니다." : itemName + "가 수동 모드로 설정되었습니다.";
                        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
