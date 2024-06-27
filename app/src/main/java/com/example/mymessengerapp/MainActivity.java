package com.example.mymessengerapp;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mymessengerapp.adapter.UserAdapter;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    FrameLayout home, user, message, notification;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;
    MaterialTextView title;
    Boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = findViewById(R.id.user);
        message = findViewById(R.id.message);
        home = findViewById(R.id.home);
        notification = findViewById(R.id.notification);
        home_selected = findViewById(R.id.home_selected);
        noti_selected = findViewById(R.id.noti_selected);
        chat_selected = findViewById(R.id.chat_selected);
        user_selected = findViewById(R.id.user_selected);
        title = findViewById(R.id.title);

        Application app = getApplication();
        long appId = Long.parseLong(getString(R.string.app_id));
        String appSign = getString(R.string.app_sign);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Check the user
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userNameRef = databaseReference.child("user").child(currentUser.getUid()).child("userName");
            userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.getValue(String.class);
                    Log.d("MainActivity", "userName: " + userName);
                    ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
                    ZegoUIKitPrebuiltCallService.init(getApplication(), appId, appSign, userId, userName, callInvitationConfig);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("MainActivity", "Failed to read username.", databaseError.toException());
                }
            });

            // No authenticated user handler -> LOGIN
        } else {
            Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
        }

        // Bottom navigation
        if (getIntent() == null || getIntent().getStringExtra("fragment") == null) {
            selectHome();
        } else {
            if (getIntent().getStringExtra("fragment").equals("user"))
                selectUser();
            else if (getIntent().getStringExtra("fragment").equals("message"))
                selectMessage();
            else if (getIntent().getStringExtra("fragment").equals("notification"))
                selectNotification();
            else
                selectHome();
        }


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUser();
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMessage();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectHome();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNotification();
            }
        });

        // back press two times within 2 seconds to exit app
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Please swipe BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    }

    private void selectHome() {
        loadFragment(new MainFragment());
        title.setText("Tindeo");
        noti_selected.setBackground(null);
        chat_selected.setBackground(null);
        user_selected.setBackground(null);
        home_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    private void selectMessage() {
        loadFragment(new ChatFragment());
        title.setText("Chat");
        home_selected.setBackground(null);
        noti_selected.setBackground(null);
        user_selected.setBackground(null);
        chat_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    private void selectNotification() {
        loadFragment(new NotificationFragment(MainActivity.this));
        title.setText("Notifications");
        home_selected.setBackground(null);
        chat_selected.setBackground(null);
        user_selected.setBackground(null);
        noti_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    private void selectUser() {
        loadFragment(new UserSettingFragment());
        title.setText("User");
        home_selected.setBackground(null);
        noti_selected.setBackground(null);
        chat_selected.setBackground(null);
        user_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

}