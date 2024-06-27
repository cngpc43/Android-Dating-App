package com.example.mymessengerapp.adapter;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchingRequestAdapter extends RecyclerView.Adapter<MatchingRequestAdapter.viewholder>{
    Context context;
    ArrayList<HashMap<String, Object>> matchingRequests;
    public MatchingRequestAdapter(Context context, ArrayList<HashMap<String, Object>> matchingRequests) {
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
    public void onBindViewHolder(@NonNull MatchingRequestAdapter.viewholder holder, @SuppressLint("RecyclerView") int position) {
        // Handle timestamp get from firebase
        Long timestamp = (Long) matchingRequests.get(position).get("timestamp");
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date currentDate = new Date();
        Long difference = currentDate.getTime() - date.getTime();
        if (TimeUnit.MILLISECONDS.toSeconds(difference) < 60)
            holder.time_sent.setText("Just now");
        else if (TimeUnit.MILLISECONDS.toMinutes(difference) >= 1 && TimeUnit.MILLISECONDS.toMinutes(difference) < 60)
            holder.time_sent.setText(TimeUnit.MILLISECONDS.toMinutes(difference) + "m");
        else if (TimeUnit.MILLISECONDS.toHours(difference) >= 1 && TimeUnit.MILLISECONDS.toHours(difference) < 24)
            holder.time_sent.setText(TimeUnit.MILLISECONDS.toHours(difference) + "h");
        else
            holder.time_sent.setText(TimeUnit.MILLISECONDS.toDays(difference) + "d");

        Log.d("MatchingRequestAdapter", "Date: " + sdf.format(date));
        String userId = matchingRequests.get(position).get("requesterId").toString();
        Log.d("MatchingRequestAdapter", "Binding user ID: " + userId);
        FirebaseDatabase.getInstance().getReference().child("user/" + userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() != null) {
                        DataSnapshot snapshot = task.getResult();
                        HashMap<String, Object> users = (HashMap<String, Object>) snapshot.getValue();
                        if (users.get("userName") != null)
                            holder.user_name.setText(users.get("userName").toString());
                        Log.d("MatchingRequestAdapter", "User name: " + users.get("userName").toString());
                        if (users.get("profilepic") != null)
                            Picasso.get().load(users.get("profilepic").toString()).into(holder.user_icon);
                    }
                } else {
                    Log.e("MatchingRequestAdapter", "Error fetching user data: " + task.getException().getMessage().toString());
                }
            }
        });
        holder.accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accept the request
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String matchedUserId = matchingRequests.get(position).get("requesterId").toString();

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
                        .setValue("");

                FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                        matchingRequests.get(position).get("requestId") + "/status").setValue("accepted");
                FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                        matchingRequests.get(position).get("requestId") + "/timestamp").setValue(ServerValue.TIMESTAMP);
                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                // Remove the request from the list
                matchingRequests.remove(position);
            }
        });
        holder.decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decline the request
                FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                        matchingRequests.get(position).get("requestId") + "/status").setValue("declined");
                FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                        matchingRequests.get(position).get("requestId") + "/timestamp").setValue(ServerValue.TIMESTAMP);
                Toast.makeText(context, "Request Declined", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchingRequests.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView user_name;
        CircleImageView user_icon;
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
