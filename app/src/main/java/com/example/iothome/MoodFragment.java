package com.example.iothome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MoodFragment extends Fragment {

    GridView gridview;
    MoodAdapter adapter;
    TextView currentMoodText;
    Button turnOffModeBtn;
    private  DatabaseReference moodDataRef;
    private DatabaseReference airDataRef;
    private DatabaseReference windowDataRef;
    private DatabaseReference humDataRef;
    private DatabaseReference lightDataRef;
    private DatabaseReference speakerDataRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gridview=(GridView) view.findViewById(R.id.moodGrid);
        currentMoodText=view.findViewById(R.id.currentMoodText);
        turnOffModeBtn=view.findViewById(R.id.turnOffModeBtn);

        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        moodDataRef = database.getReference("moodData");

        if (moodDataRef != null) {
            Log.d("MoodFragment", "Firebase Connected: Yes");
        } else {
            Log.d("MoodFragment", "Firebase Connected: No");
        }

        final ArrayList<String> titles = new ArrayList<String>();
        final ArrayList<String> subTexts = new ArrayList<String>();
        final ArrayList<Integer> images = new ArrayList<Integer>();

        titles.add("파티 모드");
        titles.add("취침 모드");
        titles.add("집중 모드");
        titles.add("로맨틱 모드");

        subTexts.add("신나는 음악과 화려한 조명을 즐겨보세요");
        subTexts.add("숙면을 위한 공기와 습도를 느껴보세요");
        subTexts.add("집중력을 올려주는 최적의 분위기");
        subTexts.add("연인과의 시간을 더 로맨틱하게 보낼 수 있도록");

        images.add(R.drawable.party);
        images.add(R.drawable.bedtime);
        images.add(R.drawable.study);
        images.add(R.drawable.romantic);

        adapter=new MoodAdapter(getContext(), R.layout.mood_item, titles, subTexts, images);
        gridview.setAdapter(adapter);

        gridview.setCacheColorHint(Color.parseColor("#00000000"));

        moodDataRef.child("moodOption").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentMood = snapshot.getValue(Long.class);
                if(currentMood!=null){
                    if(currentMood==0) currentMoodText.setText("현재 적용 중인 모드가 없습니다.");
                    else {
                        String title=adapter.getTitle((int)(currentMood-1));
                        currentMoodText.setText(title+" 적용 중 입니다.");
                    }
                } else{
                    Log.e("MoodFragment", "currentMood is null" );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving currentMood: " + error.getMessage());
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Long MoodClicked=(long)(position+1);

                moodDataRef.child("moodOption").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long currentMood = snapshot.getValue(Long.class);
                        if (currentMood!=null && currentMood!=MoodClicked) {
                            // Show a dialog to confirm the change
                            showConfirmationDialog(MoodClicked, adapter.getTitle(position));
                        } else {
                            // Handle the case where Mood is not turned on
                            Toast.makeText(requireContext(), "해당 모드는 이미 적용 중 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error retrieving moodOption: " + error.getMessage());
                    }
                });
            }
        });

        turnOffModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파이어베이스의 isMusicPlaying 값 가져오기
                moodDataRef.child("moodOption").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            Long moodOption = snapshot.getValue(Long.class);
                            if (moodOption!=0) {
                                // moodOption이 0(꺼져있음을 의미)이 아닌 경우
                                // moodOption 값을 0으로 업데이트
                                moodDataRef.child("moodOption").setValue(0)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                currentMoodText.setText("현재 적용 중인 모드가 없습니다.");
                                            }
                                        });
                            } else {
                                // moodOption이 0인 경우
                                // 이미 모드 적용이 꺼져있음을 알리는 토스트 띄우기
                                Toast.makeText(requireContext(), "현재 적용 중인 모드가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void showConfirmationDialog(final long optionNumber, String optionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("옵션 설정");

        SpannableString message = new SpannableString("'" + optionText + "'으로 설정하시겠습니까?");
        message.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        builder.setMessage(message);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moodDataRef.child("moodOption").setValue(optionNumber);
                setMoodOption(optionNumber);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void setMoodOption(long optionNumber) {
        switch ((int)optionNumber){
            case 1:
                partyMode();
                break;
            case 2:
                sleepingMode();
                break;
            case 3:
                concentrationMode();
                break;
            case 4:
                romanticMode();
                break;
        }
        currentMoodText.setText(adapter.getTitle((int)optionNumber-1) + " 적용 중 입니다.");
        Toast.makeText(requireContext(), "설정되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void partyMode(){
        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        airDataRef = database.getReference("airData");
        humDataRef = database.getReference("humData");
        lightDataRef = database.getReference("lightData");
        speakerDataRef = database.getReference("speakerData");
        windowDataRef = database.getReference("windowData");

        airDataRef.child("windPower").setValue("turbo");
        humDataRef.child("humMode").setValue("weak");
        lightDataRef.child("lightOption").setValue(2);
        speakerDataRef.child("currentMusic").setValue(1);
        windowDataRef.child("closeLivingRoomWindow").setValue(true);
        windowDataRef.child("closeRoomWindow").setValue(true);
    }

    private void sleepingMode(){
        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        airDataRef = database.getReference("airData");
        humDataRef = database.getReference("humData");
        lightDataRef = database.getReference("lightData");
        speakerDataRef = database.getReference("speakerData");
        windowDataRef = database.getReference("windowData");

        airDataRef.child("windPower").setValue("weak");
        humDataRef.child("humMode").setValue("moderate");
        lightDataRef.child("lightOption").setValue(1);
        speakerDataRef.child("currentMusic").setValue(3);
        windowDataRef.child("closeLivingRoomWindow").setValue(true);
        windowDataRef.child("closeRoomWindow").setValue(true);
    }

    private void concentrationMode(){
        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        airDataRef = database.getReference("airData");
        humDataRef = database.getReference("humData");
        lightDataRef = database.getReference("lightData");
        speakerDataRef = database.getReference("speakerData");
        windowDataRef = database.getReference("windowData");

        airDataRef.child("windPower").setValue("weak");
        humDataRef.child("humMode").setValue("moderate");
        lightDataRef.child("lightOption").setValue(4);
        speakerDataRef.child("currentMusic").setValue(4);
        windowDataRef.child("closeLivingRoomWindow").setValue(false);
        windowDataRef.child("closeRoomWindow").setValue(true);
    }

    private void romanticMode(){
        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        airDataRef = database.getReference("airData");
        humDataRef = database.getReference("humData");
        lightDataRef = database.getReference("lightData");
        speakerDataRef = database.getReference("speakerData");
        windowDataRef = database.getReference("windowData");

        airDataRef.child("windPower").setValue("moderate");
        humDataRef.child("humMode").setValue("strong");
        lightDataRef.child("lightOption").setValue(3);
        speakerDataRef.child("currentMusic").setValue(3);
        windowDataRef.child("closeLivingRoomWindow").setValue(true);
        windowDataRef.child("closeRoomWindow").setValue(true);
    }
}