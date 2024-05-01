package com.example.mymessengerapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserSettingFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private LinearLayout accountSettings;
    RangeSlider age_range;
    LinearLayout account_settings, location;

    Spinner gender_spinner, sexual_spinner;
    TextView age_range_preview, location_preview, profile_username, profile_status;
    EditText et_username, et_status;
    ImageButton edit_button, save_button;
    LinearLayout logout;
    DatabaseReference reference;
    int LAUNCH_SECOND_ACTIVITY = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("user").child(auth.getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_setting, container, false);


        location = view.findViewById(R.id.location);
        logout = view.findViewById(R.id.logout);
        account_settings = view.findViewById(R.id.account_settings);
        gender_spinner = view.findViewById(R.id.gender_spinner);
        sexual_spinner = view.findViewById(R.id.sexual_spinner);
        location = view.findViewById(R.id.location);
        location_preview = view.findViewById(R.id.location_preview);
        age_range_preview = view.findViewById(R.id.age_range_preview);
        age_range = view.findViewById(R.id.age_range_slider);
        profile_username = view.findViewById(R.id.profile_username);
        profile_status = view.findViewById(R.id.profile_status);
        et_username = view.findViewById(R.id.et_username);
        et_status = view.findViewById(R.id.et_status);
        edit_button = view.findViewById(R.id.edit_button);
        save_button = view.findViewById(R.id.save_button);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.gender_spinner_item);
        adapter.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        gender_spinner.setAdapter(adapter);

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.sexual_array, R.layout.gender_spinner_item);
        adapter1.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        sexual_spinner.setAdapter(adapter1);

        // for text marquee
        profile_username.setSelected(true);
        profile_status.setSelected(true);

        // update values from database
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot: snapshot.getChildren()) {
                    // location
                    location_preview.setText(snapshot.child("location").getValue(String.class));
                    // gender
                    int gender_position = adapter.getPosition(snapshot.child("gender").getValue(String.class));
                    gender_spinner.setSelection(gender_position);
                    // sexual_orientation
                    int sexual_position = adapter1.getPosition(snapshot.child("sexual_orientation").getValue(String.class));
                    sexual_spinner.setSelection(sexual_position);
                    // age_range
                    String age_range_string = snapshot.child("age_range").getValue(String.class);
                    float age_valueFrom = Float.valueOf(age_range_string.substring(0, age_range_string.indexOf('-')));
                    float age_valueTo = Float.valueOf(age_range_string.substring(age_range_string.indexOf('-') + 1, age_range_string.length()));
                    age_range.setValues(age_valueFrom, age_valueTo);
                    age_range_preview.setText(age_range.getValues().get(0).intValue() + "-" + age_range.getValues().get(1).intValue());
                    // username
                    profile_username.setText(snapshot.child("userName").getValue(String.class));
                    // status
                    profile_status.setText(snapshot.child("status").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_button.setVisibility(View.GONE);
                save_button.setVisibility(View.VISIBLE);
                profile_username.setVisibility(View.GONE);
                profile_status.setVisibility(View.GONE);
                et_username.setVisibility(View.VISIBLE);
                et_status.setVisibility(View.VISIBLE);
                et_username.setText(profile_username.getText().toString());
                et_status.setText(profile_status.getText().toString());
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_button.setVisibility(View.GONE);
                edit_button.setVisibility(View.VISIBLE);
                reference.child("userName").setValue(et_username.getText().toString());
                reference.child("status").setValue(et_status.getText().toString());
                profile_username.setText(et_username.getText().toString());
                profile_status.setText(et_status.getText().toString());
                profile_username.setVisibility(View.VISIBLE);
                profile_status.setVisibility(View.VISIBLE);
                et_username.setVisibility(View.GONE);
                et_status.setVisibility(View.GONE);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), location_change.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext(), R.style.dialogue);
                dialog.setContentView(R.layout.dialogue_layout);
                Button no, yes;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 0, 0, 0)));

                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), login.class);
                        startActivity(intent);
//                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), account_settings.class);
                startActivity(intent);
            }

        });

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String gender = parent.getItemAtPosition(pos).toString();
                reference.child("gender").setValue(gender);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        sexual_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String sexual = parent.getItemAtPosition(pos).toString();
                reference.child("sexual_orientation").setValue(sexual);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), location_change.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });

        age_range.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider rangeSlider, float v, boolean b) {
                String string = age_range.getValues().get(0).intValue() + "-" + age_range.getValues().get(1).intValue();
                reference.child("age_range").setValue(string);
                age_range_preview.setText(string);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                location_preview.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult

}