package com.example.iothome;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button customLightButton = view.findViewById(R.id.light_button);
        Button customWindowButton = view.findViewById(R.id.window_button);
        Button customAirButton = view.findViewById(R.id.airPurifier_button);
        Button customHumButton = view.findViewById(R.id.humidifier_button);
        Button customSpeakerButton = view.findViewById(R.id.speaker_button);
        Button customFrameButton = view.findViewById(R.id.frame_button);

        //조명 버튼 텍스트 설정
        String light_firstLine = "조명";
        String light_secondLine = "방 1";

        SpannableString lightBtnString = new SpannableString(light_firstLine + "\n\n" + light_secondLine);

        lightBtnString.setSpan(new StyleSpan(Typeface.BOLD), 0, light_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        lightBtnString.setSpan(new AbsoluteSizeSpan(20, true), 0, light_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        lightBtnString.setSpan(new StyleSpan(Typeface.NORMAL), light_firstLine.length() + 2, lightBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        lightBtnString.setSpan(new AbsoluteSizeSpan(16, true), light_firstLine.length() + 2, lightBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        lightBtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, lightBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customLightButton.setText(lightBtnString);


        //창문 버튼 텍스트 설정
        String window_firstLine = "창문";
        String window_secondLine = "거실 1 방 1";

        SpannableString windowBtnString = new SpannableString(window_firstLine + "\n\n" + window_secondLine);

        windowBtnString.setSpan(new StyleSpan(Typeface.BOLD), 0, window_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        windowBtnString.setSpan(new AbsoluteSizeSpan(20, true), 0, window_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        windowBtnString.setSpan(new StyleSpan(Typeface.NORMAL), window_firstLine.length() + 2, windowBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        windowBtnString.setSpan(new AbsoluteSizeSpan(16, true), window_firstLine.length() + 2, windowBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        windowBtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, windowBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customWindowButton.setText(windowBtnString);


        //공기 청정기 버튼 텍스트 설정
        String air_firstLine = "공기 청정기";
        String air_secondLine = "거실 1";

        SpannableString airBtnString = new SpannableString(air_firstLine + "\n\n" + air_secondLine);

        airBtnString.setSpan(new StyleSpan(Typeface.BOLD), 0, air_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        airBtnString.setSpan(new AbsoluteSizeSpan(20, true), 0, air_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        airBtnString.setSpan(new StyleSpan(Typeface.NORMAL), air_firstLine.length() + 2, airBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        airBtnString.setSpan(new AbsoluteSizeSpan(16, true), air_firstLine.length() + 2, airBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        airBtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, airBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customAirButton.setText(airBtnString);


        //가습기 버튼 텍스트 설정
        String hum_firstLine = "가습기";
        String hum_secondLine = "방 1";

        SpannableString humBtnString = new SpannableString(hum_firstLine + "\n\n" + hum_secondLine);

        humBtnString.setSpan(new StyleSpan(Typeface.BOLD), 0, hum_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        humBtnString.setSpan(new AbsoluteSizeSpan(20, true), 0, hum_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        humBtnString.setSpan(new StyleSpan(Typeface.NORMAL), hum_firstLine.length() + 2, humBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        humBtnString.setSpan(new AbsoluteSizeSpan(16, true), hum_firstLine.length() + 2, humBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        humBtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, humBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customHumButton.setText(humBtnString);


        //스피커 버튼 텍스트 설정
        String speaker_firstLine = "스피커";
        String speaker_secondLine = "거실 1";

        SpannableString speakerBtnString = new SpannableString(speaker_firstLine + "\n\n" + speaker_secondLine);

        speakerBtnString.setSpan(new StyleSpan(Typeface.BOLD), 0, speaker_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        speakerBtnString.setSpan(new AbsoluteSizeSpan(20, true), 0, speaker_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        speakerBtnString.setSpan(new StyleSpan(Typeface.NORMAL), speaker_firstLine.length() + 2, speakerBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        speakerBtnString.setSpan(new AbsoluteSizeSpan(16, true), speaker_firstLine.length() + 2, speakerBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        speakerBtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, speakerBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customSpeakerButton.setText(speakerBtnString);


        //액자 버튼 텍스트 설정
        String frame_firstLine = "액자";
        String frame_secondLine = "거실 1";

        SpannableString frameBtnString = new SpannableString(frame_firstLine + "\n\n" + frame_secondLine);

        frameBtnString.setSpan(new StyleSpan(Typeface.BOLD), 0, frame_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        frameBtnString.setSpan(new AbsoluteSizeSpan(20, true), 0, frame_firstLine.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        frameBtnString.setSpan(new StyleSpan(Typeface.NORMAL), frame_firstLine.length() + 2, frameBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        frameBtnString.setSpan(new AbsoluteSizeSpan(16, true), frame_firstLine.length() + 2, frameBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        frameBtnString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, frameBtnString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        customFrameButton.setText(frameBtnString);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 클릭 리스너 설정
        Button lightButton = view.findViewById(R.id.light_button);
        lightButton.setOnClickListener(v -> replaceFragment(new LightFragment()));
        Button windowButton = view.findViewById(R.id.window_button);
        windowButton.setOnClickListener(v -> replaceFragment(new WindowFragment()));
        Button airButton = view.findViewById(R.id.airPurifier_button);
        airButton.setOnClickListener(v -> replaceFragment(new AirFragment()));
        Button humButton = view.findViewById(R.id.humidifier_button);
        humButton.setOnClickListener(v -> replaceFragment(new HumFragment()));
        Button speakerButton = view.findViewById(R.id.speaker_button);
        speakerButton.setOnClickListener(v -> replaceFragment(new SpeakerFragment()));
        Button frameButton = view.findViewById(R.id.frame_button);
        frameButton.setOnClickListener(v -> replaceFragment(new FrameFragment()));
    }

    // 프래그먼트 교체 함수
    private void replaceFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}