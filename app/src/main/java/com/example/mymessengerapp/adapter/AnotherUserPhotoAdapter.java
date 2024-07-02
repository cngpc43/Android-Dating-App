package com.example.mymessengerapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class AnotherUserPhotoAdapter extends ArrayAdapter<String> {
    ArrayList<String> photos;

    public AnotherUserPhotoAdapter(Context context, ArrayList<String> photos) {
        super(context, 0, photos);
        this.photos = photos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.another_photos_item, parent, false);

        String photo = photos.get(position);
        ImageView imageView = convertView.findViewById(R.id.image);

        Picasso.get().load(photo).into(imageView);

        // View full "that" image
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Another photo", "Image clicked");
                Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.dialog_full_image);
                PhotoView photoView = dialog.findViewById(R.id.full_image);
                Picasso.get().load(photo).into(photoView);
                dialog.show();
            }
        });

        return convertView;
    }
}
