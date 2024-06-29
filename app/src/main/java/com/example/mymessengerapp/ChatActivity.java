package com.example.mymessengerapp;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
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
        LinearLayout llSendChat;
        FirebaseDatabase database;
        FirebaseAuth auth;

        PopupWindow attachmentPopup;
        ImageButton chatInput;
        private MediaRecorder recorder;
        private String fileName;
        private StorageReference storageReference;
        private String audioFilePath;
        private boolean isRecording = false;
        boolean isRecordingStarted = false;
        private ArrayList<Uri> selectedMediaUris = new ArrayList<>();
        private static final int PICK_MEDIA_REQUEST = 1;

        @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "ResourceAsColor"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);
            storageReference = FirebaseStorage.getInstance().getReference();
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


            // Init chat message model
            chatMessages = new ArrayList<ChatMessage>();

            // Send message into the recycle view to display
            chatAdapter = new ChatAdapter(chatMessages);
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

            // Init PopupWindow
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.attachment_popup, null);
            attachmentPopup = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            chatInput = popupView.findViewById(R.id.image_attachment);
            Log.d("ChatActivity", "Chat input: " + chatInput);
            chatInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    String[] mimetypes = {"image/*", "video/*"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_MEDIA_REQUEST);
                }
            });

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
                        attachmentPopup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                        attachmentPopup.showAtLocation(messageInput, Gravity.NO_GRAVITY, 0, mainChat.getBottom() - 100);
                        return true;
                    }
                }
                return false;
            });

            voiceAttach.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isRecordingStarted == false) {
                        startRecording();
                        isRecordingStarted = true;
                    } else {
                        stopRecording();
                        isRecordingStarted = false;
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
                    // Add this URI to a list of selected media
                    selectedMediaUris.add(mediaUri);
                }
            } else if (data.getData() != null) {
                Uri mediaUri = data.getData();
                // Add this URI to a list of selected media
                selectedMediaUris.add(mediaUri);
            }
            // Now open the confirmation dialog/activity, passing the list of selected media
            showImagePreviewDialog(selectedMediaUris);
        }
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
        public void showImagePreviewDialog(ArrayList<Uri> selectedMediaUris) {
            // Create a dialog
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_image_preview);

            // Find the ViewPager and Buttons in the layout
            ViewPager imagePreviewPager = dialog.findViewById(R.id.image_preview_pager);
            Button confirmButton = dialog.findViewById(R.id.confirm_button);
            Button cancelButton = dialog.findViewById(R.id.cancel_button);

            // Set the adapter for the ViewPager
            imagePreviewPager.setAdapter(new ImagePagerAdapter(this, selectedMediaUris));

            confirmButton.setOnClickListener(v -> {
                // Handle the confirmation action here
                for (Uri mediaUri : selectedMediaUris) {
                    StorageReference fileRef = storageReference.child("files/" + mediaUri.getLastPathSegment());
                    UploadTask uploadTask = fileRef.putFile(mediaUri);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileUrl = uri.toString();
                            String receiverId = getIntent().getStringExtra("userId");
                            handleSendMessage("Image", receiverId, fileUrl, "media");
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ChatActivity.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                    });
                }
                // Clear the selected images and dismiss the dialog
                selectedMediaUris.clear();
                dialog.dismiss();
            });
            cancelButton.setOnClickListener(v -> {
                // Clear the selected images and dismiss the dialog
                selectedMediaUris.clear();
                dialog.dismiss();
            });

            // Show the dialog
            dialog.show();
        }
        void handleSendMessage(String message, String receiverId, String attachmentUrl, String attachmentType) {
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
    public void showConfirmationDialog(ArrayList<Uri> selectedMediaUris) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Selection");
        builder.setMessage("You have selected " + selectedMediaUris.size() + " items. Do you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectedMediaUris.clear();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
            } catch (IOException e) {

                Toast.makeText(ChatActivity.this, "Failed to start recording", Toast.LENGTH_SHORT).show();

            }
            recorder.start();
            isRecording = true;
        }

        private void stopRecording() {
            if (isRecording && recorder != null) {
                try {
                    recorder.stop();
                } catch (RuntimeException e) {

                    Toast.makeText(ChatActivity.this, "Failed to stop recording", Toast.LENGTH_SHORT).show();
                }
                recorder.release();
                recorder = null;
                isRecording = false;
                uploadAudioFile();
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
                    handleSendMessage(null, receiverId, audioUrl, "audio");
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ChatActivity.this, "Failed to upload audio file", Toast.LENGTH_SHORT).show();
            });

        }

    private void uploadFiles() {
        for (Uri fileUri : selectedMediaUris) {
            StorageReference fileRef = storageReference.child("files/" + fileUri.getLastPathSegment());
            UploadTask uploadTask = fileRef.putFile(fileUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fileUrl = uri.toString();
                    // Save the download URL in the Firebase Database
                    DatabaseReference filesRef = database.getReference("files");
                    String fileId = filesRef.push().getKey();
                    filesRef.child(fileId).setValue(fileUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ChatActivity.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

