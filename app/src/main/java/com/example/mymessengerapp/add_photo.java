package com.example.mymessengerapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymessengerapp.adapter.UserPhotoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import java.util.ArrayList;

public class add_photo extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    ArrayList<Pair<String, String>> photos;
    GridView photo_gridview;
    MaterialButton add_button;
    ImageButton back_icon;
    FirebaseAuth auth;
    DatabaseReference database_ref;
    StorageReference storage_ref;
    ValueEventListener eventListener;
    int position = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        database_ref = FirebaseDatabase.getInstance().getReference().child("user/" + auth.getCurrentUser().getUid() + "/photos/");
        storage_ref = FirebaseStorage.getInstance().getReference().child("images/" + auth.getCurrentUser().getUid() + "/photos/");

        back_icon = findViewById(R.id.back_icon);
        photo_gridview = findViewById(R.id.photo_gridview);
        add_button = findViewById(R.id.add_button);

        photos = new ArrayList<Pair<String, String>>(9);
        UserPhotoAdapter userPhotoAdapter = new UserPhotoAdapter(add_photo.this, photos);
        photo_gridview.setAdapter(userPhotoAdapter);

        database_ref.addValueEventListener(eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        photos.add(new Pair<>(dataSnapshot.getKey().toString(), dataSnapshot.getValue(String.class)));
                    }
                }
                userPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = -1;
                database_ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (int i = 0; i < 9; i++) {
                            if (!dataSnapshot.hasChild(String.valueOf(i))) {
                                position = i;
                                break;
                            }
                        }
                        if (position == -1) {
                            Toast.makeText(add_photo.this, "Maximum number of photos is 9", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            SelectImage();
                        }
                    }
                });
            }
        });

        photo_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = Integer.valueOf(photos.get(i).first);
                Dialog dialog = new Dialog(add_photo.this, R.style.dialogue);
                dialog.setContentView(R.layout.dialogue_modify_user_photos);
                RelativeLayout replace_photo, delete_photo;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 0, 0, 0)));

                replace_photo = dialog.findViewById(R.id.replace_photo);
                delete_photo = dialog.findViewById(R.id.delete_photo);
                replace_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectImage();
                        dialog.dismiss();
                    }
                });
                delete_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePhoto();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    protected void addPhoto(int position) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(add_photo.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            // adding listeners on upload
            // or failure of image
            storage_ref.child(String.valueOf(position)).putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(add_photo.this, "Photo uploaded", Toast.LENGTH_SHORT).show();
                    storage_ref.child(String.valueOf(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database_ref.child(String.valueOf(position)).setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }

    protected void deletePhoto() {
        storage_ref.child(String.valueOf(position)).delete();
        database_ref.child(String.valueOf(position)).removeValue();
        Toast.makeText(add_photo.this, "Photo deleted", Toast.LENGTH_SHORT).show();
    }

    private void SelectImage()
    {
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
                                add_photo.this.getContentResolver(),
                                filePath);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
        addPhoto(position);

    } //onActivityResult

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database_ref != null && eventListener != null) {
            database_ref.removeEventListener(eventListener);
        }
    }
}
