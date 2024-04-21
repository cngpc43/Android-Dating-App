package com.example.mymessengerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class setting  extends AppCompatActivity {
    LinearLayout logout;
    LinearLayout account_settings, location;
    FirebaseAuth auth;
    TextView username;
    TextView status;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ImageView home, chat;
    RangeSlider age_range;
    TextView age_range_preview, location_preview;
    FrameLayout user, message, notification;
    Spinner gender_spinner, sexual_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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

//                password = snapshot.child("password").getValue().toString();
//                String name = snapshot.child("userName").getValue().toString();
//                String profile = snapshot.child("profilepic").getValue().toString();
//                String status = snapshot.child("status").getValue().toString();
//                setname.setText(name);
//                setstatus.setText(status);
//                Picasso.get().load(profile).into(setprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(setting.this, R.style.dialogue);
                dialog.setContentView(R.layout.dialogue_layout);
                Button no, yes;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 0, 0, 0)));

                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(setting.this, login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setting.this, MainActivity.class);
                startActivity(intent);
            }
        });
        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setting.this, ChatHomePage.class);
                startActivity(intent);
            }
        });

        notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setting.this, notification_page.class);
                startActivity(intent);
            }
        });
        age_range_preview = findViewById(R.id.age_range_preview);
        age_range = findViewById(R.id.age_range_slider);
        age_range.setValues(13F, 24F);
        age_range_preview.setText(String.valueOf(age_range.getValues().get(0).intValue()) + "-" + String.valueOf(age_range.getValues().get(1).intValue()));

        age_range.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider rangeSlider, float v, boolean b) {
                List<Float> age_range_list = age_range.getValues();
                age_range_preview.setText(String.valueOf(age_range.getValues().get(0).intValue()) + "-" + String.valueOf(age_range.getValues().get(1).intValue()));
            }
        });

        account_settings = findViewById(R.id.account_settings);
        account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setting.this, account_settings.class);
                startActivity(intent);
            }

        });
        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setting.this, chat_home_page.class);
                startActivity(intent);
            }
        });
        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setting.this, setting.class);
                startActivity(intent);
            }
        });

        gender_spinner = findViewById(R.id.gender_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, R.layout.gender_spinner_item);
        adapter.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        gender_spinner.setAdapter(adapter);

        sexual_spinner = findViewById(R.id.sexual_spinner);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.sexual_array, R.layout.gender_spinner_item);
        adapter1.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        sexual_spinner.setAdapter(adapter1);

        location = findViewById(R.id.location);
        location_preview = findViewById(R.id.location_preview);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent intent = new Intent(setting.this, location_change.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
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
                // Write your code if there's no result
            }
        }
    } //onActivityResult
}