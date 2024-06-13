package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class account_settings extends AppCompatActivity {
    ImageButton back_icon;
    LinearLayout password, email, phone_number;
    TextView email_preview, phone_preview;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        auth = FirebaseAuth.getInstance();

        back_icon = findViewById(R.id.back_icon);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);
        email_preview = findViewById(R.id.email_preview);
        phone_preview = findViewById(R.id.phone_preview);

        email_preview.setText(auth.getCurrentUser().getEmail());
        if (!auth.getCurrentUser().getPhoneNumber().equals(""))
            phone_preview.setText(auth.getCurrentUser().getPhoneNumber());

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_settings.this, password_change.class);
                startActivity(intent);
            }
        });


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_settings.this, email_change.class);
                startActivity(intent);
            }
        });

        phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser().getPhoneNumber().equals("")) {
                    Intent intent = new Intent(account_settings.this, phone_number_add.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(account_settings.this, phone_number_change.class);
                    startActivity(intent);
                }
            }
        });

    }
}