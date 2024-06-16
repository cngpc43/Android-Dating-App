package com.example.mymessengerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

public class email_change extends AppCompatActivity {
    ImageButton back_icon;
    TextInputEditText etPassword, etEmail;
    MaterialButton continue_button;
    LinearLayout password_hint_wrong;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth auth;
    TextView password_hint;
    int attempts = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        back_icon = (ImageButton)findViewById(R.id.back_icon);
        etPassword = (TextInputEditText)findViewById(R.id.etPassword);
        etEmail = (TextInputEditText)findViewById(R.id.etEmail);
        continue_button = (MaterialButton)findViewById(R.id.continue_button);
        password_hint_wrong = (LinearLayout)findViewById(R.id.password_hint_wrong);
        password_hint = (TextView)findViewById(R.id.password_hint);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = etEmail.getText().toString();
                String Pass = etPassword.getText().toString();

                if ((TextUtils.isEmpty(Pass))) {
                    Toast.makeText(email_change.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (Pass.length() < 6) {
                    Toast.makeText(email_change.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Email)) {
                    etEmail.setError("Please enter your new email address");
                } else if (!Email.matches(emailPattern)) {
                    etEmail.setError("Give proper email address");
                } else if (Email.equals(auth.getCurrentUser().getEmail().toString())) {
                    etEmail.setError("This is your current email address");
                } else {
                    // check if new email is already existed
                    auth.fetchSignInMethodsForEmail(Email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (!task.getResult().getSignInMethods().isEmpty()) {
                                etEmail.setError("This email address has been taken by another account");
                            } else {
                                // new email is not existed, check for password confirmation
                                // incorrect password confirmation over 3 times
                                if (attempts > 3) {
                                    Toast.makeText(email_change.this, "Too many attempts, please log out and try again later.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(email_change.this, login.class);
                                    // clear all previous activities
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    finish();
                                }
                                // reauthenticate current user for email change
                                auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail().toString(), Pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Intent intent = new Intent(email_change.this, email_confirm.class);
                                        intent.putExtra("new_email", Email);
                                        intent.putExtra("pass", Pass);
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
        });

    }
}