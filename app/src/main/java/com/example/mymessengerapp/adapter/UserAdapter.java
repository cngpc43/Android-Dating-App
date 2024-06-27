package com.example.mymessengerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import com.example.mymessengerapp.MainActivity;
import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatRoom;
import com.example.mymessengerapp.model.MyImageSwitcher;
import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    Context mainActivity;
    ArrayList<Users> usersArrayList;
    DatabaseReference reference;
    Boolean hasSentRequest = false;
    String matchRequestId;

    public UserAdapter(Context mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity = mainActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item, parent, false);
        reference = FirebaseDatabase.getInstance().getReference().child("MatchRequests");

        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, @SuppressLint("RecyclerView") int position) {
        final int[] currentIndex = {0};
        ArrayList<String> imageList = usersArrayList.get(position).getPhotos();
        Users users = usersArrayList.get(position);
        holder.username.setText(users.getUserName());
        holder.userstatus.setText(users.getStatus());
        String profilepic = users.getProfilepic();
        if (profilepic != null) {
            Picasso.get().load(profilepic).into(holder.profile_pic);
        }

        holder.userimg.setImageUrl(imageList.get(currentIndex[0]));

        FirebaseDatabase.getInstance().getReference("MatchRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hasSentRequest = false;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("status").getValue(String.class) != null && dataSnapshot.child("requesterId").getValue(String.class) != null
                                    && dataSnapshot.child("recipientId").getValue(String.class) != null) {
                                // if current user has received request from the user on RecyclerView already
                                if (dataSnapshot.child("recipientId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        && dataSnapshot.child("requesterId").getValue(String.class).equals(usersArrayList.get(holder.getAdapterPosition()).getUserId())
                                        && dataSnapshot.child("status").getValue(String.class).equals("pending")) {
                                    hasSentRequest = true;
                                    matchRequestId = dataSnapshot.getKey();
                                    break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error here
                    }
                });
        holder.left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex[0] > 0) {
                    // setting animation to swipe image from right to left
                    holder.userimg.setInAnimation(mainActivity.getApplicationContext(),R.anim.from_left);
                    holder.userimg.setOutAnimation(mainActivity.getApplicationContext(),R.anim.to_right);
                    currentIndex[0]--;

                    holder.userimg.setImageUrl(imageList.get(currentIndex[0]));
                }
            }
        });

        holder.right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex[0] < imageList.size() - 1) {
                    // setting animation to swipe image from left to right
                    holder.userimg.setInAnimation(mainActivity.getApplicationContext(), R.anim.from_right);
                    holder.userimg.setOutAnimation(mainActivity.getApplicationContext(),R.anim.to_left);
                    currentIndex[0]++;

                    holder.userimg.setImageUrl(imageList.get(currentIndex[0]));
                }
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId;
                String likedUserId;
                String likedUserName = usersArrayList.get(holder.getAdapterPosition()).getUserName();
                Boolean temp_hasSentRequest = hasSentRequest;
                Map<String, Object> matchRequestData = new HashMap<>();
                if (temp_hasSentRequest) {
                    matchRequestData.put("status", "accepted");
                    currentUserId = usersArrayList.get(holder.getAdapterPosition()).getUserId();
                    likedUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }
                else {
                    matchRequestData.put("status", "pending");
                    matchRequestId = reference.push().getKey();
                    currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    likedUserId = usersArrayList.get(holder.getAdapterPosition()).getUserId();
                }
                matchRequestData.put("requesterId", currentUserId);
                matchRequestData.put("recipientId", likedUserId);
                matchRequestData.put("timestamp", ServerValue.TIMESTAMP); // Use server timestamp

                reference.child(matchRequestId).setValue(matchRequestData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (temp_hasSentRequest) {
                            Toast.makeText(mainActivity, "You and " + likedUserName + " are now matched", Toast.LENGTH_SHORT).show();
                            // insert code for creating chat room here
                            String chatRoomId = currentUserId + "_" + likedUserId;
                            FirebaseDatabase.getInstance().getReference("ChatRooms")
                                    .child(currentUserId)
                                    .child(chatRoomId)
                                    .setValue(true);
                            FirebaseDatabase.getInstance().getReference("ChatRooms")
                                    .child(likedUserId)
                                    .child(chatRoomId)
                                    .setValue(true);
                            // Add chatroomId into Chats
                            FirebaseDatabase.getInstance().getReference("Chats")
                                    .child(chatRoomId)
                                    .setValue("Chat started");
                        }
                        else
                            Toast.makeText(mainActivity, "Matching request sent to " + likedUserName, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId;
                String dislikedUserId;
                Boolean temp_hasSentRequest = hasSentRequest;
                Map<String, Object> matchRequestData = new HashMap<>();
                if (temp_hasSentRequest) {
                    currentUserId = usersArrayList.get(holder.getAdapterPosition()).getUserId();
                    dislikedUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }
                else {
                    matchRequestId = reference.push().getKey();
                    currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    dislikedUserId = usersArrayList.get(holder.getAdapterPosition()).getUserId();
                }
                matchRequestData.put("requesterId", currentUserId);
                matchRequestData.put("recipientId", dislikedUserId);
                matchRequestData.put("timestamp", ServerValue.TIMESTAMP); // Use server timestamp
                matchRequestData.put("status", "declined");
                reference.child(matchRequestId).setValue(matchRequestData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mainActivity, "Suggested user removed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        MyImageSwitcher userimg;
        CircleImageView profile_pic;
        TextView username, userstatus;
        ImageButton likeButton, dislikeButton, left_button, right_button;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            likeButton = itemView.findViewById(R.id.like);
            dislikeButton = itemView.findViewById(R.id.dislike);
            left_button = itemView.findViewById(R.id.left_button);
            right_button = itemView.findViewById(R.id.right_button);
            userimg.setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    ImageView myView = new ImageView(mainActivity.getApplicationContext());
                    myView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    myView.setLayoutParams(new ImageSwitcher.LayoutParams(userimg.getLayoutParams()));
                    return myView;
                }
            });
        }
    }
}
