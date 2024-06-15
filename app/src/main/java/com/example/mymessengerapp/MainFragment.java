package com.example.mymessengerapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainFragment extends Fragment {
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ValueEventListener valueEventListener;
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference reference;
    ArrayList<String> requestList;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        usersArrayList = new ArrayList<>();
        requestList = new ArrayList<>();
        adapter = new UserAdapter( getContext(), usersArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mainUserRecyclerView = view.findViewById(R.id.mainUserRecyclerView);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._16sdp);
        mainUserRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);

        layoutManager.setStackFromEnd(true);
        mainUserRecyclerView.setLayoutManager(layoutManager);
        mainUserRecyclerView.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mainUserRecyclerView);
        reference = database.getReference();

        auth.addAuthStateListener(authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    if (reference != null && valueEventListener != null) {
                        reference.removeEventListener(valueEventListener);
                    }
                }
            }
        });
        reference.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                String currentUserId = auth.getCurrentUser().getUid();

                // check if current account has set up information for dating or not (dob, age_range, gender_show, gender, photos)
                FirebaseDatabase.getInstance().getReference().child("user/" + currentUserId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Users currentUser = new Users(dataSnapshot.child("userId").getValue(String.class), dataSnapshot.child("userName").getValue(String.class),
                                dataSnapshot.child("mail").getValue(String.class), dataSnapshot.child("password").getValue(String.class),
                                dataSnapshot.child("profilepic").getValue(String.class), dataSnapshot.child("status").getValue(String.class),
                                dataSnapshot.child("gender").getValue(String.class), dataSnapshot.child("dob").getValue(String.class),
                                dataSnapshot.child("phone").getValue(String.class), dataSnapshot.child("location").getValue(String.class),
                                dataSnapshot.child("sexual_orientation").getValue(String.class), dataSnapshot.child("height").getValue(String.class),
                                dataSnapshot.child("age_range").getValue(String.class), dataSnapshot.child("gender_show").getValue(String.class),
                                dataSnapshot.child("show_me").getValue(Boolean.class), 0);

                        if (dataSnapshot.hasChild("photos"))
                            currentUser.setNum_of_photo((int) dataSnapshot.child("photos").getChildrenCount());

                        if (!checkForAccountSetup(currentUser)) {
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.main_frame, new AccountWarningFragment());
                            transaction.commit();
                        } else {
                            // get MatchRequest list
                            FirebaseDatabase.getInstance().getReference().child("MatchRequests/").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    requestList.clear();
                                    if (task.isSuccessful()) {
                                        for (DataSnapshot dataSnapshot1 : task.getResult().getChildren()) {
                                            if (dataSnapshot1.child("status").getValue(String.class) != null && dataSnapshot1.child("requesterId").getValue(String.class) != null
                                                    && dataSnapshot1.child("recipientId").getValue(String.class) != null) {
                                                if (dataSnapshot1.child("status").getValue(String.class).equals("declined") || dataSnapshot1.child("status").getValue(String.class).equals("accepted")) {
                                                    if (dataSnapshot1.child("requesterId").getValue(String.class).equals(currentUserId))
                                                        requestList.add(dataSnapshot1.child("recipientId").getValue(String.class));
                                                    else if (dataSnapshot1.child("recipientId").getValue(String.class).equals(currentUserId))
                                                        requestList.add(dataSnapshot1.child("requesterId").getValue(String.class));
                                                } else if (dataSnapshot1.child("status").getValue(String.class).equals("pending")) {
                                                    if (dataSnapshot1.child("requesterId").getValue(String.class).equals(currentUserId))
                                                        requestList.add(dataSnapshot1.child("recipientId").getValue(String.class));
                                                }
                                            }
                                        }
                                    }
                                    // get all users from Firebase Database/user
                                    for (DataSnapshot dataSnapshot2 : snapshot.child("user").getChildren()) {
                                        Users users = new Users(dataSnapshot2.child("userId").getValue(String.class), dataSnapshot2.child("userName").getValue(String.class),
                                                dataSnapshot2.child("mail").getValue(String.class), dataSnapshot2.child("password").getValue(String.class),
                                                dataSnapshot2.child("profilepic").getValue(String.class), dataSnapshot2.child("status").getValue(String.class),
                                                dataSnapshot2.child("gender").getValue(String.class), dataSnapshot2.child("dob").getValue(String.class),
                                                dataSnapshot2.child("phone").getValue(String.class), dataSnapshot2.child("location").getValue(String.class),
                                                dataSnapshot2.child("sexual_orientation").getValue(String.class), dataSnapshot2.child("height").getValue(String.class),
                                                dataSnapshot2.child("age_range").getValue(String.class), dataSnapshot2.child("gender_show").getValue(String.class),
                                                dataSnapshot2.child("show_me").getValue(Boolean.class), 0);
                                        if (dataSnapshot2.hasChild("photos"))
                                            users.setNum_of_photo((int) dataSnapshot.child("photos").getChildrenCount());
                                        if (users != null) {
                                            if (!users.getUserId().equals(currentUserId) && users.isShow_me() && !requestList.contains(users.getUserId()) && checkForAccountSetup(users)) {
                                                // current user age range
                                                int startAge = Integer.valueOf(currentUser.getAge_range().substring(0, currentUser.getAge_range().indexOf("-")));
                                                int endAge = Integer.valueOf(currentUser.getAge_range().substring(currentUser.getAge_range().indexOf("-") + 1, currentUser.getAge_range().length()));
                                                // another user age
                                                int userAge = Calendar.getInstance().get(Calendar.YEAR) - Integer.valueOf(users.getDob().substring(users.getDob().indexOf(",") + 2, users.getDob().length()));

                                                if (userAge >= startAge && userAge <= endAge && currentUser.getGender_show().contains(users.getGender()))
                                                    usersArrayList.add(users);
                                            }
                                        }
                                    }
                                    if (usersArrayList.size() == 0)
                                        Toast.makeText(getActivity(), "No users match your requests, try changing your desired gender, age range or location", Toast.LENGTH_LONG).show();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    // function to check if user has set up information for dating or not (dob, age_range, gender_show, gender, photos)
    public Boolean checkForAccountSetup(Users user) {
        if (user.getDob() == null || user.getDob().equals(""))
            return false;
        if (user.getAge_range() == null || user.getAge_range().equals(""))
            return false;
        if (user.getGender_show() == null || user.getGender_show().equals(""))
            return false;
        if (user.getGender() == null || user.getGender().equals("") || user.getGender().equals("none"))
            return false;
        if (user.getNum_of_photo() <= 0)
            return false;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (auth != null && authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
        if (reference != null && valueEventListener != null)
            reference.removeEventListener(valueEventListener);
    }
}