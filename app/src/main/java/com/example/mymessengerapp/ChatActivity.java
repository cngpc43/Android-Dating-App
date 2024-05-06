package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.adapter.ChatAdapter;
import com.example.mymessengerapp.model.ChatMessage;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    EditText messageInput;
    ImageButton sendMessBtn, backBtn;
    TextView userName, userStatus;
    ShapeableImageView userAvatar;
    RecyclerView mainChat;
    ChatAdapter chatAdapter;
    List<ChatMessage> chatMessages;
    LinearLayout llSendChat;

    PopupWindow attachmentPopup;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // lấy ID của users và chatroom

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
        chatMessages.add(new ChatMessage("Hello, how are you ?", false));
        chatMessages.add(new ChatMessage("I am fine", true));

        // Gui chat message vo recyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        mainChat.setAdapter(chatAdapter);
        mainChat.setLayoutManager(new LinearLayoutManager(this));

        // Set content for UI
//        userName.setText();
//        userStatus.setText();
//        userAvatar.set

        // Xu ly khi nhan back ve
        backBtn.setOnClickListener(v -> onBackPressed());
        sendMessBtn.setOnClickListener(v -> {
            String message = messageInput.toString().trim();

            // Khong thuc hienn khi khong co tin nhan
            if (message.isEmpty())
                return;

            handleSendMessage(message);
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
    }

    // Xu ly chuc nang gui tin nhan
    void handleSendMessage(String message) {

    }
}