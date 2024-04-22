package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;

import java.util.ArrayList;
import java.util.List;

// display the chat names in the ListView and filter the chat names based on the search query
public class ChatHomeAdapter extends ArrayAdapter<String> {
    private List<String> chatNames;
    private List<String> chatNamesFiltered;

    public ChatHomeAdapter(Context context, List<String> chatNames) {
        super(context, 0, chatNames);
        this.chatNames = new ArrayList<>(chatNames);
        this.chatNamesFiltered = new ArrayList<>(chatNames);
    }

    // display the chat names in the ListView, tạo model message để lưu hội thoại rồi đẩy data vào đây
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);

//        ImageView userIcon = convertView.findViewById(R.id.user_icon);
//        TextView tvChatName = convertView.findViewById(R.id.tv_chat_name);
//        TextView chatMessage = convertView.findViewById(R.id.chat_message);
//        TextView chatTime = convertView.findViewById(R.id.chat_time);

//        String chatName = getItem(position);
//
//        if (chatName != null)
//            tvChatName.setText(chatName);
//        if (userIcon != null)
//            userIcon.setImageResource(R.drawable.ic_baseline_account_circle_24);
//        if (chatMessage != null)
//            chatMessage.setText("Hello");
//        if (chatTime != null)
//            chatTime.setText("12:00");

        return convertView;
    }

    public void filter(String text) {
        chatNamesFiltered.clear();

        if (!text.isEmpty()) {
            chatNamesFiltered.addAll(chatNames);
            notifyDataSetChanged();
            return;
        }

        text = text.toLowerCase();
        for (String item : chatNames) {
            if (item.toLowerCase().contains(text)) {
                chatNamesFiltered.add(item);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatNamesFiltered.size();
    }

    @Override
    public String getItem(int position) {
        return chatNamesFiltered.get(position);
    }
}
