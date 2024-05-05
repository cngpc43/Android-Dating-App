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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

public class phone_number_add extends AppCompatActivity {
    ImageButton back_icon;
    TextInputEditText etPassword, etEmail;
    MaterialButton continue_button;
    LinearLayout password_hint_wrong;
    FirebaseAuth auth;
    TextView password_hint, title, email;
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
        title = (TextView)findViewById(R.id.title);
        email = (TextView)findViewById(R.id.email);

        title.setText("Add your phone number");
        email.setText("Enter your phone number:");

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(phone_number_add.this, account_settings.class);
                startActivity(intent);
            }
        });

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Phone = etEmail.getText().toString();
                String Pass = etPassword.getText().toString();

                if ((TextUtils.isEmpty(Pass))) {
                    Toast.makeText(phone_number_add.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (Pass.length() < 6) {
                    Toast.makeText(phone_number_add.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Phone)) {
                    etEmail.setError("Please enter your phone number");
                } else if (Phone.length() < 10 || Phone.length() > 11) {
                    etEmail.setError("Give proper phone number");
                } else {
                    // reauthenticate current user
                    auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail().toString(), Pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(phone_number_add.this, phone_number_confirm.class);
                            intent.putExtra("new_phone_number", Phone);
                            intent.putExtra("type", "add");
                            startActivity(intent);
                            finish();
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