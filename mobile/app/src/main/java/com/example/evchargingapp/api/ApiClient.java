/*
 * File: ApiClient.java
 * Purpose: Provides centralized HTTP client methods (GET, POST, PUT)
 *           to interact with the backend API endpoints using HttpURLConnection.
 *           Handles authentication tokens, JSON requests/responses, and
 *           thread-safe async execution using ExecutorService.
 */

package com.example.evchargingapp.api;

import com.example.evchargingapp.utils.Constants;


import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiClient {
    public static final String BASE_URL = "http://192.168.1.5:5001/api/";

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static Future<String> post(String endpoint, String jsonBody, String token) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(BASE_URL + endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }
                
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                // Log the request
                Log.d("ApiClient", "POST to: " + url.toString());
                Log.d("ApiClient", "Body: " + jsonBody);
                
                // Write body
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                // Get response
                int responseCode = connection.getResponseCode();
                Log.d("ApiClient", "Response code: " + responseCode);
                
                InputStream inputStream;
                if (responseCode >= 200 && responseCode < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                    Log.e("ApiClient", "Error response code: " + responseCode);
                }
                
                String response = readStream(inputStream);
                Log.d("ApiClient", "Response: " + response);
                
                return response;
                
            } catch (Exception e) {
                Log.e("ApiClient", "Request failed", e);
                throw new RuntimeException("API request failed: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
    
    private static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    // GET request
    public static Future<String> get(String endpoint, String token) {
        return executor.submit(() -> {
            URL url = new URL(Constants.BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                conn.setRequestMethod("GET");
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int code = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream()
                ));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                return sb.toString();

            } finally {
                conn.disconnect();
            }
        });
    }

    // PUT request
    public static String put(String endpoint, String jsonBody, String token) throws Exception {
        URL url = new URL(Constants.BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
        }

        int code = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream()
        ));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        conn.disconnect();

        return sb.toString();
    }

    // PATCH request (added for deactivate)
    public static String patch(String endpoint, String token) throws Exception {
        URL url = new URL(Constants.BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PATCH");
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int code = conn.getResponseCode();
        conn.disconnect();
        return String.valueOf(code);
    }
}
