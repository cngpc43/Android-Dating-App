package com.example.mymessengerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymessengerapp.adapter.ListViewAdapter;
import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class location_change extends AppCompatActivity {
    ImageButton back_icon;
    ListView cities_listview;

    RelativeLayout searchBar;
    EditText etLocation;
    String[] cities;
    List<String> citiesList;
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


        cities_listview = findViewById(R.id.cities_listview);
        back_icon = findViewById(R.id.back_icon);
        searchBar = findViewById(R.id.searchBar);
        etLocation = findViewById(R.id.etLocation);

        cities = getResources().getStringArray(R.array.vn_cities_array);
        citiesList = new LinkedList<>(Arrays.asList(cities));
        ListViewAdapter adapter = new ListViewAdapter(this, citiesList);
        cities_listview.setAdapter(adapter);


        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(location_change.this, user_manage.class);
                startActivity(intent);
            }
        });

        cities_listview.setTextFilterEnabled(true);
        cities_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                reference.child("location").setValue(adapter.getItem(position));
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", adapter.getItem(position));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                adapter.filter(str);
            }
        });

    }
}
