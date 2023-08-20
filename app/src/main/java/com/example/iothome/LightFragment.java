package com.example.iothome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LightFragment extends Fragment {
    private DatabaseReference lightDataRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lightDataRef = database.getReference("lightData");

        if (lightDataRef != null) {
            Log.d("LightFragment", "Firebase Connected: Yes");
        } else {
            Log.d("LightFragment", "Firebase Connected: No");
        }

        Switch lightSwitch = view.findViewById(R.id.light_switch);

        lightDataRef.child("isLightTurnedOn").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isLightTurnedOn = snapshot.getValue(Boolean.class);
                if (isLightTurnedOn != null) {
                    lightSwitch.setChecked(isLightTurnedOn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("lightFragment", "Firebase Database Error: " + error.getMessage());
            }
        });

        lightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lightDataRef.child("turnOnLight").setValue(isChecked);
        });

        Button customLight1Button = view.findViewById(R.id.light_option_1_button);
        Button customLight2Button = view.findViewById(R.id.light_option_2_button);
        Button customLight3Button = view.findViewById(R.id.light_option_3_button);
        Button customLight4Button = view.findViewById(R.id.light_option_4_button);

        customLight1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog(1, "비행기 풍경");
            }
        });

        customLight2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog(2, "폭죽 그림");
            }
        });

        customLight3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog(3, "하트 배경");
            }
        });

        customLight4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog(4, "에펠탑 풍경");
            }
        });

        //option 1 텍스트 설정
        String light1_firstLine = "구름 위에 있는 기분을\n느끼고 싶다면";
        String light1_secondLine = "비행기 풍경";

        SpannableString light1BtnString = new SpannableString(light1_firstLine + "\n\n" + light1_secondLine);

        light1BtnString.setSpan(new StyleSpan(Typeface.NORMAL), 0, light1_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light1BtnString.setSpan(new AbsoluteSizeSpan(10, true), 0, light1_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light1BtnString.setSpan(new StyleSpan(Typeface.BOLD), light1_firstLine.length() + 2, light1BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light1BtnString.setSpan(new AbsoluteSizeSpan(16, true), light1_firstLine.length() + 2, light1BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        light1BtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, light1BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customLight1Button.setText(light1BtnString);


        //option 2 텍스트 설정
        String light2_firstLine = "연말 파티에 어울리는";
        String light2_secondLine = "폭죽 그림";

        SpannableString light2BtnString = new SpannableString(light2_firstLine + "\n\n" + light2_secondLine);

        light2BtnString.setSpan(new StyleSpan(Typeface.NORMAL), 0, light2_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light2BtnString.setSpan(new AbsoluteSizeSpan(10, true), 0, light2_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light2BtnString.setSpan(new StyleSpan(Typeface.BOLD), light2_firstLine.length() + 2, light2BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light2BtnString.setSpan(new AbsoluteSizeSpan(16, true), light2_firstLine.length() + 2, light2BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        light2BtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, light2BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customLight2Button.setText(light2BtnString);


        //option 3 텍스트 설정
        String light3_firstLine = "연인과의 분위기를 \n올리고 싶다면";
        String light3_secondLine = "하트 배경";

        SpannableString light3BtnString = new SpannableString(light3_firstLine + "\n\n" + light3_secondLine);

        light3BtnString.setSpan(new StyleSpan(Typeface.NORMAL), 0, light3_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light3BtnString.setSpan(new AbsoluteSizeSpan(10, true), 0, light3_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light3BtnString.setSpan(new StyleSpan(Typeface.BOLD), light3_firstLine.length() + 2, light3BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light3BtnString.setSpan(new AbsoluteSizeSpan(16, true), light3_firstLine.length() + 2, light3BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        light3BtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, light3BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customLight3Button.setText(light3BtnString);


        //option 4 텍스트 설정
        String light4_firstLine = "파리의 무드를 \n내고 싶다면";
        String light4_secondLine = "에펠탑 풍경";

        SpannableString light4BtnString = new SpannableString(light4_firstLine + "\n\n" + light4_secondLine);

        light4BtnString.setSpan(new StyleSpan(Typeface.NORMAL), 0, light4_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light4BtnString.setSpan(new AbsoluteSizeSpan(10, true), 0, light4_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light4BtnString.setSpan(new StyleSpan(Typeface.BOLD), light4_firstLine.length() + 2, light4BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        light4BtnString.setSpan(new AbsoluteSizeSpan(16, true), light4_firstLine.length() + 2, light4BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        light4BtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, light4BtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customLight4Button.setText(light4BtnString);

        return view;
    }

    private void showConfirmationDialog(final int optionNumber, String optionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("옵션 설정");

        SpannableString message = new SpannableString("'" + optionText + "'으로 설정하시겠습니까?");
        message.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        builder.setMessage(message);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveLightOption(optionNumber);
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

    private void saveLightOption(int optionNumber) {
        lightDataRef.child("lightOption").setValue(optionNumber);
        Toast.makeText(requireContext(), "설정되었습니다.", Toast.LENGTH_SHORT).show();
    }
}