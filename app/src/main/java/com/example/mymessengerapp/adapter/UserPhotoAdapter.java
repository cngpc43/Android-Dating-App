package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mymessengerapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserPhotoAdapter extends ArrayAdapter<Pair<String, String>> {

    ArrayList<Pair<String, String>> photoList;
    public UserPhotoAdapter(@NonNull Context context, ArrayList<Pair<String, String>> photoList) {
        super(context, 0, photoList);
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_photo_item, parent, false);
        }

        String photo = photoList.get(position).second;
        ImageView image = convertView.findViewById(R.id.image);

        if (photo != null) {
            if (!photo.equals("")) {
                Picasso.get().load(photo).into(image);
            }
        }

        return convertView;
    }
}
