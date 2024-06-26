package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.adapter.ChatAdapter;
import com.example.mymessengerapp.model.ChatMessage;

import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    EditText messageInput;
    ImageButton sendMessBtn, backBtn, audioCall;
    TextView userName, userStatus;
    ShapeableImageView userAvatar;
    RecyclerView mainChat;
    ArrayList<Users> usersArrayList;
    ChatAdapter chatAdapter;
    List<ChatMessage> chatMessages;
    LinearLayout llSendChat;
    FirebaseDatabase database;

    FirebaseAuth auth;

    PopupWindow attachmentPopup;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String chatRoomId = senderId + "_" + receiverId;
        if (senderId.compareTo(receiverId) < 0) {
            chatRoomId = senderId + "_" + receiverId;
        } else {
            chatRoomId = receiverId + "_" + senderId;
        }
        DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatRoomId);
        chatRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the chatMessages list
                chatMessages.clear();

                // Loop through the messages in the chat room
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    // Get the message data
                    String message = messageSnapshot.child("text").getValue(String.class);
                    long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                    String senderId = messageSnapshot.child("senderId").getValue(String.class);

                    // Create a new ChatMessage object
                    ChatMessage chatMessage = new ChatMessage(message, timestamp, senderId, true);
                    Log.d("ChatActivity", "Message: " + message + ", Timestamp: " + timestamp + ", Sender ID: " + senderId);

                    // Add the chat message to the chatMessages list
                    chatMessages.add(chatMessage);
                }

                // Notify the adapter that the data set has changed
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error
                Log.w("ChatActivity", "Failed to read messages.", error.toException());
            }
        });
        // lấy ID của users và chatroom
        usersArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                String currentUserId = auth.getCurrentUser().getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users != null && !users.getUserId().equals(currentUserId)) {
                        usersArrayList.add(users);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Get UI from xml
        userName = findViewById(R.id.user_name_chat);
        userStatus = findViewById(R.id.user_status_chat);
        userAvatar = findViewById(R.id.user_icon_chat);
        messageInput = findViewById(R.id.chat_input);
        sendMessBtn = findViewById(R.id.chat_send_button);
        backBtn = findViewById(R.id.chat_back_button);
        mainChat = findViewById(R.id.chat_main);
        llSendChat = findViewById(R.id.send_chat);

        // Khoi tao chat message - day la hard data, thay bang firebase sau
        chatMessages = new ArrayList<ChatMessage>();

        // Gui chat message vo recyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        mainChat.setAdapter(chatAdapter);
        mainChat.setLayoutManager(new LinearLayoutManager(this));

        // Set content for UI
        userName.setText(getIntent().getStringExtra("userName"));
        String profileUri = getIntent().getStringExtra("userImage");
        if (profileUri == null) {
            FirebaseStorage.getInstance().getReference().child("default.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(userAvatar);
                }
            });
        } else {
            Picasso.get().load(profileUri).into(userAvatar);
        }

        // Xu ly khi nhan back ve
        backBtn.setOnClickListener(v -> onBackPressed());
        sendMessBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();

            // Don't proceed if the message is empty
            if (message.isEmpty())
                return;

            handleSendMessage(message, receiverId);

            // Clear the input field
            messageInput.setText("");

            // Add the new message to the chatMessages list
            long timestamp = System.currentTimeMillis();
            chatMessages.add(new ChatMessage(message, timestamp, senderId, true));

            // Notify the adapter that the data set has changed
            chatAdapter.notifyDataSetChanged();

            // Scroll the RecyclerView to the last item
            mainChat.scrollToPosition(chatMessages.size() - 1);
        });

        // Init PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.attachment_popup, null);
        attachmentPopup = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Xu ly khi nhan vao icon attachment trong chat_input
        messageInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (messageInput.getRight() - messageInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Kiem tra popup co shoing hay k
                    if (attachmentPopup.isShowing()) {
                        // Neu dang show thi huy no di
                        attachmentPopup.dismiss();
                        return true;
                    }

                    // Neu k show thi hien no o tren chat_input view
                    attachmentPopup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT); // Set width to match parent
                    attachmentPopup.showAtLocation(messageInput, Gravity.NO_GRAVITY, 0, mainChat.getBottom() - 100); // Show at top of chat_input
                    return true;
                }
            }
            return false;
        });

        // Zego call listener
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
        newVideoCall.setIsVideoCall(true);
        newVideoCall.setResourceID("zego_uikit_call");
        newVideoCall.setBackgroundResource(R.drawable.icons8_video_call_36);
        newVideoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(getIntent().getStringExtra("userId"), getIntent().getStringExtra("userName"))));

        // Audio call handler
        ZegoSendCallInvitationButton newVoiceCall = findViewById(R.id.new_voice_call);
        newVoiceCall.setIsVideoCall(false);
        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.setBackgroundResource(R.drawable.icons8_call_36);
        newVoiceCall.setOnClickListener(v -> {
            String targetUserID = getIntent().getStringExtra("userName");
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });
    }

    // Xu ly chuc nang gui tin nhan
    void handleSendMessage(String message, String receiverId) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Concatenate the user IDs to form a unique chat room ID
        String chatRoomId = senderId + "_" + receiverId;
        if (senderId.compareTo(receiverId) < 0) {
            chatRoomId = senderId + "_" + receiverId;
        } else {
            chatRoomId = receiverId + "_" + senderId;
        }
        // Get the current timestamp
        long timestamp = System.currentTimeMillis() / 1000;

        // Create a new message object
        HashMap<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("senderId", senderId);
        chatMessage.put("timestamp", timestamp);
        chatMessage.put("text", message);

        // Push this message object to the Firebase database under the chat room ID
        FirebaseDatabase.getInstance().getReference("Chats")
                .child(chatRoomId)
                .push()
                .setValue(chatMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Message sent successfully
                        Log.d("ChatActivity", "Message sent.");
                    } else {
                        // Failed to send the message
                        Log.w("ChatActivity", "Failed to send message.", task.getException());
                    }
                });
    }
}



