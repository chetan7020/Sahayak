package com.example.saftytracking.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.saftytracking.Dashboard;
import com.example.saftytracking.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Notifications extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Set up ViewPager adapter
        viewPager.setAdapter(new ViewPagerAdapter(this));

        ImageView notification = findViewById(R.id.notification_icon);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Notifications.this, Notifications.class);
                startActivity(intent);
            }
        });

        LinearLayout signl = findViewById(R.id.signup);
        signl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Notifications.this, SignLanguage.class);
                startActivity(intent);
            }
        });

        // Link TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("All");
                            break;
                        case 1:
                            tab.setText("Received");
                            break;
                        case 2:
                            tab.setText("Sent");
                            break;
                    }
                }).attach();
    }

    // Adapter to handle fragments for ViewPager
    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 3;  // 3 Tabs (All, Received, Sent)
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new AllFragment();  // Fragment for "All"
                case 1:
                    return new ReceivedFragment();  // Fragment for "Received"
                case 2:
                    return new SentFragment();  // Fragment for "Sent"
                default:
                    return new AllFragment();
            }
        }
    }
}