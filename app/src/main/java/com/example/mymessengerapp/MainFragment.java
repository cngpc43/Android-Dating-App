package com.example.mymessengerapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.UserDistanceComparator;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    ImageView ic_home, ic_chat, ic_noti, ic_user;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;
    TextView title;

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
        title = getActivity().findViewById(R.id.title);
        home_selected = getActivity().findViewById(R.id.home_selected);
        noti_selected = getActivity().findViewById(R.id.noti_selected);
        chat_selected = getActivity().findViewById(R.id.chat_selected);
        user_selected = getActivity().findViewById(R.id.user_selected);
        ic_home = getActivity().findViewById(R.id.icon_home);
        ic_chat = getActivity().findViewById(R.id.icon_chat);
        ic_noti = getActivity().findViewById(R.id.icon_noti);
        ic_user = getActivity().findViewById(R.id.icon_user);

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

        title.setText("Tindeo");

        noti_selected.setBackground(null);
        chat_selected.setBackground(null);
        user_selected.setBackground(null);
        home_selected.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selected_nav_item));

        ic_chat.setColorFilter(Color.BLACK);
        ic_noti.setColorFilter(Color.BLACK);
        ic_user.setColorFilter(Color.BLACK);
        ic_home.setColorFilter(Color.rgb(236, 83, 131));


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
                                dataSnapshot.child("show_me").getValue(Boolean.class), new ArrayList<String>(),
                                dataSnapshot.child("latitude").getValue(String.class), dataSnapshot.child("longitude").getValue(String.class),
                                "", dataSnapshot.child("location_distance").getValue(String.class));
                        Object isOnline = dataSnapshot.child("isOnline").getValue(Object.class);
                        if (isOnline != null) {
                            if (isOnline.equals("true"))
                                currentUser.setIsOnline("true");
                            else
                                currentUser.setIsOnline(isOnline.toString());
                        }

                        if (dataSnapshot.child("photos").hasChildren()) {
                            ArrayList<String> arrayList = new ArrayList<String>();
                            for (DataSnapshot photoSnapshot : dataSnapshot.child("photos").getChildren()) {
                                if (photoSnapshot.getValue(String.class) != null)
                                    arrayList.add(photoSnapshot.getValue(String.class));
                            }
                            currentUser.setPhotos(arrayList);
                        }

                        if (!checkForAccountSetup(currentUser)) {
                            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                                getActivity().getSupportFragmentManager().popBackStack();
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
                                                dataSnapshot2.child("show_me").getValue(Boolean.class), new ArrayList<String>(),
                                                dataSnapshot2.child("latitude").getValue(String.class), dataSnapshot2.child("longitude").getValue(String.class),
                                                "", dataSnapshot2.child("location_distance").getValue(String.class));
                                        Object isOnline = dataSnapshot2.child("isOnline").getValue(Object.class);
                                        if (isOnline != null) {
                                            if (isOnline.equals("true"))
                                                users.setIsOnline("true");
                                            else
                                                users.setIsOnline(isOnline.toString());
                                        }
                                        if (dataSnapshot2.child("photos").hasChildren()) {
                                            ArrayList<String> arrayList = new ArrayList<String>();
                                            for (DataSnapshot photoSnapshot : dataSnapshot2.child("photos").getChildren()) {
                                                if (photoSnapshot.getValue(String.class) != null)
                                                    arrayList.add(photoSnapshot.getValue(String.class));
                                            }
                                            users.setPhotos(arrayList);
                                        }
                                        if (users != null) {
                                            // filter user based on below conditions
                                            // userID != current userID, user.isShow_me = true, user is not in current user RequestSent list, and user has set up their account
                                            if (!users.getUserId().equals(currentUserId) && users.isShow_me() && !requestList.contains(users.getUserId()) && checkForAccountSetup(users)) {
                                                // current user age range
                                                int startAge = Integer.valueOf(currentUser.getAge_range().substring(0, currentUser.getAge_range().indexOf("-")));
                                                int endAge = Integer.valueOf(currentUser.getAge_range().substring(currentUser.getAge_range().indexOf("-") + 1, currentUser.getAge_range().length()));
                                                // another user age
                                                int userAge = Calendar.getInstance().get(Calendar.YEAR) - Integer.valueOf(users.getDob().substring(users.getDob().indexOf(",") + 2, users.getDob().length()));

                                                // user is in current user desired age range
                                                if (userAge >= startAge && userAge <= endAge) {
                                                    // user gender is suitable with current user desired gender show
                                                    if (currentUser.getGender_show().equals(users.getGender()) || currentUser.getGender_show().equals("Everyone")) {
                                                        // user location is in current user desired location distance
                                                        float[] distance = new float[1];
                                                        Location.distanceBetween(Double.valueOf(currentUser.getLatitude()), Double.valueOf(currentUser.getLongitude()),
                                                                Double.valueOf(users.getLatitude()), Double.valueOf(users.getLongitude()), distance);
                                                        Log.d("distance_between_users", String.valueOf(distance[0]/1000));
                                                        if (distance[0]/1000 <= Float.valueOf(currentUser.getLocation_distance())) {
                                                            usersArrayList.add(users);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (usersArrayList.size() < 1) {
                                        if (getActivity() != null)
                                            Toast.makeText(getActivity(), "No users match your requests, try changing your desired gender, age range or location", Toast.LENGTH_LONG).show();
                                    } else {
                                        // sort userList by the distance to currentUser (ascending)
                                        Collections.sort(usersArrayList, new UserDistanceComparator(currentUser));
                                    }
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


    // function to check if user has set up information for dating or not (dob, age_range, gender_show, gender, photos, location, latitude, longitude)
    public Boolean checkForAccountSetup(Users user) {
        if (user.getDob() == null || user.getDob().equals(""))
            return false;
        if (user.getAge_range() == null || user.getAge_range().equals(""))
            return false;
        if (user.getGender_show() == null || user.getGender_show().equals(""))
            return false;
        if (user.getGender() == null || user.getGender().equals("") || user.getGender().equals("none"))
            return false;
        if (user.getPhotos().size() <= 0)
            return false;
        if (user.getLocation() == null || user.getLocation().equals("") || user.getLatitude() == null ||
                user.getLatitude().equals("") || user.getLongitude() == null || user.getLongitude().equals(""))
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