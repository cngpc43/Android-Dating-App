package com.example.mymessengerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class CallActivity extends AppCompatActivity {
    private String callType;

    private String userId;

    private String userName;

    private String userAvatar;

    public CallActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        // Get data from intent
        callType = getIntent().getStringExtra("callType");
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        userAvatar = getIntent().getStringExtra("userAvatar");

        // DOM elements
        TextView targetNameTV = findViewById(R.id.targetName);
        ImageView targetAvatarIV = findViewById(R.id.targetAvatar);
        ImageView BTEndCall = findViewById(R.id.end_call_button);
        ImageView BTMute = findViewById(R.id.mic_button);
        ImageView BTVideo = findViewById(R.id.video_button);
        ImageView BTSwitchCamera = findViewById(R.id.switch_camera_button);

        // Set content to UI
        targetNameTV.setText(userName);
        if (userAvatar != null) {
            Picasso.get().load(userAvatar).into(targetAvatarIV);
            targetAvatarIV.setVisibility(View.VISIBLE);
        } else {
            targetAvatarIV.setVisibility(View.GONE);
        }

        if (callType.equals("video")) {
            BTVideo.setVisibility(View.VISIBLE);
            BTSwitchCamera.setVisibility(View.VISIBLE);
        } else {
            BTVideo.setVisibility(View.GONE);
            BTSwitchCamera.setVisibility(View.GONE);
        }

        // Call buttons click listeners
        BTEndCall.setOnClickListener(v -> {
            // @TODO: Send request to end call
            finish();
        });

        BTMute.setOnClickListener(v -> {

        });

    }
}
