package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;
import com.squareup.picasso.Picasso;

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
        return convertView;
    }
}
