package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class password_change_success extends AppCompatActivity {
    TextView email_changed, description;
    MaterialButton log_in_button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change_success);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        email_changed = findViewById(R.id.email_changed);
        description = findViewById(R.id.description);
        log_in_button = findViewById(R.id.log_in_button);

        email_changed.setText("Password Updated");
        description.setText("Your password has been successfully changed. Please sign in again using your new password.");

        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(password_change_success.this, login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
