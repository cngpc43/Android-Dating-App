package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class notification_page extends AppCompatActivity {

    ImageView home;
    FrameLayout user, message;
    RecyclerView noti_list;
    LinearLayout noti_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        noti_selected = findViewById(R.id.noti_selected);
        noti_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));
        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notification_page.this, MainActivity.class);
                startActivity(intent);
            }
        });
        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notification_page.this, ChatHomePage.class);
                startActivity(intent);
            }
        });

        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notification_page.this, setting.class);
                startActivity(intent);

            }
        });
    }
}
