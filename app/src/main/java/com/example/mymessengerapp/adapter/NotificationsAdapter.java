package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mymessengerapp.ChatActivity;
import com.example.mymessengerapp.R;
import com.example.mymessengerapp.ViewAnotherProfile;
import com.example.mymessengerapp.model.NotificationModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.color.MaterialColorUtilitiesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

// Adapter to display data in notification fragment
public class NotificationsAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    public List<NotificationModel> notificationsList;

    public NotificationsAdapter(Context context, List<NotificationModel> notificationsList) {
        mContext = context;
        this.notificationsList = notificationsList;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return notificationsList.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return notificationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView userName, noti_context, time;
        CircleImageView user_icon;
        ConstraintLayout notification_item;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        NotificationsAdapter.Holder holder = new NotificationsAdapter.Holder();
        view = inflater.inflate(R.layout.notification_item, null);
        // Locate the TextViews in listview_item.xml
        holder.userName = view.findViewById(R.id.user_name);
        holder.noti_context = view.findViewById(R.id.noti_context);
        holder.time = view.findViewById(R.id.time);
        holder.user_icon = view.findViewById(R.id.user_icon);
        holder.notification_item = view.findViewById(R.id.notification_item);

        // Set the results into view
        holder.userName.setText(notificationsList.get(position).getUserName() + ", " + notificationsList.get(position).getAge());

        if (notificationsList.get(position).getStatus().equals("seen")) {
            holder.notification_item.setBackgroundColor(Color.WHITE);
        }

        if (notificationsList.get(position).getType().equals("request_send"))
            holder.noti_context.setText("Has sent you a match request.");
        else if (notificationsList.get(position).getType().equals("request_accept"))
            holder.noti_context.setText("And you are now matched.");

        Long timestamp = notificationsList.get(position).getTimestamp();
        Date date = new Date(timestamp);
        Date currentDate = new Date();
        Long difference = currentDate.getTime() - date.getTime();

        if (TimeUnit.MILLISECONDS.toSeconds(difference) < 60)
            holder.time.setText("Just now");
        else if (TimeUnit.MILLISECONDS.toMinutes(difference) >= 1 && TimeUnit.MILLISECONDS.toMinutes(difference) < 60)
            holder.time.setText(TimeUnit.MILLISECONDS.toMinutes(difference) + "m");
        else if (TimeUnit.MILLISECONDS.toHours(difference) >= 1 && TimeUnit.MILLISECONDS.toHours(difference) < 24)
            holder.time.setText(TimeUnit.MILLISECONDS.toHours(difference) + "h");
        else
            holder.time.setText(TimeUnit.MILLISECONDS.toDays(difference) + "d");

        Picasso.get().load(notificationsList.get(position).getProfileImg()).into(holder.user_icon);

        holder.notification_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationsList.get(position).getType().equals("request_send")) {
                    // go to view profile
                    Intent intent = new Intent(mContext, ViewAnotherProfile.class);
                    intent.putExtra("userId", notificationsList.get(position).getUserId());
                    Log.d("NotificationAdapter", notificationsList.get(position).getUserId());
                    mContext.startActivity(intent);

                    // set recipient_status to "seen"
                    FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                            notificationsList.get(position).getMatchRequestId() + "/recipient_status").setValue("seen");
                } else if (notificationsList.get(position).getType().equals("request_accept")) {
                    // go to chat room
                    String firstId = FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + notificationsList.get(position).getUserId();
                    String secondId = notificationsList.get(position).getUserId() + "_" + FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference().child("Chats").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            String roomId = firstId;
                            if (dataSnapshot.hasChild(secondId)) {
                                roomId = secondId;
                            }
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.putExtra("userId", notificationsList.get(position).getUserId());
                            intent.putExtra("userName", notificationsList.get(position).getUserName());
                            intent.putExtra("userImage", notificationsList.get(position).getProfileImg());
                            intent.putExtra("roomId", roomId);
                            mContext.startActivity(intent);
                            // set noti_status to "seen"
                            FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                                            notificationsList.get(position).getMatchRequestId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("requesterId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                                                notificationsList.get(position).getMatchRequestId() + "/recipient_status").setValue("seen");
                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                                                notificationsList.get(position).getMatchRequestId() + "/requester_status").setValue("seen");
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });

        return view;
    }
}
