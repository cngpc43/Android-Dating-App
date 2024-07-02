package com.example.mymessengerapp.model;

public class NotificationModel {
    private String matchRequestId, status, userId, userName, age, profileImg, type; // type = {"request_send", "request_accept"}
    private Long timestamp;

    public NotificationModel(String matchRequestId, String status, String userId, String userName, String age, String profileImg, String type, Long timestamp) {
        this.matchRequestId = matchRequestId;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.age = age;
        this.profileImg = profileImg;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getMatchRequestId() {
        return matchRequestId;
    }

    public void setMatchRequestId(String matchRequestId) {
        this.matchRequestId = matchRequestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
