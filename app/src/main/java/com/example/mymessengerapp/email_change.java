package com.example.mymessengerapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class email_change extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();
        }

    }
}
