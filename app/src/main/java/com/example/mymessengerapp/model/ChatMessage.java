package com.example.mymessengerapp.model;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChatMessage {
    private String senderId, message, imgUrl, imgType;
    private boolean isSent;
    ChatRoom chatRoom;
    long timeStamp;
    boolean isLastMessage;
    private String attachmentUrl;
    private String attachmentType;
    private String status;

    public ChatMessage() {

    }

    public ChatMessage(String message, long timeStamp, String senderId, boolean isLastMessage, String attachmentUrl, String attachmentType, String status) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.isLastMessage = true;
        this.attachmentUrl = attachmentUrl;
        this.attachmentType = attachmentType;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }
    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
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
        String[] dayInWeek = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        Date date = new Date(timestamp);
        Date currentDate = new Date();
        Long difference = currentDate.getTime() - date.getTime();

        // hh:mm
        @SuppressLint("DefaultLocale") String hourMin = String.format("%02d:%02d", date.getHours(), date.getMinutes());

        // Get Calendar instances for comparison
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);

        // NOT CURRENT YEAR -> 12 June, 2022 hh:mm
        if (currentCalendar.get(Calendar.YEAR) != dateCalendar.get(Calendar.YEAR)) {
            return dateCalendar.get(Calendar.DAY_OF_MONTH) + " " + months[dateCalendar.get(Calendar.MONTH)] + ", " + (dateCalendar.get(Calendar.YEAR)) + " " + hourMin;
        }
        // Not current week -> 12 June hh:mm
        else if (currentCalendar.get(Calendar.WEEK_OF_YEAR) != dateCalendar.get(Calendar.WEEK_OF_YEAR)) {
            return dateCalendar.get(Calendar.DAY_OF_MONTH) + " " + months[dateCalendar.get(Calendar.MONTH)] + " " + hourMin;
        }
        // Not current day but in the current week -> Mon hh:mm
        else if (currentCalendar.get(Calendar.DAY_OF_YEAR) != dateCalendar.get(Calendar.DAY_OF_YEAR)) {
            return dayInWeek[dateCalendar.get(Calendar.DAY_OF_WEEK) - 1] + " " + hourMin;
        }
        // Current day
        else {
            return hourMin;
        }
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isLastMessage() {
        return isLastMessage;
    }

    public void setLastMessage(boolean lastMessage) {
        isLastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}