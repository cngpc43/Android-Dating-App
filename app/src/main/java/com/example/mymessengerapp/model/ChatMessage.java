package com.example.mymessengerapp.model;

import com.google.android.gms.tasks.Task;

public class ChatMessage {
    private String senderId, message, imgUrl, imgType;
    private boolean isSent;
    ChatRoom chatRoom;
    long timeStamp;

    public ChatMessage() {

    }

    public ChatMessage(String message, long timeStamp, String senderId) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }
    public Task<String> getUserName() {
        Users user = new Users();
        return user.getUserNameById(senderId);
    }
    public String getLastMessage() {
        return message;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }
}