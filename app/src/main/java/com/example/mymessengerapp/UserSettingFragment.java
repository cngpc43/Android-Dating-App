package com.example.mymessengerapp;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSettingFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ValueEventListener listener;
    RangeSlider age_range;
    LinearLayout account_settings, location, height, dob, add_photo;
    Spinner gender_spinner, sexual_spinner, gender_show_spinner;
    TextView age_range_preview, location_preview, profile_username, profile_status, height_preview, dob_preview, photo_count;
    EditText et_username, et_status;
    CircleImageView profile_pic, profile_pic_button;
    ImageButton edit_button, save_button;
    MaterialSwitch show_me_switch;
    LinearLayout logout, delete_account;
    DatabaseReference reference;
    int LAUNCH_SECOND_ACTIVITY = 1;

    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    int attempts = 0;
    boolean gender_spinner_initial = false, sexual_spinner_initial = false, gender_show_spinner_initial = false;

    public UserSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
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
        gender_show_spinner = view.findViewById(R.id.gender_show_spinner);
        location = view.findViewById(R.id.location);
        location_preview = view.findViewById(R.id.location_preview);
        age_range_preview = view.findViewById(R.id.age_range_preview);
        age_range = view.findViewById(R.id.age_range_slider);
        profile_username = view.findViewById(R.id.profile_username);
        profile_status = view.findViewById(R.id.profile_status);
        photo_count = view.findViewById(R.id.photo_count);
        et_username = view.findViewById(R.id.et_username);
        et_status = view.findViewById(R.id.et_status);
        edit_button = view.findViewById(R.id.edit_button);
        save_button = view.findViewById(R.id.save_button);
        show_me_switch = view.findViewById(R.id.show_me_switch);
        profile_pic_button = view.findViewById(R.id.profile_pic_button);
        profile_pic = view.findViewById(R.id.profile_pic);
        height = view.findViewById(R.id.height);
        height_preview = view.findViewById(R.id.height_preview);
        dob = view.findViewById(R.id.dob);
        dob_preview = view.findViewById(R.id.dob_preview);
        delete_account = view.findViewById(R.id.delete_account);
        add_photo = view.findViewById(R.id.add_photo);

        // for text marquee
        profile_username.setSelected(true);
        profile_status.setSelected(true);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.gender_spinner_item);
        adapter.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        gender_spinner.setAdapter(adapter);

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.sexual_array, R.layout.gender_spinner_item);
        adapter1.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        sexual_spinner.setAdapter(adapter1);

        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.gender_show_array, R.layout.gender_spinner_item);
        adapter2.setDropDownViewResource(R.layout.gender_spinner_dropdown);
        gender_show_spinner.setAdapter(adapter2);

        // update values from database
        reference.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // location
                if (snapshot.child("location").getValue(String.class) != null)
                    location_preview.setText(snapshot.child("location").getValue(String.class));
                // gender_show
                if (snapshot.child("gender_show").getValue(String.class) != null) {
                    int gender_show_position = adapter2.getPosition(snapshot.child("gender_show").getValue(String.class));
                    gender_show_spinner.setSelection(gender_show_position);
                }
                // gender
                if (snapshot.child("gender").getValue(String.class) != null) {
                    int gender_position = adapter.getPosition(snapshot.child("gender").getValue(String.class));
                    gender_spinner.setSelection(gender_position);
                }
                // sexual_orientation
                if (snapshot.child("sexual_orientation").getValue(String.class) != null) {
                    int sexual_position = adapter1.getPosition(snapshot.child("sexual_orientation").getValue(String.class));
                    sexual_spinner.setSelection(sexual_position);
                }
                // age_range
                if (snapshot.child("age_range").getValue(String.class) == null) {
                    age_range.setValues(18f, 24f);
                    age_range_preview.setText("18 - 24");
                } else {
                    String age_range_string = snapshot.child("age_range").getValue(String.class);
                    float age_valueFrom = Float.valueOf(age_range_string.substring(0, age_range_string.indexOf('-')));
                    float age_valueTo = Float.valueOf(age_range_string.substring(age_range_string.indexOf('-') + 1, age_range_string.length()));
                    age_range.setValues(age_valueFrom, age_valueTo);
                    age_range_preview.setText(age_range.getValues().get(0).intValue() + "-" + age_range.getValues().get(1).intValue());
                }
                // username
                if (snapshot.child("userName").getValue(String.class) != null)
                    profile_username.setText(snapshot.child("userName").getValue(String.class));
                // status
                if (snapshot.child("status").getValue(String.class) != null)
                    profile_status.setText(snapshot.child("status").getValue(String.class));
                // show me
                if (snapshot.child("show_me").getValue(boolean.class) != null)
                    show_me_switch.setChecked(snapshot.child("show_me").getValue(boolean.class));
                // height
                if (snapshot.child("height").getValue(String.class) != null) {
                    height_preview.setText(snapshot.child("height").getValue(String.class) + "cm");
                }
                // dob
                if (snapshot.child("dob").getValue(String.class) != null) {
                    dob_preview.setText(snapshot.child("dob").getValue(String.class));
                }
                if (snapshot.child("photos").hasChildren()) {
                    photo_count.setText(String.valueOf((int) snapshot.child("photos").getChildrenCount()));
                }
                // profile_picture
                if (snapshot.child("profilepic").getValue(String.class) == null) {
                    FirebaseStorage.getInstance().getReference().child("default.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profile_pic);
                            Picasso.get().load(uri).into(profile_pic_button);
                            reference.child("profilepic").setValue(uri.toString());
                        }
                    });
                } else {
                    Picasso.get().load(snapshot.child("profilepic").getValue(String.class)).into(profile_pic);
                    Picasso.get().load(snapshot.child("profilepic").getValue(String.class)).into(profile_pic_button);
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
                profile_pic.setVisibility(View.GONE);
                et_username.setVisibility(View.VISIBLE);
                et_status.setVisibility(View.VISIBLE);
                profile_pic_button.setVisibility(View.VISIBLE);
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
                profile_pic.setVisibility(View.VISIBLE);
                et_username.setVisibility(View.GONE);
                et_status.setVisibility(View.GONE);
                profile_pic_button.setVisibility(View.GONE);
                uploadImage();

                // hide the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        getActivity().finish();
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

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext(), R.style.dialogue);
                dialog.setContentView(R.layout.dialogue_layout);
                TextView description = dialog.findViewById(R.id.description);
                description.setText("Do you really want to delete this account?");
                Button no, yes;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 0, 0, 0)));

                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.setContentView(R.layout.dialogue_delete_account_layout);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 0, 0, 0)));
                        TextInputEditText etPassword = dialog.findViewById(R.id.etPassword);
                        MaterialButton delete_button = dialog.findViewById(R.id.delete_button);
                        delete_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String Pass = etPassword.getText().toString();
                                if ((TextUtils.isEmpty(Pass))) {
                                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                                } else if (Pass.length() < 6) {
                                    Toast.makeText(getContext(), "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                                } else {
                                    // re-authenticate current user
                                    auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail().toString(), Pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // delete profile picture from Firebase Storage
                                            storage.getReference("images/" + auth.getCurrentUser().getUid() + "/profile_pic").delete();

                                            // delete user from Firebase Realtime Database
                                            reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), "Delete user from RealtimeDatabase successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            // delete user from Firebase Auth
                                            auth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), "Delete user from Firebase successfully!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getContext(), login.class);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            attempts++;
                                            Toast.makeText(getContext(), "Wrong password, you have " + String.valueOf(4 - attempts) + " attempts left.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
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


        gender_show_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (gender_show_spinner_initial) {
                    String gender_show = parent.getItemAtPosition(pos).toString();
                    reference.child("gender_show").setValue(gender_show);
                } else {
                    gender_show_spinner_initial = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (gender_spinner_initial) {
                    String gender = parent.getItemAtPosition(pos).toString();
                    reference.child("gender").setValue(gender);
                } else {
                    gender_spinner_initial = true;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        sexual_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (sexual_spinner_initial) {
                    String sexual = parent.getItemAtPosition(pos).toString();
                    reference.child("sexual_orientation").setValue(sexual);
                } else {
                    sexual_spinner_initial = true;
                }
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

        show_me_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                reference.child("show_me").setValue(b);
            }
        });

        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new UserHeightFragment());
            }
        });

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        dob_preview.setText(materialDatePicker.getHeaderText());
                        reference.child("dob").setValue(materialDatePicker.getHeaderText());
                    }
                });

        profile_pic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), add_photo.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Select Image method
    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                location_preview.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                No result handler
            }
        }

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath);
                profile_pic_button.setImageBitmap(bitmap);
                profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

    } //onActivityResult


    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storage.getReference()
                    .child(
                            "images/"
                                    + auth.getCurrentUser().getUid() + "/profile_pic");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child("profilepic").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }

}