package com.example.mymessengerapp;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.mymessengerapp.adapter.ChatAdapter;
import com.example.mymessengerapp.adapter.ImagePagerAdapter;
import com.example.mymessengerapp.model.ChatMessage;

import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.app.AlertDialog;
//import Manifest
import android.Manifest;

public class ChatActivity extends AppCompatActivity {
    ImageButton voiceAttach;
    private static final int GALLERY_REQUEST_CODE = 123;
    EditText messageInput;
    ImageButton sendMessBtn, backBtn, optMore;
    TextView userName, userStatus;
    CircleImageView userAvatar;
    RecyclerView mainChat;
    ArrayList<Users> usersArrayList;
    ChatAdapter chatAdapter;
    List<ChatMessage> chatMessages;
    LinearLayout llSendChat, attachmentPopup;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ImageButton chatInput;
    private MediaRecorder recorder;
    private StorageReference storageReference;
    private String audioFilePath;
    private boolean isRecording = false;
    boolean isRecordingStarted = false;
    private ArrayList<Uri> selectedMediaUris = new ArrayList<>();
    private static final int PICK_MEDIA_REQUEST = 1;
    private String receiverToken, senderName, chatRoomId;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "ResourceAsColor", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        storageReference = FirebaseStorage.getInstance().getReference();
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverId = getIntent().getStringExtra("userId");
        chatRoomId = getIntent().getStringExtra("roomId");
        // Get receiver token for sending notification
        FirebaseDatabase.getInstance().getReference().child("user/" + receiverId + "/FCM_token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) != null)
                    receiverToken = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get sender name for sending notification
        FirebaseDatabase.getInstance().getReference().child("user/" + senderId + "/userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) != null)
                    senderName = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    String attachmentUrl = messageSnapshot.child("attachmentUrl").getValue(String.class);
                    String attachmentType = messageSnapshot.child("attachmentType").getValue(String.class);
                    // Create a new ChatMessage object
                    ChatMessage chatMessage = new ChatMessage(message, timestamp, senderId, true, attachmentUrl, attachmentType);
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
        voiceAttach = findViewById(R.id.voice_attach);

        // username marquee
        userName.setSelected(true);

        // Init chat message model
        chatMessages = new ArrayList<ChatMessage>();

        // Send message into the recycle view to display
        chatAdapter = new ChatAdapter(chatMessages, this);
        mainChat.setAdapter(chatAdapter);
        mainChat.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager) mainChat.getLayoutManager()).setStackFromEnd(true);

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

            handleSendMessage(message, receiverId, null, "text");

            // Clear the input field
            messageInput.setText("");

            // Add the new message to the chatMessages list
            long timestamp = System.currentTimeMillis();
            chatMessages.add(new ChatMessage(message, timestamp, senderId, true, null, "text"));

            // Notify the adapter that the data set has changed
            chatAdapter.notifyDataSetChanged();

            // Scroll the RecyclerView to the last item
            if (chatAdapter.getItemCount() > 0) {
                mainChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

        // Handle image attachment icon in chat input field
        messageInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (messageInput.getRight() - messageInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    String[] mimetypes = {"image/*", "video/*"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_MEDIA_REQUEST);

                    return true;
                }
            }

            return false;
        });

        voiceAttach.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri mediaUri = clipData.getItemAt(i).getUri();
                    selectedMediaUris.add(mediaUri);
                }
                uploadFilesToFirebase(selectedMediaUris);
            } else if (data.getData() != null) {
                Uri mediaUri = data.getData();
                selectedMediaUris.add(mediaUri);
                // Show preview for single selected item
                if (selectedMediaUris.size() == 1) {
                    showImagePreviewDialog(mediaUri);
                } else {
                    uploadFilesToFirebase(selectedMediaUris);
                    selectedMediaUris.clear();
                }
            }
        }
    }

    private void uploadFilesToFirebase(ArrayList<Uri> selectedMediaUris) {
        for (Uri mediaUri : selectedMediaUris) {
            String mimeType = getContentResolver().getType(mediaUri);
            StorageReference fileRef = storageReference.child("files/" + mediaUri.getLastPathSegment());
            UploadTask uploadTask = fileRef.putFile(mediaUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileUrl = uri.toString();
                    String receiverId = getIntent().getStringExtra("userId");
                    if (mimeType.startsWith("image/")) {
                        handleSendMessage("Image", receiverId, fileUrl, "media");
                    } else if (mimeType.startsWith("video/")) {
                        handleSendMessage("Video", receiverId, fileUrl, "video");
                    }
                    selectedMediaUris.clear();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ChatActivity.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
            });
        }
        selectedMediaUris.clear();
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
    public void showImagePreviewDialog(Uri selectedMediaUri) {
        // Create a dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_preview);

        // Find the ViewPager and Buttons in the layout
        ViewPager mediaPreviewPager = dialog.findViewById(R.id.image_preview_pager);
        Button confirmButton = dialog.findViewById(R.id.confirm_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);

        // Create an ArrayList and add the single Uri to it
        ArrayList<Uri> mediaUris = new ArrayList<>();
        mediaUris.add(selectedMediaUri);

        // Set the adapter for the ViewPager
        mediaPreviewPager.setAdapter(new MediaPagerAdapter(this, mediaUris));

        confirmButton.setOnClickListener(v -> {
            String mimeType = getContentResolver().getType(selectedMediaUri);
            StorageReference fileRef = storageReference.child("files/" + selectedMediaUri.getLastPathSegment());
            UploadTask uploadTask = fileRef.putFile(selectedMediaUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileUrl = uri.toString();
                    String receiverId = getIntent().getStringExtra("userId");
                    if (mimeType.startsWith("image/")) {
                        handleSendMessage("Image", receiverId, fileUrl, "media");
                    } else if (mimeType.startsWith("video/")) {
                        handleSendMessage("Video", receiverId, fileUrl, "video");
                    }
                    // Dismiss the dialog
                    dialog.dismiss();
                    selectedMediaUris.clear();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ChatActivity.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
            });

            // Dismiss the dialog
            dialog.dismiss();
        });
        cancelButton.setOnClickListener(v -> {
            mediaUris.clear();
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    void handleSendMessage(String message, String receiverId, String attachmentUrl, String
            attachmentType) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Create a new message object
        HashMap<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("senderId", senderId);
        chatMessage.put("timestamp", timestamp);
        chatMessage.put("text", message);
        chatMessage.put("attachmentUrl", attachmentUrl);
        chatMessage.put("attachmentType", attachmentType);
        // Push this message object to the Firebase database under the chat room ID
        FirebaseDatabase.getInstance().getReference("Chats")
                .child(chatRoomId)
                .push()
                .setValue(chatMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Message sent successfully
                        Log.d("ChatActivity", "Message sent.");
                        // Push notification to receiver
                        sendNotification(message);
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
                        RelativeLayout searchBarMsg = findViewById(R.id.searchBarMsg);
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

    private void startRecording() {
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audiorecord.3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(audioFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e) {
            Toast.makeText(ChatActivity.this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(ChatActivity.this, "Recorder is not properly prepared", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (isRecording && recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                uploadAudioFile();
            } catch (RuntimeException e) {
                Toast.makeText(this, "Failed to stop recording", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "stopRecording: ", e);
            }
        }
    }

    private void uploadAudioFile() {
        Uri audioFileUri = Uri.fromFile(new File(audioFilePath));
        StorageReference audioRef = storageReference.child("audio/" + audioFileUri.getLastPathSegment());
        UploadTask uploadTask = audioRef.putFile(audioFileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String audioUrl = uri.toString();
                String receiverId = getIntent().getStringExtra("userId");
                handleSendMessage("Voice", receiverId, audioUrl, "audio");
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ChatActivity.this, "Failed to upload audio file", Toast.LENGTH_SHORT).show();
        });
    }

    public void sendNotification(String message) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SendNotifications notificationSender = new SendNotifications(receiverToken, senderName, message, ChatActivity.this);
                notificationSender.Send();
            }
        }, 300);
    }
}

