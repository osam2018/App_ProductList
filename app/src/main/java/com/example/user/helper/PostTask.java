package com.example.user.helper;

import android.os.AsyncTask;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostTask extends AsyncTask<String, Void, String> {
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA7PcMxWM:APA91bGgDsrcfRIxsxgNAKFIEIZ3lZpERF1aqkNzRBVJXHDjij02NcW84yShc3QNHQOIE7CYI1_TRpvnZ2zA6BkR0z6Mro4c-QM5g7_M-pFEOa5B2-F-SkhbIGi4TLlFcBulMl4IjD6o";
    String SENDER_ID = "1017757091171";

    @Override
    protected String doInBackground(String... strings) {
        try
        {
            String title = strings.length >= 1 ? strings[0] : "";
            String content = strings.length >= 2 ? strings[1] : "";
            String to = strings.length >= 3 ? strings[2] : "";

            // FMC 메시지 생성 start
            JSONObject root = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("content", strings[1]);
            notification.put("title", strings[0]);
            //notification.put("", "");
            //notification.put("message", Math.random());
            //root.put("notification", notification);
            root.put("data", notification);
            root.put("to", to);
            // FMC 메시지 생성 end

            URL Url = new URL(FCM_MESSAGE_URL);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-type", "application/json");

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());

            wr.writeBytes(root.toString());

            wr.flush();
            wr.close();

            //OutputStream os = conn.getOutputStream();
            //os.write(root.toString().getBytes("utf-8"));
            //os.flush();

            conn.getResponseCode();
        } catch (Exception e) {

        }

        return null;
    }
}
