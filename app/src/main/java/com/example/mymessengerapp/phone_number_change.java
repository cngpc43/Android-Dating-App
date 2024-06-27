package com.example.mymessengerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class phone_number_change extends AppCompatActivity {
    ImageButton back_icon;
    TextInputEditText etPassword, etEmail;
    MaterialButton continue_button;
    TextInputLayout textLayoutEmail;
    LinearLayout password_hint_wrong;
    FirebaseAuth auth;
    TextView password_hint, title;
    int attempts = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        back_icon = (ImageButton) findViewById(R.id.back_icon);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);
        etEmail = (TextInputEditText) findViewById(R.id.etEmail);
        continue_button = (MaterialButton) findViewById(R.id.continue_button);
        password_hint_wrong = (LinearLayout) findViewById(R.id.password_hint_wrong);
        password_hint = (TextView) findViewById(R.id.password_hint);
        title = (TextView) findViewById(R.id.title);
        textLayoutEmail = (TextInputLayout) findViewById(R.id.editEmail);

        title.setText("Change your phone number");
        textLayoutEmail.setHint("Enter your new phone number");
        etEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_local_phone_24, 0, 0, 0);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Phone = etEmail.getText().toString();
                String Pass = etPassword.getText().toString();

                if ((TextUtils.isEmpty(Pass))) {
                    Toast.makeText(phone_number_change.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (Pass.length() < 6) {
                    Toast.makeText(phone_number_change.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Phone)) {
                    etEmail.setError("Please enter your new phone number");
                } else if (Phone.length() < 10 || Phone.length() > 11) {
                    etEmail.setError("Give proper phone number");
                } else if (Phone.equals(auth.getCurrentUser().getPhoneNumber())) {
                    etEmail.setError("This is your current phone number");
                } else {
                    // reauthenticate current user
                    auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail().toString(), Pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(phone_number_change.this, phone_number_confirm.class);
                            intent.putExtra("new_phone_number", Phone);
                            intent.putExtra("type", "change");
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            password_hint_wrong.setVisibility(View.VISIBLE);
                            attempts++;
                            password_hint.setText("Wrong password, you have " + String.valueOf(4 - attempts) + " attempts left.");
                        }
                    });
                }
            }
        });

    }
}