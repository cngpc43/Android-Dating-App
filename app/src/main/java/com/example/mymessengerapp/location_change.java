package com.example.mymessengerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class location_change extends AppCompatActivity {
    ImageButton back_icon;
    ListView cities_listview;
    SearchBar sb_search;
    String[] cities;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_change);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());

        cities = getResources().getStringArray(R.array.vn_cities_array);
        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(location_change.this, user_manage.class);
                startActivity(intent);
            }
        });

        cities_listview = findViewById(R.id.cities_listview);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.vn_cities_array, R.layout.gender_spinner_dropdown);
        cities_listview.setAdapter(adapter);

        cities_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                reference.child("location").setValue(cities[position]);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", cities[position]);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        sb_search = findViewById(R.id.sb_search);
    }
}
