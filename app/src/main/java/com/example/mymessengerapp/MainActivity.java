package com.example.mymessengerapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    //    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    FrameLayout home, user, message, notification;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        home_selected = findViewById(R.id.home_selected);
        noti_selected = findViewById(R.id.noti_selected);
        chat_selected = findViewById(R.id.chat_selected);
        user_selected = findViewById(R.id.user_selected);
        home_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));
        usersArrayList = new ArrayList<>();
        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                String currentUserId = auth.getCurrentUser().getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users != null && !users.getUserId().equals(currentUserId)) {
                        usersArrayList.add(users);
                    }
                }
                adapter = new UserAdapter(MainActivity.this, usersArrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }
        loadFragment(new MainFragment());
        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UserSettingFragment());
                home_selected.setBackground(null);
                noti_selected.setBackground(null);
                chat_selected.setBackground(null);
                user_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
            }
        });
        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ChatFragment());
                home_selected.setBackground(null);
                noti_selected.setBackground(null);
                user_selected.setBackground(null);
                chat_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
            }
        });
        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new MainFragment());
                noti_selected.setBackground(null);
                chat_selected.setBackground(null);
                user_selected.setBackground(null);
                home_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
            }
        });
        notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new NotificationFragment(MainActivity.this));
                home_selected.setBackground(null);
                chat_selected.setBackground(null);
                user_selected.setBackground(null);
                noti_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    }
}