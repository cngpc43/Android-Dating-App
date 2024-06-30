package com.example.mymessengerapp.model;

public class ChatDetail {
    private  String userId;
    private String userName;
    private String userImage;
    private String lastMessage;
    private String chatRoom;
    private long timestamp;
    boolean isOnline;

    public ChatDetail(String userId, String userName, String userImage, String lastMessage, long timestamp, boolean isOnline, String chatRoom) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.isOnline = isOnline;
        this.chatRoom = chatRoom;
    }

    @Override
    public String toString() {
        return "ChatDetail{" +
                "userName='" + userName + '\'' +
                ", userImage='" + userImage + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }
}