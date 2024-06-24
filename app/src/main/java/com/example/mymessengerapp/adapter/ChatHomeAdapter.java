package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

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
            ImageView chatImage = convertView.findViewById(R.id.chat_item_avt);
            TextView tvChatName = convertView.findViewById(R.id.tv_chat_name);
            TextView chatMessage = convertView.findViewById(R.id.tv_chat_message);
            TextView chatTime = convertView.findViewById(R.id.tv_chat_time);
            ChatDetail chatDetail = getItem(position);
            if (chatDetail != null) {

                if(chatDetail.getTimestamp()==0){
                    chatTime.setVisibility(View.INVISIBLE);
                }

                tvChatName.setText(chatDetail.getUserName());
                chatMessage.setText(chatDetail.getLastMessage());
                Date date = new Date(chatDetail.getTimestamp());
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedTime = sdf.format(date);
                chatTime.setText(formattedTime);
                String imgURL = chatDetail.getUserImage();
                if (imgURL != null && !imgURL.isEmpty()) {
                    Picasso.get().load(imgURL).into(chatImage);
                } else {
                    FirebaseStorage.getInstance().getReference().child("default.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(chatImage);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e("ChatHomeAdapter", "Error in getView", e);
        }
        return convertView;
    }
}