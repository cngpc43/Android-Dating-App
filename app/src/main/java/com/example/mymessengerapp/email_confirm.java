package com.example.mymessengerapp;



import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.IdTokenListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.InternalTokenResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class email_confirm extends AppCompatActivity {
    ImageButton back_icon;
    MaterialButton resend_email_button, continue_button;
    TextView description;
    FirebaseAuth auth;
    DatabaseReference mDatabase;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirm);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        back_icon = (ImageButton)findViewById(R.id.back_icon);
        resend_email_button = (MaterialButton)findViewById(R.id.resend_email_button);
        continue_button = (MaterialButton)findViewById(R.id.continue_button);
        description = (TextView)findViewById(R.id.description);

        String string = "We have sent a verification link to <b>" + getIntent().getStringExtra("new_email") + "</b>.";
        description.setText(Html.fromHtml(string));
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(email_confirm.this, account_settings.class);
                startActivity(intent);
            }
        });

        auth.getCurrentUser().verifyBeforeUpdateEmail(getIntent().getStringExtra("new_email"));

        resend_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().verifyBeforeUpdateEmail(getIntent().getStringExtra("new_email"));
                new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String formattedTime = dateFormat.format(new Date(millisUntilFinished));

                        resend_email_button.setText(formattedTime);
                        resend_email_button.setBackgroundColor( ContextCompat.getColor(email_confirm.this, R.color.secondary1));
                        resend_email_button.setClickable(false);
                    }
                    public void onFinish() {
                        resend_email_button.setText("Resend Verification Email");
                        resend_email_button.setBackgroundColor( ContextCompat.getColor(email_confirm.this, R.color.primary));
                        resend_email_button.setClickable(true);
                    }
                }.start();
            }
        });
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().reload();
                auth.addIdTokenListener(new IdTokenListener() {
                    @Override
                    public void onIdTokenChanged(@NonNull InternalTokenResult internalTokenResult) {
                        Intent intent = new Intent(email_confirm.this, email_change_success.class);
                        startActivity(intent);
                        finish();
                    }
                });
                Toast.makeText(email_confirm.this, "Email has not been verified yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
