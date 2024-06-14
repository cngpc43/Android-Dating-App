package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatHomeAdapter extends ArrayAdapter<ChatDetail> {
    private ArrayList<ChatDetail> chatDetails;
    private Context context;

    public ChatHomeAdapter(Context context, ArrayList<ChatDetail> chatDetails) {
        super(context, 0, chatDetails);
        this.context = context;
        this.chatDetails = chatDetails;
    }
    @Override
    public int getCount() {
        int size = chatDetails.size();
        Log.d("ChatHomeAdapter", "getCount: " + size);
        return size;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ChatHomeAdapter", "Rendering item at position: " + position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);
        try{
            TextView tvChatName = convertView.findViewById(R.id.tv_chat_name);
            TextView chatMessage = convertView.findViewById(R.id.tv_chat_message);
            TextView chatTime = convertView.findViewById(R.id.tv_chat_time);
            ChatDetail chatDetail = getItem(position);
            if (chatDetail != null) {
                tvChatName.setText(chatDetail.getUserName());
                chatMessage.setText(chatDetail.getLastMessage());
                Date date = new Date(chatDetail.getTimestamp());
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedTime = sdf.format(date);
                chatTime.setText(formattedTime);
                Log.d("ChatHomeAdapter", "Setting data for item at position: " + position);
                Log.d("ChatHomeAdapter", "userName: " + chatDetail.getUserName());
                Log.d("ChatHomeAdapter", "lastMessage: " + chatDetail.getLastMessage());
                Log.d("ChatHomeAdapter", "timestamp: " + formattedTime);
            }
        } catch (Exception e) {
            Log.e("ChatHomeAdapter", "Error in getView", e);
        }
        return convertView;
    }
}