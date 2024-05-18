package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
                            Users users = snapshot.getValue(Users.class);
                            holder.user_name.setText(users.getUserName());
                            Log.d("MatchingRequestAdapter", "User name: " + users.getUserName());
//                            Picasso.get().load(users.getUserIcon()).into(holder.user_icon);


//                            String timeSent = convertTimestampToReadableFormat(users.getTimeSent());
//                            holder.time_sent.setText(timeSent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error here
                        Log.e("MatchingRequestAdapter", "Error fetching user data: " + error.getMessage());
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

        public viewholder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_icon = itemView.findViewById(R.id.user_icon);
            time_sent = itemView.findViewById(R.id.time);
        }
    }
}
