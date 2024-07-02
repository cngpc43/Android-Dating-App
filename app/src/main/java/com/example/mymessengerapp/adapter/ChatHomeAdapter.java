package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatDetail;
import com.example.mymessengerapp.model.VNCharacterUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHomeAdapter extends ArrayAdapter<ChatDetail> {
    private List<ChatDetail> chatDetails;
    private ArrayList<ChatDetail> chatDetailsArrayList;
    private Context context;
    Boolean isFiltered = false;
    String filteredText;

    public ChatHomeAdapter(Context context, List<ChatDetail> chatDetails) {
        super(context, 0, chatDetails);
        this.context = context;
        this.chatDetails = new LinkedList<ChatDetail>();
        if (chatDetails != null) {
            this.chatDetails = chatDetails;
        }
        this.chatDetailsArrayList = new ArrayList<ChatDetail>();
        if (this.chatDetails.size() > 0)
            this.chatDetailsArrayList.addAll(this.chatDetails);
    }
    @Override
    public int getCount() {
        int size = chatDetails.size();
        return size;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ChatHomeAdapter", "Rendering item at position: " + position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);
        try{
            CircleImageView chatImage = convertView.findViewById(R.id.chat_item_avt);
            TextView tvChatName = convertView.findViewById(R.id.tv_chat_name);
            TextView chatMessage = convertView.findViewById(R.id.tv_chat_message);
            TextView chatTime = convertView.findViewById(R.id.tv_chat_time);
            ImageView online_icon = convertView.findViewById(R.id.online_icon);
            ChatDetail chatDetail = getItem(position);
            if (chatDetail != null) {
                if(chatDetail.getTimestamp()==0){
                    chatTime.setVisibility(View.INVISIBLE);
                }
                if (chatDetail.isOnline())
                    online_icon.setVisibility(View.VISIBLE);
                else
                    online_icon.setVisibility(View.GONE);
                tvChatName.setText(chatDetail.getUserName());
                chatMessage.setText(chatDetail.getLastMessage());
                if (chatDetail.isHave_not_read()) {
                    chatMessage.setTypeface(chatMessage.getTypeface(), Typeface.BOLD);
                    chatMessage.setTextColor(Color.BLACK);
                    tvChatName.setTypeface(tvChatName.getTypeface(), Typeface.BOLD);
                    tvChatName.setTextColor(Color.BLACK);
                    chatTime.setTypeface(chatTime.getTypeface(), Typeface.BOLD);
                    chatTime.setTextColor(Color.BLACK);
                }
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
                // check if userList is filtered
                if (isFiltered) {
                    // Remove accents and lowercase the user name for searching
                    String modifiedName = VNCharacterUtils.removeAccent(chatDetails.get(position).getUserName()).toLowerCase(Locale.getDefault());
                    if (modifiedName.contains(filteredText)) {
                        Spannable WordtoSpan = new SpannableString(chatDetails.get(position).getUserName());
                        WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedName.indexOf(filteredText),
                                modifiedName.indexOf(filteredText) + filteredText.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvChatName.setText(WordtoSpan);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ChatHomeAdapter", "Error in getView", e);
        }
        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = VNCharacterUtils.removeAccent(charText).toLowerCase(Locale.getDefault());
        chatDetails.clear();
        if (charText.length() == 0) {
            chatDetails.addAll(chatDetailsArrayList);
            isFiltered = false;
        } else {
            for (ChatDetail chat : chatDetailsArrayList) {
                if (VNCharacterUtils.removeAccent(chat.getUserName()).toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredText = charText;
                    chatDetails.add(chat);
                    isFiltered = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setArraylist(List<ChatDetail> chatDetails) {
        chatDetailsArrayList.clear();
        chatDetailsArrayList.addAll(chatDetails);
    }
}