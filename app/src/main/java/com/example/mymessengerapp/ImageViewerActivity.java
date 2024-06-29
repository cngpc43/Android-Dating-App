package com.example.mymessengerapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ImageView imageView = findViewById(R.id.image_view);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        ImageButton backButton = findViewById(R.id.back_button);
        Picasso.get().load(imageUrl).into(imageView);
        backButton.setOnClickListener(v -> finish());
    }
}