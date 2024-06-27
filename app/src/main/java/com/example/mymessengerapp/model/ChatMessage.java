package com.example.mymessengerapp.model;

import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChatMessage {
    private String senderId, message, imgUrl, imgType;
    private boolean isSent;
    ChatRoom chatRoom;
    long timeStamp;
    boolean isLastMessage;
    public ChatMessage() {

    }

    public ChatMessage(String message, long timeStamp, String senderId, boolean isLastMessage) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.isLastMessage = true;
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
    public String getTime() {
        return convertTimestampToTime(timeStamp);
    }
    public String getSenderId() {
        return senderId;
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
    private String convertTimestampToTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date currentDate = new Date();
        Long difference = currentDate.getTime() - date.getTime();

        if (TimeUnit.MILLISECONDS.toSeconds(difference) < 60)
            return "Just now";
        if (TimeUnit.MILLISECONDS.toMinutes(difference) >= 1 && TimeUnit.MILLISECONDS.toMinutes(difference) < 60)
            return (TimeUnit.MILLISECONDS.toMinutes(difference) + "m");
        else if (TimeUnit.MILLISECONDS.toHours(difference) >= 1 && TimeUnit.MILLISECONDS.toHours(difference) < 24)
            return (TimeUnit.MILLISECONDS.toHours(difference) + "h");
        else
            return (TimeUnit.MILLISECONDS.toDays(difference) + "d");
    }
}