package com.example.mymessengerapp;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
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
    ValueEventListener onlineValueListener;
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    Toast.makeText(this, "You will not receive notifications", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
            onlineRef.addValueEventListener(onlineValueListener = new ValueEventListener() {
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

            // Get Firebase Cloud Messaging token for sending notifications
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("user/" + auth.getCurrentUser().getUid() + "/FCM_token").setValue(task.getResult());
                    } else {
                        Log.d("MainActivity", "FCM_token get failed: " + task.getException().getMessage());
                    }
                }
            });

            // request notification permission
            askNotificationPermission();

        } else {
            // No authenticated user handler -> LOGIN
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
            else if (getIntent().getStringExtra("fragment").equals("matching_requests"))
                selectMatchingRequest();
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



    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
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

    private void selectMatchingRequest() {
        if (loadFragment(new MatchingRequestsFragment(), "Notifications")) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onlineRef != null && onlineValueListener != null)
            onlineRef.removeEventListener(onlineValueListener);
    }



}