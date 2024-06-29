package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAnotherProfile extends AppCompatActivity {
    CircleImageView anotherAvt;
    TextView anotherName, anotherStt, anotherLocation, anotherGender, anotherDob;
    GridView anotherPhotos;
    DatabaseReference reference;
    ImageButton back_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_another_profile);

        // Get user
        reference = FirebaseDatabase.getInstance().getReference("user/" + getIntent().getStringExtra("userId"));

        // Get UI element
        back_btn = findViewById(R.id.back_icon);
        anotherAvt = findViewById(R.id.another_avt);
        anotherName = findViewById(R.id.another_name);
        anotherLocation = findViewById(R.id.another_location);
        anotherGender = findViewById(R.id.another_gender);
        anotherStt = findViewById(R.id.another_status);
        anotherDob = findViewById(R.id.another_dob);
        anotherPhotos = findViewById(R.id.another_photos);

        // Get another user info from firebase
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set data to UI
                anotherName.setText(snapshot.child("userName").getValue(String.class));
                String profileUri = snapshot.child("profilepic").getValue(String.class);
                if (profileUri == null) {
                    FirebaseStorage.getInstance().getReference().child("default.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(anotherAvt);
                        }
                    });
                } else {
                    Picasso.get().load(profileUri).into(anotherAvt);
                }
                anotherStt.setText(snapshot.child("status").getValue(String.class));
                anotherDob.setText(snapshot.child("dob").getValue(String.class));
                anotherGender.setText(snapshot.child("gender").getValue(String.class));
                if (snapshot.child("gender").getValue(String.class).equals("Female"))
                    anotherGender.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.female_svgrepo_com, 0, 0, 0);
                anotherLocation.setText(snapshot.child("location").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAnotherProfile.this, "Get profile failed !!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle back button
        back_btn.setOnClickListener(v -> onBackPressed());
    }
}