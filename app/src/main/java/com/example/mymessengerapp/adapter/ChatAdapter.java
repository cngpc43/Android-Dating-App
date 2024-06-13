package com.example.mymessengerapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    int ITEM_SEND = 1;
    int ITEM_RECEIVER = 2;
    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == ITEM_SEND){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_sender, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_receiver, parent, false);
        }
        return new ChatViewHolder(view);
    }

    // Lay data message
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        // Neu gui thi de hien thi ben layout send, khong thi nguoc lai
        if (chatMessage.isSent()) {
//            holder.sendLayout.setVisibility(View.VISIBLE);
//            holder.receiveLayout.setVisibility(View.GONE);
            holder.sendMessage.setText(chatMessage.getMessage());
        } else {
//            holder.receiveLayout.setVisibility(View.VISIBLE);
//            holder.sendLayout.setVisibility(View.GONE);
            holder.receiveMessage.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // De match moi UI id thoi, de biet duong ma set data vo (onBindViewHolder lam)
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView sendMessage, receiveMessage;
        View sendLayout, receiveLayout;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            sendMessage = itemView.findViewById(R.id.send_message);
            receiveMessage = itemView.findViewById(R.id.receive_message);
            sendLayout = itemView.findViewById(R.id.send_layout);
            receiveLayout = itemView.findViewById(R.id.receive_layout);
        }
    }
}