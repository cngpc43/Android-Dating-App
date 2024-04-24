package com.example.mymessengerapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    FirebaseAuth auth;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    Context mainActivity;

    public NotificationFragment(Context context) {
        mainActivity = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        usersArrayList = new ArrayList<>();
        adapter = new UserAdapter(mainActivity, usersArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }
}