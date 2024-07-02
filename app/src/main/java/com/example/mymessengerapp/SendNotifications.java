package com.example.mymessengerapp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.AccessToken;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotifications {
    private final String userFcmToken;
    private final String title;
    private final String body;
    private final String notiType;
    private final String chatId;
    private final String userId;
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/messenger-app-b4fed/messages:send";

    public SendNotifications(String userFcmToken, String title, String body, String notiType, String chatId, String userId, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.notiType = notiType;
        this.chatId = chatId;
        this.userId = userId;
        this.context = context;
    }

    public void Send() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            JSONObject dataObject = new JSONObject();

            notificationObject.put("title", title);
            notificationObject.put("body", body);

            dataObject.put("type", notiType);
            dataObject.put("chatId", chatId);
            dataObject.put("userId", userId);

            messageObject.put("token", userFcmToken);
            messageObject.put("notification", notificationObject);
            messageObject.put("data", dataObject);

            mainObj.put("message", messageObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    postUrl, mainObj, response -> {
                // code run got response
            }, volleyError -> {
                // code run error
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    AccessTokenForNotifications accessToken = new AccessTokenForNotifications();
                    String accessKey = accessToken.getAccessToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer " + accessKey);
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
