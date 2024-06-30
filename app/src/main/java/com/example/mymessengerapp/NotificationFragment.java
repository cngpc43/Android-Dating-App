package com.example.mymessengerapp;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mymessengerapp.MainActivity;
import com.example.mymessengerapp.MatchingRequestsFragment;
import com.example.mymessengerapp.R;
import com.example.mymessengerapp.RequestsSentFragment;
import com.example.mymessengerapp.adapter.NotificationsAdapter;
import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.NotificationModel;
import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NotificationFragment extends Fragment {
    FirebaseAuth auth;
    RelativeLayout matchingRequests, requestsSent;
    TextView badge, sent_badge, title;
    ListView lv_noti;
    NotificationsAdapter adapter;
    FirebaseDatabase database;
    Context context;
    String currentUserId;
    ValueEventListener valueEventListener;
    DatabaseReference reference;
    boolean listenerAdded = false;
    int matching_request_count = 0, request_sent_count = 0;
    List<NotificationModel> notificationsList;

    public NotificationFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        notificationsList = new LinkedList<NotificationModel>();
        adapter = new NotificationsAdapter(getActivity(), notificationsList);
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
        lv_noti = view.findViewById(R.id.lv_noti);

        title.setText("Notifications");
        lv_noti.setAdapter(adapter);

        currentUserId = auth.getCurrentUser().getUid();
        reference = database.getReference("MatchRequests");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                matching_request_count = 0;
                request_sent_count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("recipientId").getValue(String.class) != null
                            && dataSnapshot.child("requesterId").getValue(String.class) != null
                            && dataSnapshot.child("status").getValue(String.class) != null) {
                        if (dataSnapshot.child("recipientId").getValue(String.class).equals(currentUserId) && dataSnapshot.child("status").getValue(String.class).equals("pending")) {
                            matching_request_count++;
                            FirebaseDatabase.getInstance().getReference().child("user/" + dataSnapshot.child("requesterId").getValue(String.class)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot1) {
                                    String dob = dataSnapshot1.child("dob").getValue(String.class);
                                    // another user age
                                    int userAge = Calendar.getInstance().get(Calendar.YEAR) - Integer.valueOf(dob.substring(dob.indexOf(",") + 2, dob.length()));
                                    NotificationModel notificationModel = new NotificationModel(dataSnapshot1.child("requesterId").getValue(String.class),
                                            dataSnapshot1.child("userName").getValue(String.class), String.valueOf(userAge),
                                            dataSnapshot1.child("profilepic").getValue(String.class), "request_send",
                                            dataSnapshot.child("timestamp").getValue(Long.class));
                                    if (notificationModel != null)
                                        notificationsList.add(notificationModel);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        if (dataSnapshot.child("status").getValue(String.class).equals("accepted")) {
                            String userId = dataSnapshot.child("recipientId").getValue(String.class);
                            if (userId.equals(auth.getCurrentUser().getUid()))
                                userId = dataSnapshot.child("requesterId").getValue(String.class);
                            String finalUserId = userId;
                            FirebaseDatabase.getInstance().getReference().child("user/" + userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot1) {
                                    String dob = dataSnapshot1.child("dob").getValue(String.class);
                                    // another user age
                                    int userAge = Calendar.getInstance().get(Calendar.YEAR) - Integer.valueOf(dob.substring(dob.indexOf(",") + 2, dob.length()));
                                    NotificationModel notificationModel = new NotificationModel(finalUserId,
                                            dataSnapshot1.child("userName").getValue(String.class), String.valueOf(userAge),
                                            dataSnapshot1.child("profilepic").getValue(String.class), "request_accept",
                                            dataSnapshot.child("timestamp").getValue(Long.class));
                                    if (notificationModel != null)
                                        notificationsList.add(notificationModel);
                                    adapter.notifyDataSetChanged();
                                }
                            });
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
        };

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
    public void onStart() {
        super.onStart();
        if (!listenerAdded) {
            reference.addValueEventListener(valueEventListener);
            listenerAdded = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listenerAdded) {
            reference.removeEventListener(valueEventListener);
            listenerAdded = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerAdded) {
            reference.removeEventListener(valueEventListener);
            listenerAdded = false;
        }
    }
}