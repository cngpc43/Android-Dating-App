package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class username_change extends AppCompatActivity {
    ImageView back_icon;
    TextView email, title, password;
    TextInputEditText etEmail;
    TextInputLayout editPassword;
    MaterialButton continue_button;
    DatabaseReference reference;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());
        email = findViewById(R.id.email);
        title = findViewById(R.id.title);
        password = findViewById(R.id.password);
        editPassword = findViewById(R.id.editPassword);
        back_icon = findViewById(R.id.back_icon);
        continue_button = findViewById(R.id.continue_button);
        etEmail = findViewById(R.id.etEmail);

        password.setVisibility(View.GONE);
        editPassword.setVisibility(View.GONE);
        continue_button.setText("Save");
        title.setText("Change username");
        email.setText("Enter your new username:");
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(username_change.this, account_settings.class);
                startActivity(intent);
            }
        });

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("userName").setValue(etEmail.getText().toString());
                Intent intent = new Intent(username_change.this, account_settings.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
