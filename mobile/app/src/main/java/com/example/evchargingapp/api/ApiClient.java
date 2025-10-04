package com.example.evchargingapp.api;

import com.example.evchargingapp.utils.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiClient {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    // POST request
    public static Future<String> post(String endpoint, String jsonBody, String token) {
        return executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                URL url = new URL(Constants.BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                try {
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    if (token != null && !token.isEmpty()) {
                        conn.setRequestProperty("Authorization", "Bearer " + token);
                    }
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000); // 10s
                    conn.setReadTimeout(10000);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonBody.getBytes("UTF-8"));
                        os.flush();
                    }

                    int responseCode = conn.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            responseCode >= 200 && responseCode < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream()
                    ));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    return response.toString();

                } finally {
                    conn.disconnect();
                }
            }
        });
    }

    // GET request
    public static Future<String> get(String endpoint, String token) {
        return executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                URL url = new URL(Constants.BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                try {
                    conn.setRequestMethod("GET");
                    if (token != null && !token.isEmpty()) {
                        conn.setRequestProperty("Authorization", "Bearer " + token);
                    }
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    int responseCode = conn.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            responseCode >= 200 && responseCode < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream()
                    ));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    return response.toString();

                } finally {
                    conn.disconnect();
                }
            }
        });
    }

    public static String put(String endpoint, String jsonBody, String token) throws Exception {
        URL url = new URL(Constants.BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
        }

        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        (status < 400) ? conn.getInputStream() : conn.getErrorStream()
                )
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        return response.toString();
    }
}
