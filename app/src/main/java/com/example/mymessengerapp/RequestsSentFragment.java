package com.example.mymessengerapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mymessengerapp.adapter.RequestSentAdapter;
import com.example.mymessengerapp.model.MapComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class RequestsSentFragment extends Fragment {

    TextView title;
    FirebaseAuth auth;
    RecyclerView matchingRequestsRecyclerView;
    FirebaseDatabase database;
    ArrayList<HashMap<String, Object>> matchingItems;
    ValueEventListener valueEventListener;
    DatabaseReference reference;
    boolean listenerAdded = false;
    public RequestsSentFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        matchingItems = new ArrayList<HashMap<String, Object>>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_sent, container, false);
        matchingRequestsRecyclerView = view.findViewById(R.id.requests_sent_recycler_view);
        title = getActivity().findViewById(R.id.title);
        title.setText("Requests Sent");
        matchingRequestsRecyclerView.setHasFixedSize(true);
        matchingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String currentUserId = auth.getCurrentUser().getUid();

        RequestSentAdapter adapter = new RequestSentAdapter(getContext(), matchingItems);
        matchingRequestsRecyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("MatchRequests");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchingItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("requesterId").getValue(String.class) != null && dataSnapshot.child("status").getValue(String.class) != null) {
                        if (dataSnapshot.child("requesterId").getValue(String.class).equals(currentUserId) && dataSnapshot.child("status").getValue(String.class).equals("pending")) {
                            GenericTypeIndicator<HashMap<String, Object>> to = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            HashMap<String, Object> hashMap = dataSnapshot.getValue(to);
                            hashMap.put("requestId", dataSnapshot.getKey());
                            if (hashMap != null)
                                matchingItems.add(hashMap);
                        }
                    }
                }
                if (matchingItems.size() > 1)
                    Collections.sort(matchingItems, new MapComparator("timestamp"));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new NotificationFragment((MainActivity)getContext())).commit();
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