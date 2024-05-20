package com.example.mymessengerapp;



import static com.google.firebase.auth.PhoneAuthProvider.*;

import android.content.Intent;
import android.credentials.Credential;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.internal.IdTokenListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.InternalTokenResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class phone_number_confirm extends AppCompatActivity {
    ImageButton back_icon;
    MaterialButton resend_code_button, continue_button;
    TextView description;
    FirebaseAuth auth;
    DatabaseReference reference;
    TextInputEditText etCode;
    Timer timer;
    String mVerificationId, phone_number;
    Object token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_confirm);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());

        back_icon = (ImageButton)findViewById(R.id.back_icon);
        resend_code_button = (MaterialButton)findViewById(R.id.resend_code_button);
        continue_button = (MaterialButton)findViewById(R.id.continue_button);
        description = (TextView)findViewById(R.id.description);
        etCode = (TextInputEditText)findViewById(R.id.etCode);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                etCode.setError(e.toString());
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken
                                           token) {

                mVerificationId = verificationId;
            }
        };


        phone_number = getIntent().getStringExtra("new_phone_number");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phone_number,
                120,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        String string = "We have sent a verification code to <b>" + phone_number + "</b>.";
        description.setText(Html.fromHtml(string));
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(phone_number_confirm.this, account_settings.class);
                startActivity(intent);
            }
        });

        resend_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+84" + phone_number,
                        120,
                        TimeUnit.SECONDS,
                        phone_number_confirm.this,
                        mCallbacks);
                new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String formattedTime = dateFormat.format(new Date(millisUntilFinished));

                        resend_code_button.setText(formattedTime);
                        resend_code_button.setBackgroundColor( ContextCompat.getColor(phone_number_confirm.this, R.color.secondary1));
                        resend_code_button.setClickable(false);
                    }
                    public void onFinish() {
                        resend_code_button.setText("Resend Verification Email");
                        resend_code_button.setBackgroundColor( ContextCompat.getColor(phone_number_confirm.this, R.color.primary));
                        resend_code_button.setClickable(true);
                    }
                }.start();
            }
        });
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = getCredential(mVerificationId, etCode.getText().toString());
                if (getIntent().getStringExtra("type").equals("add")) {
                    auth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            reference.child("phone").setValue(phone_number);
                            Intent intent = new Intent(phone_number_confirm.this, account_settings.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(phone_number_confirm.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (getIntent().getStringExtra("type").equals("change")) {
                    auth.getCurrentUser().updatePhoneNumber(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child("phone").setValue(phone_number);
                            Toast.makeText(phone_number_confirm.this, "Phone number updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(phone_number_confirm.this, account_settings.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(phone_number_confirm.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
