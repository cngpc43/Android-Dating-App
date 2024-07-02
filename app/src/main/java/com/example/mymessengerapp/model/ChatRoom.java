package com.example.mymessengerapp.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private String chatRoomId;
    private List<String> users; // luu id user, dùng list vì để handle được chat cho nhóm
    private Timestamp lastMessageTimeStamp;
    private String lastMessageSenderId; // id của người gửi cuối

    public ChatRoom() {

    }

    public ChatRoom(String userId1, String userId2) {
        this.users = new ArrayList<>();
        this.users.add(userId1);
        this.users.add(userId2);
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUserId(List<String> users) {
        this.users = users;
    }

    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}