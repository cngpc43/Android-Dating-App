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
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/messenger-app-b4fed/messages:send";

    public SendNotifications(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void Send() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();

            notificationObject.put("title", title);
            notificationObject.put("body", body);

            messageObject.put("token", userFcmToken);
            messageObject.put("notification", notificationObject);

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
                    header.put("content-type", "application/json");
                    header.put("authorization", "Bearer " + accessKey);
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
