package com.example.mymessengerapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class notification_page extends AppCompatActivity {
    LinearLayout home_selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_main, null);
        home_selected = layout.findViewById(R.id.home_selected);
//        noti_selected.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_nav_item));


    }

}
