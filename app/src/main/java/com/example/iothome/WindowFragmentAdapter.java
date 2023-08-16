package com.example.iothome;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WindowFragmentAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public WindowFragmentAdapter(Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WindowLivingRoomFragment();
            case 1:
                return new WindowRoomFragment();
            default:
                throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
