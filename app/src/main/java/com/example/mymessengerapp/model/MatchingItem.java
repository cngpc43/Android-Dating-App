package com.example.mymessengerapp.model;

public class MatchingItem {
    String userName;
    String userId;
    String profilePic;
    String lastMessage;
    String status;
    String time;
    public MatchingItem(String userName, String userId, String profilePic, String lastMessage, String status, String time) {
        this.userName = userName;
        this.userId = userId;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.status = status;
        this.time = time;
    }
    public String getUserName() {
        return userName;
    }
}
