package com.example.mymessengerapp;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class ChatHomePage extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseStorage storage;

    FrameLayout user, message, notification;

//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_home_page);
//        chat_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chat_frame, chatFragment);
        transaction.commit();

        // Tìm kiếm tên chat, ở trên là data mẫu thôi, tự xem rồi fix lai
//        TextInputEditText searchEditText = (TextInputEditText) searchView.getEditText();





    }


}