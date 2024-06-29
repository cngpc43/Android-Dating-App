package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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


        return super.getView(position, convertView, parent);
    }
}
