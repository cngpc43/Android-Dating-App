package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymessengerapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class email_change_success extends AppCompatActivity {
    MaterialButton log_in_button;
    ImageView image;
    TextView email_changed, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change_success);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        log_in_button = (MaterialButton)findViewById(R.id.log_in_button);
        image = (ImageView)findViewById(R.id.ic_email);
        email_changed = (TextView)findViewById(R.id.email_changed);
        description = (TextView)findViewById(R.id.description);

        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(email_change_success.this, login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
