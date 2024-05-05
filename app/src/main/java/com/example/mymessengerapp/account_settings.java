package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class account_settings extends AppCompatActivity {
    ImageButton back_icon;
    LinearLayout password, email, phone_number;
    TextView email_preview, phone_preview;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());

        back_icon = findViewById(R.id.back_icon);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);
        email_preview = findViewById(R.id.email_preview);
        phone_preview = findViewById(R.id.phone_preview);

        email_preview.setText(auth.getCurrentUser().getEmail());
        if (auth.getCurrentUser().getPhoneNumber() == null)
            phone_preview.setVisibility(View.GONE);
        else
            phone_preview.setText(auth.getCurrentUser().getPhoneNumber());

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_settings.this, user_manage.class);
                startActivity(intent);
                finish();
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_settings.this, password_change.class);
                startActivity(intent);
                finish();
            }
        });


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_settings.this, email_change.class);
                startActivity(intent);
                finish();
            }
        });

        phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser().getPhoneNumber() == null) {
                    Intent intent = new Intent(account_settings.this, phone_number_add.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(account_settings.this, phone_number_change.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}