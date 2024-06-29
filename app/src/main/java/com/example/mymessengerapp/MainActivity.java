package com.example.mymessengerapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference onlineRef, userRef;
    FrameLayout home, user, message, notification;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;
    ImageView ic_home, ic_chat, ic_noti, ic_user;
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

        long appId = Long.parseLong(getString(R.string.app_id));
        String appSign = getString(R.string.app_sign);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        FirebaseUser currentUser = auth.getCurrentUser();

        user = findViewById(R.id.user);
        message = findViewById(R.id.message);
        home = findViewById(R.id.home);
        notification = findViewById(R.id.notification);
        home_selected = findViewById(R.id.home_selected);
        noti_selected = findViewById(R.id.noti_selected);
        chat_selected = findViewById(R.id.chat_selected);
        user_selected = findViewById(R.id.user_selected);
        ic_home = findViewById(R.id.icon_home);
        ic_chat = findViewById(R.id.icon_chat);
        ic_noti = findViewById(R.id.icon_noti);
        ic_user = findViewById(R.id.icon_user);
        title = findViewById(R.id.title);

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

            // check for online status
            onlineRef = database.getReference().child(".info/connected");
            userRef = database.getReference().child("user/" + auth.getCurrentUser().getUid() + "/isOnline");
            onlineRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean connected = snapshot.getValue(Boolean.class);
                    if (connected != null && connected) {
                        userRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                        userRef.setValue("true");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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
                selectChat();
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
                selectChat();
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
                if (title.getText().toString().equals("Tindeo")) {
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
                } else {
                    selectHome();
                }
            }
        });
    }

    private boolean loadFragment(Fragment fragment, String title) {
        if (!this.title.getText().toString().equals(title)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, fragment);
            transaction.commit();
            return true;
        }
        return false;
    }

    private void selectHome() {
        if (loadFragment(new MainFragment(), "Tindeo")) {
            title.setText("Tindeo");

            noti_selected.setBackground(null);
            chat_selected.setBackground(null);
            user_selected.setBackground(null);
            home_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));

            ic_chat.setColorFilter(Color.BLACK);
            ic_noti.setColorFilter(Color.BLACK);
            ic_user.setColorFilter(Color.BLACK);
            ic_home.setColorFilter(Color.rgb(236, 83, 131));
        }
    }

    private void selectChat() {
        if (loadFragment(new ChatFragment(), "Chat")) {
            title.setText("Chat");

            home_selected.setBackground(null);
            noti_selected.setBackground(null);
            user_selected.setBackground(null);
            chat_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));

            ic_home.setColorFilter(Color.BLACK);
            ic_noti.setColorFilter(Color.BLACK);
            ic_user.setColorFilter(Color.BLACK);
            ic_chat.setColorFilter(Color.rgb(236, 83, 131));
        }
    }

    private void selectNotification() {
        if (loadFragment(new NotificationFragment(this), "Notifications")) {
            title.setText("Notifications");

            home_selected.setBackground(null);
            chat_selected.setBackground(null);
            user_selected.setBackground(null);
            noti_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));

            ic_home.setColorFilter(Color.BLACK);
            ic_chat.setColorFilter(Color.BLACK);
            ic_user.setColorFilter(Color.BLACK);
            ic_noti.setColorFilter(Color.rgb(236, 83, 131));
        }
    }

    private void selectUser() {
        if (loadFragment(new UserSettingFragment(), "Your profile")) {
            title.setText("Your profile");

            home_selected.setBackground(null);
            noti_selected.setBackground(null);
            chat_selected.setBackground(null);
            user_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));

            ic_home.setColorFilter(Color.BLACK);
            ic_chat.setColorFilter(Color.BLACK);
            ic_noti.setColorFilter(Color.BLACK);
            ic_user.setColorFilter(Color.rgb(236, 83, 131));
        }
    }

}