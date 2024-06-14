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

import java.util.ArrayList;
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
        FirebaseDatabase.getInstance().getReference("MatchRequests")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(users.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(String.class).equals("pending")) {
                            holder.likeButton.setEnabled(false);
                            holder.likeButton.setImageResource(R.drawable.wait);
                            holder.likeButton.setBackgroundColor(mainActivity.getResources().getColor(R.color.grey));
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
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String likedUserId = usersArrayList.get(holder.getAdapterPosition()).getUserId();
                String matchRequestId = reference.push().getKey();
                Map<String, Object> matchRequestData = new HashMap<>();
                matchRequestData.put("requesterId", currentUserId);
                matchRequestData.put("recipientId", likedUserId);
                matchRequestData.put("timestamp", ServerValue.TIMESTAMP); // Use server timestamp
                matchRequestData.put("status", "pending");
                reference.child(matchRequestId).setValue(matchRequestData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mainActivity, "Matching request sent", Toast.LENGTH_SHORT).show();
                        holder.likeButton.setEnabled(false);
                        holder.likeButton.setImageResource(R.drawable.wait);
                        holder.likeButton.setBackgroundColor(mainActivity.getResources().getColor(R.color.grey));
                    }
                });


            }
        });
        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dislikedUserId = usersArrayList.get(holder.getAdapterPosition()).getUserId();
                FirebaseDatabase.getInstance().getReference("DeclineList")
                        .child(currentUserId)
                        .child(dislikedUserId)
                        .setValue("declined");
                FirebaseDatabase.getInstance().getReference("DeclineList")
                        .child(dislikedUserId)
                        .child(currentUserId)
                        .setValue("declined");
                Toast.makeText(mainActivity, "Request Declined", Toast.LENGTH_SHORT).show();
                holder.dislikeButton.setEnabled(false);
                holder.dislikeButton.setImageResource(R.drawable.wait);
                holder.dislikeButton.setBackgroundColor(mainActivity.getResources().getColor(R.color.grey));
                // Remove from the list
                usersArrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), usersArrayList.size());

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
