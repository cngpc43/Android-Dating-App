package com.example.mymessengerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.telephony.IccOpenLogicalChannelResponse;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.VNCharacterUtils;

import android.view.View;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private String filteredText = "";
    private boolean isFiltered = false;
    public List<String> citiesList;
    public ArrayList<String> arraylist;

    public ListViewAdapter(Context context, List<String> citiesList) {
        mContext = context;
        this.citiesList = citiesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(citiesList);
    }
    @Override
    public int getCount() {
        return citiesList.size();
    }

    @Override
    public String getItem(int position) {
        return citiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView cityName;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder = new Holder();
        view = inflater.inflate(R.layout.gender_spinner_dropdown, null);
        // Locate the TextViews in listview_item.xml
        holder.cityName = (TextView) view.findViewById(R.id.text1);

        // Set the results into TextViews
        // Remove accents and lowercase the city name for searching
        String modifiedCityName = VNCharacterUtils.removeAccent(citiesList.get(position).toString()).toLowerCase(Locale.getDefault());
        // if citiesList is filtered
        if (isFiltered) {
            Spannable WordtoSpan = new SpannableString(citiesList.get(position).toString());
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedCityName.indexOf(filteredText),
                    modifiedCityName.indexOf(filteredText) + filteredText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.cityName.setText(WordtoSpan);
        }
        else {
            holder.cityName.setText(citiesList.get(position).toString());
            holder.cityName.setTextColor(Color.BLACK);
        }
        holder.cityName.setTextSize(21f);
        holder.cityName.setHeight(130);
        holder.cityName.setPadding(30,0,30,0);

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = VNCharacterUtils.removeAccent(charText).toLowerCase(Locale.getDefault());
        citiesList.clear();
        if (charText.length() == 0) {
            citiesList.addAll(arraylist);
            isFiltered = false;
        } else {
            for (String str : arraylist) {
                if (VNCharacterUtils.removeAccent(str).toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredText = charText;
                    citiesList.add(str);
                    isFiltered = true;
                }
            }
        }
        notifyDataSetChanged();
    }

}
