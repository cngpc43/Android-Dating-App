package com.example.mymessengerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    Context mainActivity;
    ArrayList<Users> usersArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity=mainActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        // Bind the data to the ViewHolder
        Users user = usersArrayList.get(position);
        // Update the ViewHolder based on the data of "user"
    }

    @Override
    public int getItemCount() {
        // Return the total number of items
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        // Define your item views here

        public viewholder(@NonNull View itemView) {
            super(itemView);
            // Initialize your item views here
        }
    }
}
