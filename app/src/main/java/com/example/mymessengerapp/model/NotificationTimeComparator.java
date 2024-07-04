package com.example.mymessengerapp.model;

import android.location.Location;

import java.util.Comparator;

public class NotificationTimeComparator implements Comparator<NotificationModel> {

    public NotificationTimeComparator() {
    }
    public int compare(NotificationModel first, NotificationModel second) {
        return second.getTimestamp().compareTo(first.getTimestamp());
    }
}
