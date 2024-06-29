package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.adapter.ChatAdapter;
import com.example.mymessengerapp.model.ChatMessage;

import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    EditText messageInput;
    ImageButton sendMessBtn, backBtn, optMore;
    TextView userName, userStatus;
    CircleImageView userAvatar;
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
        setContentView(R.layout.activity_chat);
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String chatRoomId = senderId + "_" + receiverId;

        // Create chat room id from sender and receiver
        if (senderId.compareTo(receiverId) < 0) {
            chatRoomId = senderId + "_" + receiverId;
        } else {
            chatRoomId = receiverId + "_" + senderId;
        }

        DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatRoomId);
        chatRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                chatAdapter.notifyDataSetChanged();

                // Scroll the RecyclerView to the last item
                if (chatAdapter.getItemCount() > 0) {
                    mainChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error
                Log.w("ChatActivity", "Failed to read messages.", error.toException());
            }
        });

        // Get user id and chat room id
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
                    Users users = new Users(dataSnapshot.child("userId").getValue(String.class), dataSnapshot.child("userName").getValue(String.class),
                            dataSnapshot.child("mail").getValue(String.class), dataSnapshot.child("password").getValue(String.class),
                            dataSnapshot.child("profilepic").getValue(String.class), dataSnapshot.child("status").getValue(String.class),
                            dataSnapshot.child("gender").getValue(String.class), dataSnapshot.child("dob").getValue(String.class),
                            dataSnapshot.child("phone").getValue(String.class), dataSnapshot.child("location").getValue(String.class),
                            dataSnapshot.child("sexual_orientation").getValue(String.class), dataSnapshot.child("height").getValue(String.class),
                            dataSnapshot.child("age_range").getValue(String.class), dataSnapshot.child("gender_show").getValue(String.class),
                            dataSnapshot.child("show_me").getValue(Boolean.class), new ArrayList<String>(),
                            dataSnapshot.child("latitude").getValue(String.class), dataSnapshot.child("longitude").getValue(String.class),
                            "", dataSnapshot.child("location_distance").getValue(String.class));
                    Object isOnline = dataSnapshot.child("isOnline").getValue(Object.class);
                    if (isOnline != null) {
                        if (isOnline.equals("true"))
                            users.setIsOnline("true");
                        else
                            users.setIsOnline(isOnline.toString());
                    }
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
        optMore = findViewById(R.id.option_more);

        // Init chat message model
        chatMessages = new ArrayList<ChatMessage>();

        // Send message into the recycle view to display
        chatAdapter = new ChatAdapter(chatMessages);
        mainChat.setAdapter(chatAdapter);
        mainChat.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager)mainChat.getLayoutManager()).setStackFromEnd(true);

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
            if (chatAdapter.getItemCount() > 0) {
                mainChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

        // Init PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.attachment_popup, null);
        attachmentPopup = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Handle chat attachment icon in chat input field
        messageInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (messageInput.getRight() - messageInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Check popup window is showing are not
                    if (attachmentPopup.isShowing()) {
                        // Dismiss if the popup is showing
                        attachmentPopup.dismiss();
                        return true;
                    }

                    // Display it if it is not showing
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
        newVoiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(getIntent().getStringExtra("userId"), getIntent().getStringExtra("userName"))));

        // Handle option more button
        optMore.setOnClickListener(v -> showPopupMenu());

    }

    @Override
    public void onBackPressed() {
        RelativeLayout searchBarMsg = findViewById(R.id.searchBarMsg);

        // Check if searchBarMsg is visible
        if (searchBarMsg.getVisibility() == View.VISIBLE) {
            // If searchBarMsg is visible, hide it and return
            searchBarMsg.setVisibility(View.GONE);
            return;
        }

        // If searchBarMsg is not visible, call the superclass implementation
        super.onBackPressed();
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
        long timestamp = System.currentTimeMillis();

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

    // Show popup menu
    private void showPopupMenu() {
        // Init the popup menu and give the reference as current context
        PopupMenu optPopupMenu = new PopupMenu(ChatActivity.this, optMore);

        // Get the content menu
        optPopupMenu.getMenuInflater().inflate(R.menu.option_chat_popup_menu, optPopupMenu.getMenu());
        // Handle the item click in the popup menu
        optPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_msg: {
                        // Find the searchBarMsg view
                        RelativeLayout searchBarMsg = findViewById(R.id.searchBarMsg);
                        // Set its visibility to VISIBLE
                        searchBarMsg.setVisibility(View.VISIBLE);
                        return true;
                    }
                    default:
                        return true;
                }
            }
        });

        // Show popup menu
        optPopupMenu.show();
    }
}



