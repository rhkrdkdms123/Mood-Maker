package com.example.iothome;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(getResources().getColorStateList(R.color.bottom_nav_icon_item_color));
        bottomNavigationView.setItemTextColor(getResources().getColorStateList(R.color.bottom_nav_text_item_color));
        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_item_background);

        getSupportFragmentManager().beginTransaction().add(R.id.frame, new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                } else if (itemId == R.id.menu_mood) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MoodFragment()).commit();
                } else if (itemId == R.id.menu_schedule) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ScheduleFragment()).commit();
                } else if (itemId == R.id.menu_setting) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SettingFragment()).commit();
                }
                return true;
            }
        });
    }

}