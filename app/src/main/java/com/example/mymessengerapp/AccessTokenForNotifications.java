package com.example.mymessengerapp;

import android.util.Log;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AccessTokenForNotifications {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"messenger-app-b4fed\",\n" +
                    "  \"private_key_id\": \"c394b69beba7da889302cbdb555ce0ca59f3a5f4\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCf7SDvWI55UY/d\\nmwYNShM48Zkkc2KZr6WubGsXXoQaeVsHQVVhHesnvjcDlZz2HrJQ2R2gn6KW+Ca7\\n2UU3xUItlsqzRq9MNzgEh9dklg1IwIMtg9dcSkcLAlzo/DsnhQO7ce5lfI/AZ/tu\\nBOQ6VISragRTTfF1Z8wks8/FHBPAySeBZEe6kXPIbvVavwQc+OtK9227kN0yUmZe\\nOm6J4yEHT5I1uelBsTRTkXqE8PdpecuCt3lot0xKctGe3HqD/VhjWjlwH4KPclyy\\nO1OKPsDo6MZE7hLIM5dygNVa0Xekv4YF/EyD974ENY6A+CfrbjIHGVRReuhNqeWf\\nJspkHtSnAgMBAAECggEAEEbECVgL+BDzAWzbqtKVLkB4g5syt82GoT/rjk9SRpd/\\nSje+BspSupLDapv5g5sQLuZJ+6UDmTXcS1YLk2eCGr20F5bkFG+yRdPFejlDVNoy\\n/5jhfEUbAeXVquiw5I7HGHa5+p44yy5KCqJ4z+72KbP6iLfwETDlN/8M8FSxNfeZ\\ny3bf46SgkpyWxeshf+ReDgSMLUSqmUlLrm2dHzhBGbIaek98XwlazoDdz+sLctZz\\nxKRElUVdez3eNOTf2qGY+gGqfV+Pkf5dyDGEunTch7/372VqSBKZajgYgQUWx1pb\\n9W7ptPRl8753SAkto61G3UE8IOEA5q+gTw30eczG0QKBgQDV+CNOOrlyIxVPPm2x\\nQa5m7kZyOhxxeLFPcs3GUKNb2blW190IVlNaDgvBBCFQcBSIV0+5EXof6teuyyhr\\nQPlQEBV1OsDxMwMRspYf7brcG+25593SraOE2T011MydipzoZxWTDoROBMRenXxR\\n9zxxPH8loAqAmo/leIzAFMLE4wKBgQC/V1VDtUACmKPS/yblledX01d4jrPBFfo/\\n2BuJkFIanZzTpVRLIy7Nw87KIV0SSpz30w0gKC+iaNelNzjAclJr0tJfYW26+6QJ\\nbDaNgA1pps+dAb35bQw3QmOHOVmHGxblyNs+QvAQRUd3VZxRgdxzPeT4zVY84Ohz\\nf1P5ldsAbQKBgCU52JqhqOXsqKZyzA7MyJTr26G/7nbncqHJ7XcVTHaMeKxdPf1X\\nV4URcIYSO+GMvmXCGwKtwbuP1hp5Cf0u/rEpOzJ8GFlecKhBk9DWQW5OBZwUdQlU\\nYGCakwWskjTOiuH8HUrKecZBGhEEAT8ZbpCc39oh7/HT8jkQMhyw//nVAoGAHpLt\\n5OvJVqDdTJHIvVa+qKH0kCb9QhUo75qwi8Kr2+Tjck4wsfDlQavQFyxNmuahPlhS\\n+JhzRBhMnPG998JMuT3ur5750a7z/y1zahjUC9XDxurIWFq8YPgSv0DCtXv2pLD7\\nl29KvM7VJJRhbgS6HQ/+zmLBLOkjO5FU8cSoQeECgYEAqu+qMR/LQe6T5JUXyNw+\\nppFujQJ37S/hLJovJ+JBXMp0kbVYZtkQTgnO4ojs/gZ8D1mBX55XWgI8N8wc1ZcL\\n3TCMzB0dTvr2wKCeZGoLPVpbeRD/erXeitSHNzwGJtegRaRbXSW+Iq//CdGsZcvW\\nh6FC9m0wypHIpvNvW315MDg=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-22ads@messenger-app-b4fed.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"116113195627526010399\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-22ads%40messenger-app-b4fed.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Arrays.asList(firebaseMessagingScope));

            googleCredentials.refresh();

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.d("AccessTokenForNotifications", e.getMessage());
            return null;
        }
    }
}
