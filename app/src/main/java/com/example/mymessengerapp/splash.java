package com.example.mymessengerapp;

import static java.security.AccessController.getContext;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;


public class splash extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        // handle user click on notifications from outside of the app
        if (FirebaseAuth.getInstance().getCurrentUser() != null && getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("type") != null) {
                // first put Main Activity into stack
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                // if chat notification
                if (getIntent().getStringExtra("type").equals("chat")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // get userName and userImage for Chat Activity
                            FirebaseDatabase.getInstance().getReference().child("user/" + getIntent().getStringExtra("userId")).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    // go to Chat Fragment
                                    mainIntent.putExtra("fragment", "message");
                                    startActivity(mainIntent);

                                    // go to the Chat Room
                                    Intent intent = new Intent(splash.this, ChatActivity.class);
                                    intent.putExtra("userId", getIntent().getStringExtra("userId"));
                                    intent.putExtra("userName", dataSnapshot.child("userName").getValue(String.class));
                                    intent.putExtra("userImage", dataSnapshot.child("profilepic").getValue(String.class));
                                    intent.putExtra("roomId", getIntent().getStringExtra("chatId"));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }, 1000);
                }

                // if send request notification
                else if (getIntent().getStringExtra("type").equals("request_send")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // get userName and userImage for Chat Activity
                            FirebaseDatabase.getInstance().getReference().child("user/" + getIntent().getStringExtra("userId")).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot1) {
                                    // set the status of notification to seen if user press on the notification
                                    FirebaseDatabase.getInstance().getReference().child("MatchRequests").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("requesterId").getValue(String.class).equals(getIntent().getStringExtra("userId"))
                                                    && snapshot.child("recipientId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    FirebaseDatabase.getInstance().getReference("MatchRequests/" + snapshot.getKey() + "/recipient_status").setValue("seen");
                                                    break;
                                                }
                                            }
                                            // go to Matching Requests Fragment
                                            mainIntent.putExtra("fragment", "matching_requests");
                                            startActivity(mainIntent);

                                            // go to Request Sender Profile
                                            Intent intent = new Intent(splash.this, ViewAnotherProfile.class);
                                            intent.putExtra("userId", getIntent().getStringExtra("userId"));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }, 1000);
                }

                // if accept request notification
                if (getIntent().getStringExtra("type").equals("request_accept")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // get userName and userImage for Chat Activity
                            FirebaseDatabase.getInstance().getReference().child("user/" + getIntent().getStringExtra("userId")).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot1) {
                                    // set the status of notification to seen if user press on the notification
                                    FirebaseDatabase.getInstance().getReference().child("MatchRequests").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("requesterId").getValue(String.class).equals(getIntent().getStringExtra("userId"))
                                                        && snapshot.child("recipientId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        || snapshot.child("requesterId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        && snapshot.child("recipientId").getValue(String.class).equals(getIntent().getStringExtra("userId"))) {
                                                    if (snapshot.child("recipientId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                                        FirebaseDatabase.getInstance().getReference("MatchRequests/" + snapshot.getKey() + "/requester_status").setValue("seen");
                                                    else
                                                        FirebaseDatabase.getInstance().getReference("MatchRequests/" + snapshot.getKey() + "/recipient_status").setValue("seen");
                                                    break;
                                                }
                                            }
                                            // go to Chat Fragment
                                            mainIntent.putExtra("fragment", "message");
                                            startActivity(mainIntent);

                                            // go to the Chat Room
                                            Intent intent = new Intent(splash.this, ChatActivity.class);
                                            intent.putExtra("userId", getIntent().getStringExtra("userId"));
                                            intent.putExtra("userName", dataSnapshot1.child("userName").getValue(String.class));
                                            intent.putExtra("userImage", dataSnapshot1.child("profilepic").getValue(String.class));
                                            intent.putExtra("roomId", getIntent().getStringExtra("chatId"));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }, 1000);
                }
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }
}