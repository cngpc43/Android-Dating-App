package com.example.mymessengerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class VideoCallActivity extends AppCompatActivity {

    private EditText receiverUserId;
    private TextView textView;
    private ZegoSendCallInvitationButton videoCallBtn;
    private ZegoSendCallInvitationButton audioCallBtn;
    private LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        receiverUserId = findViewById(R.id.receiver_user_id_text_field);
        textView = findViewById(R.id.user_id_text_view);
        videoCallBtn = findViewById(R.id.video_call_btn);
        audioCallBtn = findViewById(R.id.audio_call_btn);
        buttonLayout = findViewById(R.id.buttons_layout);

        String userId = getIntent().getStringExtra("userID");
        textView.setText("Hi " + userId + "!");

        receiverUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not yet implemented
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String _receiverId = receiverUserId.getText().toString();
                if (!_receiverId.isEmpty()) {
                    buttonLayout.setVisibility(View.VISIBLE);
                    startVideoCall(_receiverId);
                    startAudioCall(_receiverId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not yet implemented
            }
        });
    }

    private void startVideoCall(String receiverId) {
        videoCallBtn.setIsVideoCall(true);
        videoCallBtn.setResourceID("zego_uikit_call");
        videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(receiverId)));
    }

    private void startAudioCall(String receiverId) {
        audioCallBtn.setIsVideoCall(false);
        audioCallBtn.setResourceID("zego_uikit_call");
        audioCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(receiverId)));
    }
}
