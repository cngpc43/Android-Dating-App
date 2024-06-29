package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import okhttp3.internal.Util;

public class password_change extends AppCompatActivity {
    ImageButton back_icon;
    TextInputEditText etPassword, etNewPassword, reEtNewPassword;
    MaterialButton change_button;
    LinearLayout password_hint_wrong, re_newpass_hint_true, re_newpass_hint_false;
    TextView password_hint;
    FirebaseAuth auth;
    DatabaseReference reference;
    boolean newPassConfirm = false;
    int attempts = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user/" + auth.getCurrentUser().getUid() + "/password");

        back_icon = findViewById(R.id.back_icon);
        etPassword = findViewById(R.id.etPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        reEtNewPassword = findViewById(R.id.reEtNewPassword);
        change_button = findViewById(R.id.change_button);
        password_hint = findViewById(R.id.password_hint);
        password_hint_wrong = findViewById(R.id.password_hint_wrong);
        re_newpass_hint_true = findViewById(R.id.re_newpass_hint_true);
        re_newpass_hint_false = findViewById(R.id.re_newpass_hint_false);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reEtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (reEtNewPassword.getText().toString().equals(etNewPassword.getText().toString())) {
                    re_newpass_hint_true.setVisibility(View.VISIBLE);
                    re_newpass_hint_false.setVisibility(View.GONE);
                    newPassConfirm = true;
                } else {
                    re_newpass_hint_true.setVisibility(View.GONE);
                    re_newpass_hint_false.setVisibility(View.VISIBLE);
                    newPassConfirm = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing
            }
        });

        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attempts > 3) {
                    Toast.makeText(password_change.this, "Too many attempts, please log out and try again later.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(password_change.this, login.class);
                    // clear all previous activities
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                }
                if (!newPassConfirm) {
                    Toast.makeText(password_change.this, "New password confirmation does not match", Toast.LENGTH_SHORT).show();
                } else {
                    String pass = etPassword.getText().toString();
                    String new_pass = etNewPassword.getText().toString();
                    if (TextUtils.isEmpty(pass)) {
                        Toast.makeText(password_change.this, "Please enter your current password", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(new_pass)) {
                        Toast.makeText(password_change.this, "Please enter your new password", Toast.LENGTH_SHORT).show();
                    } else if (pass.length() < 6) {
                        Toast.makeText(password_change.this, "Current password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                    } else if (new_pass.length() < 6) {
                        Toast.makeText(password_change.this, "New password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                    } else if (pass.equals(new_pass)) {
                        Toast.makeText(password_change.this, "New password must be different from current password", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail().toString(), pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    auth.getCurrentUser().updatePassword(new_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // update new password to Firebase Realtime Database
                                            reference.setValue(new_pass);

                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user/" + auth.getCurrentUser().getUid() + "/isOnline");
                                            userRef.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    auth.signOut();
                                                }
                                            });

                                            Intent intent = new Intent(password_change.this, password_change_success.class);
                                            // clear all previous activities
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                } else {
                                    password_hint_wrong.setVisibility(View.VISIBLE);
                                    attempts++;
                                    password_hint.setText("Wrong password, you have " + String.valueOf(4 - attempts) + " attempts left.");
                                    Log.d("error", task.getException().toString());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
