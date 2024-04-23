package com.example.mymessengerapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserSettingFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private LinearLayout accountSettings;
    RangeSlider age_range;
    LinearLayout account_settings, location;

    Spinner gender_spinner, sexual_spinner;
    TextView age_range_preview, location_preview;

    LinearLayout logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_setting, container, false);



        location = view.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), location_change.class);
                startActivity(intent);
            }
        });
        logout = view.findViewById(R.id.logout);
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
        account_settings = view.findViewById(R.id.account_settings);
        account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), account_settings.class);
                startActivity(intent);
            }

        });



        gender_spinner = view.findViewById(R.id.gender_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.gender_spinner_item);
        adapter.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        gender_spinner.setAdapter(adapter);

        sexual_spinner = view.findViewById(R.id.sexual_spinner);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.sexual_array, R.layout.gender_spinner_item);
        adapter1.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        sexual_spinner.setAdapter(adapter1);

        location = view.findViewById(R.id.location);
        location_preview = view.findViewById(R.id.location_preview);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent intent = new Intent(getContext(), location_change.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });
        age_range_preview = view.findViewById(R.id.age_range_preview);
        age_range = view.findViewById(R.id.age_range_slider);
        age_range.setValues(13F, 24F);
        age_range_preview.setText(String.valueOf(age_range.getValues().get(0).intValue()) + "-" + String.valueOf(age_range.getValues().get(1).intValue()));

        age_range.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider rangeSlider, float v, boolean b) {
                List<Float> age_range_list = age_range.getValues();
                age_range_preview.setText(String.valueOf(age_range.getValues().get(0).intValue()) + "-" + String.valueOf(age_range.getValues().get(1).intValue()));
            }
        });
        return view;
    }
}