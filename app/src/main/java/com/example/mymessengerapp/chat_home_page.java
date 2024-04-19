package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.search.SearchView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;

public class chat_home_page extends AppCompatActivity {
    ChatAdapter chatAdapter;
    SearchView searchView;
    ListView lv_list_chat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_home_page);

        searchView = findViewById(R.id.sv_search);
        lv_list_chat = findViewById(R.id.lv_list_chat);

        chatAdapter = new ChatAdapter(this, new ArrayList<>(Arrays.asList("Chat 1", "Chat 2", "Chat 3")));
        lv_list_chat.setAdapter(chatAdapter);

        // Tìm kiếm tên chat, ở trên là data mẫu thôi, tự xem rồi fix lai
        TextInputEditText searchEditText = (TextInputEditText) searchView.getEditText();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // khong lam gi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chatAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // khong lam gi
            }
        });
    }
}