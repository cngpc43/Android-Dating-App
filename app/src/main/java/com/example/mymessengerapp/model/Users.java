package com.example.mymessengerapp.model;

public class Users {
    String profilepic,mail,userName,password,userId,lastMessage,status, gender, dob, phone, location, sexual_orientation, height, age_range;

    public  Users(){}

    public Users(String userId, String userName, String mail, String password, String profilepic, String status, String gender, String dob, String phone, String location, String sexual_orientation, String height, String age_range) {
        this.userId = userId;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.profilepic = profilepic;
        this.status = status;
        this.gender = gender;
        this.dob = dob;
        this.phone = phone;
        this.location = location;
        this.sexual_orientation = sexual_orientation;
        this.height = height;
        this.age_range = age_range;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAge_range() {
        return age_range;
    }

    public void setAge_range(String age_range) {
        this.age_range = age_range;
    }
}
