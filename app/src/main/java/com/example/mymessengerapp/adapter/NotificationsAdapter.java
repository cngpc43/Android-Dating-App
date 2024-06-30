package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.NotificationModel;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

// Adapter to display data in notification fragment
public class NotificationsAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    public List<NotificationModel> notificationsList;

    public NotificationsAdapter(Context context, List<NotificationModel> notificationsList) {
        mContext = context;
        this.notificationsList = notificationsList;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return notificationsList.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return notificationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView userName, noti_context, time;
        CircleImageView user_icon;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        NotificationsAdapter.Holder holder = new NotificationsAdapter.Holder();
        view = inflater.inflate(R.layout.notification_item, null);
        // Locate the TextViews in listview_item.xml
        holder.userName = view.findViewById(R.id.user_name);
        holder.noti_context = view.findViewById(R.id.noti_context);
        holder.time = view.findViewById(R.id.time);
        holder.user_icon = view.findViewById(R.id.user_icon);

        // Set the results into view
        holder.userName.setText(notificationsList.get(position).getUserName() + ", " + notificationsList.get(position).getAge());

        if (notificationsList.get(position).getType().equals("request_send"))
            holder.noti_context.setText("Has sent you a match request.");
        else if (notificationsList.get(position).getType().equals("request_accept"))
            holder.noti_context.setText("And you are now matched.");

        Long timestamp = notificationsList.get(position).getTimestamp();
        Date date = new Date(timestamp);
        Date currentDate = new Date();
        Long difference = currentDate.getTime() - date.getTime();

        if (TimeUnit.MILLISECONDS.toSeconds(difference) < 60)
            holder.time.setText("Just now");
        else if (TimeUnit.MILLISECONDS.toMinutes(difference) >= 1 && TimeUnit.MILLISECONDS.toMinutes(difference) < 60)
            holder.time.setText(TimeUnit.MILLISECONDS.toMinutes(difference) + "m");
        else if (TimeUnit.MILLISECONDS.toHours(difference) >= 1 && TimeUnit.MILLISECONDS.toHours(difference) < 24)
            holder.time.setText(TimeUnit.MILLISECONDS.toHours(difference) + "h");
        else
            holder.time.setText(TimeUnit.MILLISECONDS.toDays(difference) + "d");

        Picasso.get().load(notificationsList.get(position).getProfileImg()).into(holder.user_icon);

        return view;
    }
}
