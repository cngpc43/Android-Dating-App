package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class email_code_confirm extends AppCompatActivity {
    ImageView back_icon;
    MaterialButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_code_confirm);
        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();
        }

        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(email_code_confirm.this, account_settings.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.confirm_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(email_code_confirm.this, email_change.class);
                startActivity(intent);
            }
        });
    }
}
