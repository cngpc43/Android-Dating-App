package com.example.mymessengerapp.model;

import java.sql.Timestamp;
import java.util.List;

public class ChatRoom {
    String chatRoomId;
    List<String> userId; // luu id user, dùng list vì để handle được chat cho nhóm
    Timestamp lastMessageTimeStamp;
    String lastMessageSenderId; // id của người gửi cuối

    public ChatRoom() {

    }

    public ChatRoom(String chatRoomId, List<String> userId, Timestamp lastMessageTimeStamp, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }



}
