package com.example.mymessengerapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MediaPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Uri> mediaUris;

    public MediaPagerAdapter(Context context, ArrayList<Uri> mediaUris) {
        this.context = context;
        this.mediaUris = mediaUris;
    }

    @Override
    public int getCount() {
        return mediaUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, container, false);
        ImageView imageView = view.findViewById(R.id.image_view);
        Picasso.get().load(mediaUris.get(position)).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}