package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHeightFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference reference;
    ImageButton back_button, save_button;
    TextInputEditText editTextHeight;
    MaterialButton remove_height_button;
    View transparent_top_view;
    public UserHeightFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_height, container, false);

        transparent_top_view = view.findViewById(R.id.transparent_top_view);
        back_button = view.findViewById(R.id.back_button);
        save_button = view.findViewById(R.id.save_button);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        remove_height_button = view.findViewById(R.id.remove_height_button);

        // load height value from Database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot: snapshot.getChildren()) {
                    if (snapshot.child("height").getValue(String.class).equals("")) {
                        remove_height_button.setVisibility(View.INVISIBLE);
                    } else {
                        editTextHeight.setText(snapshot.child("height").getValue(String.class));
                        remove_height_button.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        transparent_top_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new UserSettingFragment());
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new UserSettingFragment());
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String height = editTextHeight.getText().toString();
                if (TextUtils.isEmpty(height)) {
                    editTextHeight.setError("Please enter your height");
                } else {
                    reference.child("height").setValue(height);
                    loadFragment(new UserSettingFragment());
                }
            }
        });

        remove_height_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextHeight.getText().clear();
                reference.child("height").setValue("");
                loadFragment(new UserSettingFragment());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    }
}
