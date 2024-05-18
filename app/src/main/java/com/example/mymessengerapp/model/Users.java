package com.example.mymessengerapp.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Users {
    String profilepic,mail,userName,password,userId,lastMessage,status, gender, dob, phone, location, sexual_orientation, height;

    public  Users(){}

    public Users(String userId, String userName, String mail, String password, String profilepic, String status, String gender, String dob, String phone, String location, String sexual_orientation, String height) {
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
    public Task<String> getUserNameById(String userId) {
        // Create a TaskCompletionSource
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        // Fetch the user's data from the database
        FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if (user != null) {
                    // If the user is found, set the result of the Task
                    taskCompletionSource.setResult(user.getUserName());
                } else {
                    // If the user is not found, set the result to null
                    taskCompletionSource.setResult(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If an error occurred, set the exception of the Task
                taskCompletionSource.setException(error.toException());
            }
        });

        // Return the Task
        return taskCompletionSource.getTask();
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
}
