package com.example.mymessengerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymessengerapp.model.Users;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccountWarningFragment extends Fragment {

    MaterialButton setup_button;
    TextView title;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;
    DatabaseReference reference;
    FirebaseAuth auth;
    ValueEventListener valueEventListener;
    public AccountWarningFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_warning, container, false);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user/" + auth.getCurrentUser().getUid());

        // load layout from XML
        setup_button = view.findViewById(R.id.setup_button);
        title = getActivity().findViewById(R.id.title);
        home_selected = getActivity().findViewById(R.id.home_selected);
        chat_selected = getActivity().findViewById(R.id.chat_selected);
        noti_selected = getActivity().findViewById(R.id.noti_selected);
        user_selected = getActivity().findViewById(R.id.user_selected);

        reference.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = new Users(dataSnapshot.child("userId").getValue(String.class), dataSnapshot.child("userName").getValue(String.class),
                        dataSnapshot.child("mail").getValue(String.class), dataSnapshot.child("password").getValue(String.class),
                        dataSnapshot.child("profilepic").getValue(String.class), dataSnapshot.child("status").getValue(String.class),
                        dataSnapshot.child("gender").getValue(String.class), dataSnapshot.child("dob").getValue(String.class),
                        dataSnapshot.child("phone").getValue(String.class), dataSnapshot.child("location").getValue(String.class),
                        dataSnapshot.child("sexual_orientation").getValue(String.class), dataSnapshot.child("height").getValue(String.class),
                        dataSnapshot.child("age_range").getValue(String.class), dataSnapshot.child("gender_show").getValue(String.class),
                        dataSnapshot.child("show_me").getValue(Boolean.class), new ArrayList<String>(),
                        dataSnapshot.child("latitude").getValue(String.class), dataSnapshot.child("longitude").getValue(String.class),
                        dataSnapshot.child("isOnline").getValue(String.class), dataSnapshot.child("location_distance").getValue(String.class));

                if (user != null) {
                    if (dataSnapshot.child("photos").hasChildren()) {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        for (DataSnapshot photoSnapshot : dataSnapshot.child("photos").getChildren()) {
                            if (photoSnapshot.getValue(String.class) != null)
                                arrayList.add(photoSnapshot.getValue(String.class));
                        }
                        user.setPhotos(arrayList);
                    }

                    if (user.getDob() == null || user.getDob().equals(""))
                        Toast.makeText(getActivity(), "Please set your date of birth", Toast.LENGTH_SHORT).show();
                    else if (user.getGender() == null || user.getGender().equals("") || user.getGender().equals("none"))
                        Toast.makeText(getActivity(), "Please set your gender", Toast.LENGTH_SHORT).show();
                    else if (user.getAge_range() == null || user.getAge_range().equals(""))
                        Toast.makeText(getActivity(), "Please set your desired age range", Toast.LENGTH_SHORT).show();
                    else if (user.getGender_show() == null || user.getGender_show().equals(""))
                        Toast.makeText(getActivity(), "Please set your desired gender", Toast.LENGTH_SHORT).show();
                    else if (user.getPhotos().size() <= 0)
                        Toast.makeText(getActivity(), "Please add your photos", Toast.LENGTH_SHORT).show();
                    else if (user.getLocation() == null || user.getLocation().equals("") || user.getLatitude() == null ||
                            user.getLatitude().equals("") || user.getLongitude() == null || user.getLongitude().equals(""))
                        Toast.makeText(getActivity(), "Please add location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        setup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, new UserSettingFragment());
                transaction.commit();
                title.setText("User");
                home_selected.setBackground(null);
                noti_selected.setBackground(null);
                chat_selected.setBackground(null);
                user_selected.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selected_nav_item));
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