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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestSentAdapter extends RecyclerView.Adapter<RequestSentAdapter.viewholder>{
    Context context;
    ArrayList<HashMap<String, Object>> requestSent;
    public RequestSentAdapter(Context context, ArrayList<HashMap<String, Object>> requestSent) {
        this.context = context;
        this.requestSent = requestSent;
    }

    @NonNull
    @Override
    public RequestSentAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.matching_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestSentAdapter.viewholder holder, @SuppressLint("RecyclerView") int position) {
        Long timestamp = (Long) requestSent.get(position).get("timestamp");
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

        Log.d("RequestSentAdapter", "Date: " + sdf.format(date));
        String userId = requestSent.get(position).get("recipientId").toString();
        Log.d("RequestSentAdapter", "Binding user ID: " + userId);
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
        holder.undo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the request
                FirebaseDatabase.getInstance().getReference().child("MatchRequests/" +
                        requestSent.get(position).get("requestId")).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestSent.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView user_name;
        CircleImageView user_icon;
        TextView time_sent, noti_context;
        MaterialButton undo_button, decline_button;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_icon = itemView.findViewById(R.id.user_icon);
            noti_context = itemView.findViewById(R.id.noti_context);
            time_sent = itemView.findViewById(R.id.time);
            undo_button = itemView.findViewById(R.id.accept_button);
            decline_button = itemView.findViewById(R.id.decline_button);
            decline_button.setVisibility(View.GONE);
            undo_button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.secondary1));
            undo_button.setText("Undo");
            noti_context.setText("Request sent");
        }
    }
}

