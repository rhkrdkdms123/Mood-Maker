package com.example.iothome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindowFragment extends Fragment {
    WindowFragmentAdapter windowFragmentAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_window, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.window_tab_layout);
        windowFragmentAdapter = new WindowFragmentAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.window_viewpager);

        viewPager.setAdapter(windowFragmentAdapter);

        final List<String> tabTitles = Arrays.asList("거실","방");

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String t = tabTitles.get(position);
            tab.setText(t);
        }
        ).attach();

    }
}