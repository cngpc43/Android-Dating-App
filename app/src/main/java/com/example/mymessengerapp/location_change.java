package com.example.mymessengerapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.search.SearchBar;

public class location_change extends AppCompatActivity {
    ImageView back_icon;
    ListView cities_listview;
    SearchBar sb_search;
    String[] cities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_change);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        cities = getResources().getStringArray(R.array.vn_cities_array);
        back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(location_change.this, setting.class);
                startActivity(intent);
            }
        });

        cities_listview = findViewById(R.id.cities_listview);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.vn_cities_array, R.layout.gender_spinner_dropdown);
        cities_listview.setAdapter(adapter);

        cities_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", cities[position]);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        sb_search = findViewById(R.id.sb_search);
    }
}
