package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.Users;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchingRequestAdapter extends RecyclerView.Adapter<MatchingRequestAdapter.viewholder>{
    Context context;
    ArrayList<String> matchingRequests;
    public MatchingRequestAdapter(Context context, ArrayList<String> matchingRequests) {
        this.context = context;
        this.matchingRequests = matchingRequests;
    }

    @NonNull
    @Override
    public MatchingRequestAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.matching_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchingRequestAdapter.viewholder holder, int position) {
        String userId = matchingRequests.get(position);
        Log.d("MatchingRequestAdapter", "Binding user ID: " + userId);
        FirebaseDatabase.getInstance().getReference("user")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            HashMap<String, Object> users = (HashMap<String, Object>) snapshot.getValue();
                            holder.user_name.setText(users.get("userName").toString());
                            Log.d("MatchingRequestAdapter", "User name: " + users.get("userName").toString());
//                            Picasso.get().load(users.get("userIcon").toString()).into(holder.user_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error here
                        Log.e("MatchingRequestAdapter", "Error fetching user data: " + error.getMessage());
                    }
                });
        holder.accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accept the request
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String matchedUserId = matchingRequests.get(position);
                FirebaseDatabase.getInstance().getReference("MatchRequests")
                        .child(currentUserId)
                        .child(matchedUserId)
                        .setValue("accepted");
                String chatRoomId = currentUserId + "_" + matchedUserId;
                FirebaseDatabase.getInstance().getReference("ChatRooms")
                                .child(currentUserId)
                                .child(chatRoomId)
                                .setValue(true);
                FirebaseDatabase.getInstance().getReference("ChatRooms")
                                .child(matchedUserId)
                                .child(chatRoomId)
                                .setValue(true);
                // Add chatroomId into Chats
                FirebaseDatabase.getInstance().getReference("Chats")
                        .child(chatRoomId)
                        .setValue("Chat started");

                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                // Remove the request from the list
                matchingRequests.remove(position);
            }
        });
        holder.decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decline the request
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String matchedUserId = matchingRequests.get(position);
                FirebaseDatabase.getInstance().getReference("MatchRequests")
                        .child(currentUserId)
                        .child(matchedUserId)
                        .setValue("declined");
                Toast.makeText(context, "Request Declined", Toast.LENGTH_SHORT).show();
                // Remove the request from the list
                matchingRequests.remove(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return matchingRequests.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView user_name;
        ImageView user_icon;
        TextView time_sent;
        MaterialButton accept_button, decline_button;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_icon = itemView.findViewById(R.id.user_icon);
            time_sent = itemView.findViewById(R.id.time);
            accept_button = itemView.findViewById(R.id.accept_button);
            decline_button = itemView.findViewById(R.id.decline_button);
            }
        }
    }
