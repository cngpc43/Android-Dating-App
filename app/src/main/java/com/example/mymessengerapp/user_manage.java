package com.example.mymessengerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class user_manage extends AppCompatActivity {
    LinearLayout account_settings, location;
    FirebaseAuth auth;
    TextView username;
    TextView status;
    FirebaseDatabase database;
    FirebaseStorage storage;
    TextView age_range_preview, location_preview;

    ImageView home, chat;
    FrameLayout user, message, notification;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        UserSettingFragment userSettingFragment = new UserSettingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.setting_frame, userSettingFragment);
        transaction.commit();
        username = findViewById(R.id.profile_username);
        status = findViewById(R.id.profile_status);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                location_preview.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }
}