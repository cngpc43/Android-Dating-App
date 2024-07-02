package com.example.mymessengerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import com.example.mymessengerapp.adapter.ListViewAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class location_change extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    ImageButton back_icon;
    MaterialButton set_location_button;
    ListView cities_listview;
    RelativeLayout searchBar;
    EditText etLocation;
    String[] cities;
    List<String> citiesList;
    FirebaseAuth auth;
    DatabaseReference reference;
    FusedLocationProviderClient fusedLocationProviderClient;
    HashMap<String, Pair<Double, Double>> provincesCoordinates = new HashMap<>();

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
        set_location_button = findViewById(R.id.set_location_button);

        cities = getResources().getStringArray(R.array.vn_cities_array);
        citiesList = new LinkedList<>(Arrays.asList(cities));
        ListViewAdapter adapter = new ListViewAdapter(this, citiesList);
        cities_listview.setAdapter(adapter);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cities_listview.setTextFilterEnabled(true);
        cities_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FirebaseDatabase.getInstance().getReference().child("ProvincesCoordinates/VN/" + adapter.getItem(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.child("location").setValue(adapter.getItem(position));
                            reference.child("latitude").setValue(task.getResult().child("latitude").getValue(String.class));
                            reference.child("longitude").setValue(task.getResult().child("longitude").getValue(String.class));
                            finish();
                        } else {
                            Toast.makeText(location_change.this, "Failed to get coordinates of location "
                                    + adapter.getItem(position) + ", please check your internet connection", Toast.LENGTH_SHORT).show();
                            Log.d("location_change", task.getException().getMessage());
                        }
                    }
                });
            }
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(etLocation.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing
            }
        });


        set_location_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!hasLocationPermission()) {
                    requestLocationPermission();
                    Toast.makeText(location_change.this, "Please grant location permission first!", Toast.LENGTH_SHORT).show();
                } else {
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(location_change.this);
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Geocoder geocoder = new Geocoder(location_change.this);
                            List<Address> currentLocation = null;
                            try {
                                currentLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                reference.child("location").setValue(currentLocation.get(0).getAdminArea());
                                reference.child("latitude").setValue(String.valueOf(location.getLatitude()));
                                reference.child("longitude").setValue(String.valueOf(location.getLongitude()));
                                Toast.makeText(location_change.this, "Sucess, your current location is " + currentLocation.get(0).getAdminArea(),
                                        Toast.LENGTH_SHORT).show();
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result", currentLocation.get(0).getAdminArea());
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        });


    }

    private boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    private void requestLocationPermission() {
        EasyPermissions.requestPermissions(this, "Please allow location permission to sync your exact location",
                1, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }


    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Toast.makeText(location_change.this, "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        Toast.makeText(location_change.this, "Permission denied", Toast.LENGTH_SHORT).show();
    }
}
