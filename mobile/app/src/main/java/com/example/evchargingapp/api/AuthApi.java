package com.example.evchargingapp.api;

import org.json.JSONObject;

public class AuthApi {

    public static JSONObject login(String nic, String role) throws Exception {
        JSONObject body = new JSONObject();
        body.put("nic", nic);
        body.put("role", role);

        String response = ApiClient.post("auth/login", body.toString(), null).get();
        return new JSONObject(response);
    }
}
