package com.example.iothome;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpeakerFragment extends Fragment {

    ListView listView;
    SpeakerAdapter adapter;
    TextView currentMusicText;
    Button turnOffMusicBtn;
    private DatabaseReference speakerDataRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speaker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.speakerListView);
        currentMusicText=view.findViewById(R.id.currentMusicText);
        turnOffMusicBtn=view.findViewById(R.id.turnOffMusicBtn);

        // Firebase 데이터베이스 참조
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        speakerDataRef = database.getReference("speakerData");

        if (speakerDataRef != null) {
            Log.d("SpeakerFragment", "Firebase Connected: Yes");
        } else {
            Log.d("SpeakerFragment", "Firebase Connected: No");
        }

        final ArrayList<String> items1 = new ArrayList<String>();
        final ArrayList<String> items2 = new ArrayList<String>();

        items1.add("철권 6 Karma OST");
        items1.add("Perfect for me");
        items1.add("See the World (Piano Version)");
        items1.add("빗소리 ASMR");

        items2.add("kage_matu_re");
        items2.add("Ron Pope");
        items2.add("Kan gao");
        items2.add("None");

        adapter = new SpeakerAdapter(getContext(), R.layout.speaker_item, items1, items2);
        listView.setAdapter(adapter);

        listView.setCacheColorHint(Color.parseColor("#00000000"));

        speakerDataRef.child("currentMusic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentMusic = snapshot.getValue(Long.class);
                if(currentMusic!=null){
                    if(currentMusic==0) currentMusicText.setText("재생 중인 음악이 없습니다.");
                    else {
                        String title=adapter.getTitle((int)(currentMusic-1));
                        String singer=adapter.getSinger((int)(currentMusic-1));
                        currentMusicText.setText(singer+"-"+title);
                    }
                } else{
                    Log.e("SpeakerFragment", "currentMusic is null" );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving isAirTurnedOn: " + error.getMessage());
            }
        });

        // 특정 노래를 클릭했을 때
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String TitleClicked = adapter.getTitle(position);
                String SingerClicked = adapter.getSinger(position);

                speakerDataRef.child("turnOfftheMusic").setValue(false);
                speakerDataRef.child("isMusicPlaying").setValue(true);

                speakerDataRef.child("currentMusic").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long currentMusic = snapshot.getValue(Long.class);
                        if (currentMusic!=null && currentMusic!=(position+1)) {
                            // Assuming speakerDataRef is my DatabaseReference for humData
                            speakerDataRef.child("currentMusic").setValue(position+1);
                            currentMusicText.setText(SingerClicked+"-"+TitleClicked);
                        } else {
                            // Handle the case where air is not turned on
                            Toast.makeText(requireContext(), "해당 곡은 이미 재생 중입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error retrieving isAirTurnedOn: " + error.getMessage());
                    }
                });
            }
        });

        turnOffMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파이어베이스의 isMusicPlaying 값 가져오기
                speakerDataRef.child("isMusicPlaying").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            Boolean isMusicPlaying = snapshot.getValue(Boolean.class);
                            if (isMusicPlaying) {
                                // isMusicPlaying가 true인 경우
                                // turnOfftheMusic 값을 true로 업데이트
                                speakerDataRef.child("turnOfftheMusic").setValue(true)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                speakerDataRef.child("isMusicPlaying").setValue(false);
                                                speakerDataRef.child("currentMusic").setValue(0);
                                                currentMusicText.setText("재생 중인 음악이 없습니다.");
                                            }
                                        });
                            } else {
                                // isMusicPlaying가 false인 경우
                                // 이미 노래가 꺼져있음을 알리는 토스트 띄우기
                                Toast.makeText(requireContext(), "현재 재생 중인 노래가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}