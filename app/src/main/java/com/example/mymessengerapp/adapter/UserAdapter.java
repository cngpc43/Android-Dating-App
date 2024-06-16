package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mymessengerapp.MainActivity;
import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatRoom;
import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {

        Users users = usersArrayList.get(position);
        holder.username.setText(users.getUserName());
        holder.userstatus.setText(users.getStatus());
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
        String profilepic = users.getProfilepic();
        if (profilepic != null) {

        }
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
//        Picasso.get().load(users.profilepic).into(holder.userimg);
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView userimg;
        TextView username;
        TextView userstatus;
        ImageButton likeButton;
        ImageButton dislikeButton;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            likeButton = itemView.findViewById(R.id.like);
            dislikeButton = itemView.findViewById(R.id.dislike);
        }
    }
}
