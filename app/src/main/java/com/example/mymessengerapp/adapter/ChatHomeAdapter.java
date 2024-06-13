package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatHomeAdapter extends ArrayAdapter<ChatMessage> {
    private ArrayList<ChatMessage> chatMessages;
    private Context context;

    public ChatHomeAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        super(context, 0, chatMessages);
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);

        TextView tvChatName = convertView.findViewById(R.id.tv_chat_name);
        TextView chatMessage = convertView.findViewById(R.id.tv_chat_message);
        TextView chatTime = convertView.findViewById(R.id.tv_chat_time);

        ChatMessage chatMessageItem = getItem(position);

        if (chatMessageItem != null) {
            chatMessageItem.getUserName().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userName) {
                    tvChatName.setText(userName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the error here
                }
            });
            chatMessage.setText(chatMessageItem.getLastMessage());
            Date date = new Date(chatMessageItem.getTimeStamp());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(date);
            chatTime.setText(formattedTime);
        }

        return convertView;
    }
}