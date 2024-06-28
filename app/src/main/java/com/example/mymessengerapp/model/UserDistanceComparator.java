package com.example.mymessengerapp.model;

import android.location.Location;

import java.util.Comparator;
import java.util.Map;

public class UserDistanceComparator implements Comparator<Users> {
    private Users currentUser;

    public UserDistanceComparator(Users currentUser) {
        this.currentUser = currentUser;
    }
    public int compare(Users first, Users second) {
        float[] distance1 = new float[1];
        float[] distance2 = new float[1];
        Location.distanceBetween(Double.valueOf(currentUser.getLatitude()), Double.valueOf(currentUser.getLongitude()),
                Double.valueOf(first.getLatitude()), Double.valueOf(first.getLongitude()), distance1);
        Location.distanceBetween(Double.valueOf(currentUser.getLatitude()), Double.valueOf(currentUser.getLongitude()),
                Double.valueOf(second.getLatitude()), Double.valueOf(second.getLongitude()), distance2);
        return Float.compare(distance1[0], distance2[0]);
    }
}

