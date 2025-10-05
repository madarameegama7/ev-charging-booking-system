/*
 * File: UserApi.java
 * Purpose: Manages API operations for system users.
 *           Supports retrieving user details by NIC
 *           and creating new users in the backend system.
 */

package com.example.evchargingapp.api;

import org.json.JSONObject;

public class UserApi {

    public static JSONObject getByNic(String nic, String token) throws Exception {
        String response = ApiClient.get("user/" + nic, token).get();
        return new JSONObject(response);
    }

    public static JSONObject createUser(String nic, String name, String email,
                                        String phone, String role, String password, String token) throws Exception {
        JSONObject body = new JSONObject();
        body.put("nic", nic);
        body.put("name", name);
        body.put("email", email);
        body.put("phone", phone);
        body.put("role", role);
        body.put("passwordHash", password);

        String response = ApiClient.post("user", body.toString(), token).get();
        return new JSONObject(response);
    }
}
