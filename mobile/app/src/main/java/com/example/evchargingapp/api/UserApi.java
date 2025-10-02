package com.example.evchargingapp.api;

import org.json.JSONObject;

public class UserApi {

    public static JSONObject getByNic(String nic, String token) throws Exception {
        String response = ApiClient.get("user/" + nic, token);
        return new JSONObject(response);
    }

    public static JSONObject createUser(String nic, String role, String token) throws Exception {
        JSONObject body = new JSONObject();
        body.put("nic", nic);
        body.put("role", role);

        String response = ApiClient.post("user", body.toString(), token);
        return new JSONObject(response);
    }
}
