package com.example.evchargingapp.api;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpHelper {

    public static JSONObject postJson(String endpoint, JSONObject body, String token) throws Exception {
        // Replace with your backend base URL
        URL url = new URL("http://192.168.1.5:5001" + endpoint); 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        conn.setDoOutput(true);

        // Write JSON body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
            os.flush();
        }

        // Read response
        Scanner scanner;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            scanner = new Scanner(conn.getInputStream());
        } else {
            scanner = new Scanner(conn.getErrorStream());
        }
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        scanner.close();

        return new JSONObject(sb.toString());
    }
}
