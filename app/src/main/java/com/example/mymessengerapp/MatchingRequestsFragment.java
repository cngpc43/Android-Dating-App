package com.example.mymessengerapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymessengerapp.adapter.MatchingRequestAdapter;
import com.example.mymessengerapp.model.MatchingItem;
import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MatchingRequestsFragment extends Fragment {

   FirebaseAuth auth;
   RecyclerView matchingRequestsRecyclerView;
   FirebaseDatabase database;
   ArrayList<String> matchingItems;
    public MatchingRequestsFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        matchingItems = new ArrayList<String>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matching_request, container, false);
        matchingRequestsRecyclerView = view.findViewById(R.id.matching_requests_recycler_view);
        matchingRequestsRecyclerView.setHasFixedSize(true);
        matchingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String currentUserId = auth.getCurrentUser().getUid();
        database.getReference("MatchRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchingItems.clear();
                String currentUserId = auth.getCurrentUser().getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (!dataSnapshot.getKey().equals(currentUserId)) {
                        DataSnapshot currentUserSnapshot = dataSnapshot.child(currentUserId);
                        if (currentUserSnapshot.exists()) {
                            String status = currentUserSnapshot.getValue(String.class);
                            if (status != null && status.equals("pending")) {
                                matchingItems.add(dataSnapshot.getKey());
                            }
                        }
                    }
                }

                MatchingRequestAdapter adapter = new MatchingRequestAdapter(getContext(), matchingItems);
                matchingRequestsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}