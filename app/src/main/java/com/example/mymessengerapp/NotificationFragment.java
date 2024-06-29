package com.example.mymessengerapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationFragment extends Fragment {
    FirebaseAuth auth;
    RelativeLayout matchingRequests, requestsSent;
    TextView badge, sent_badge, title;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;
    ImageView ic_home, ic_chat, ic_noti, ic_user;
    FirebaseDatabase database;
    Context context;
    String currentUserId;
    ValueEventListener valueEventListener;
    DatabaseReference reference;
    int matching_request_count = 0, request_sent_count = 0;

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

        badge = view.findViewById(R.id.badge);
        sent_badge = view.findViewById(R.id.sent_badge);
        matchingRequests = view.findViewById(R.id.matching_requests);
        requestsSent = view.findViewById(R.id.requests_sent);
        title = getActivity().findViewById(R.id.title);
        home_selected = getActivity().findViewById(R.id.home_selected);
        noti_selected = getActivity().findViewById(R.id.noti_selected);
        chat_selected = getActivity().findViewById(R.id.chat_selected);
        user_selected = getActivity().findViewById(R.id.user_selected);
        ic_home = getActivity().findViewById(R.id.icon_home);
        ic_chat = getActivity().findViewById(R.id.icon_chat);
        ic_noti = getActivity().findViewById(R.id.icon_noti);
        ic_user = getActivity().findViewById(R.id.icon_user);

        title.setText("Notifications");
        home_selected.setBackground(null);
        chat_selected.setBackground(null);
        user_selected.setBackground(null);
        noti_selected.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selected_nav_item));

        ic_home.setColorFilter(Color.BLACK);
        ic_chat.setColorFilter(Color.BLACK);
        ic_user.setColorFilter(Color.BLACK);
        ic_noti.setColorFilter(Color.rgb(236, 83, 131));

        currentUserId = auth.getCurrentUser().getUid();
        reference = database.getReference("MatchRequests");


        reference.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matching_request_count = 0;
                request_sent_count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("recipientId").getValue(String.class) != null
                            && dataSnapshot.child("requesterId").getValue(String.class) != null
                            && dataSnapshot.child("status").getValue(String.class) != null) {
                        if (dataSnapshot.child("recipientId").getValue(String.class).equals(currentUserId) && dataSnapshot.child("status").getValue(String.class).equals("pending")) {
                            matching_request_count++;
                        }
                        if (dataSnapshot.child("requesterId").getValue(String.class).equals(currentUserId) && dataSnapshot.child("status").getValue(String.class).equals("pending")) {
                            request_sent_count++;
                        }
                    }
                }
                badge.setText("(" + matching_request_count + ")");
                sent_badge.setText("(" + request_sent_count + ")");
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (reference != null && valueEventListener != null)
            reference.removeEventListener(valueEventListener);
    }

}