package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class username_change extends AppCompatActivity {
    ImageView back_icon;
    TextView email, title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        email = findViewById(R.id.email);
        email.setText("Enter your new username:");
        title = findViewById(R.id.title);
        title.setText("Change username");

        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(username_change.this, account_settings.class);
                startActivity(intent);
            }
        });


    }
}
