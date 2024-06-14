package com.example.mymessengerapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    FirebaseAuth auth;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    Context context;

    public NotificationFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        TextView badge = view.findViewById(R.id.badge);
        RelativeLayout matchingRequests = view.findViewById(R.id.matching_requests);
        RelativeLayout requestsSent = view.findViewById(R.id.requests_sent);
        String currentUserId = auth.getCurrentUser().getUid();
        DatabaseReference matchRequestsRef = database.getReference("MatchRequests").child(currentUserId);

        database.getReference("MatchRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                String currentUserId = auth.getCurrentUser().getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.getKey().equals(currentUserId)) {
                        DataSnapshot currentUserSnapshot = dataSnapshot.child(currentUserId);
                        if (currentUserSnapshot.exists()) {
                            String status = currentUserSnapshot.getValue(String.class);
                            if (status.equals("pending")) {
                                counter++;
                            }
                        }
                    }
                }
                badge.setText("(" + counter + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        matchingRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MatchingRequestsFragment()).commit();
            }
        });
        requestsSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new RequestsSentFragment()).commit();
            }
        });
        return view;
    }

}