package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class account_settings extends AppCompatActivity {
    ImageView back_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_settings.this, setting.class);
                startActivity(intent);
            }
        });
    }
}
