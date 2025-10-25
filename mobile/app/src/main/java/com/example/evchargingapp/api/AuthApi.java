/*
 * File: AuthApi.java
 * Purpose: Handles authentication-related API calls such as user login.
 *           Sends NIC and role credentials to backend and receives
 *           authentication token and user details in response.
 */

package com.example.evchargingapp.api;

import org.json.JSONObject;

public class AuthApi {

    public static JSONObject login(String nic, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("NIC", nic);
        body.put("Password", password);

        String response = ApiClient.post("auth/login", body.toString(), null).get();
        System.out.println("DEBUG RESPONSE: " + response);

        JSONObject json = new JSONObject(response);

        // 🔍 Detect backend error and throw appropriately
        if (json.has("error")) {
            String errorType = json.getString("error");
            throw new Exception(errorType);
        }

        return json;
    }


    public static JSONObject register(String firstName, String lastName, String email,
                                      String phone, String nic, String password, String role) throws Exception {
        JSONObject body = new JSONObject();
        body.put("FirstName", firstName);
        body.put("LastName", lastName);
        body.put("Email", email);
        body.put("Phone", phone);
        body.put("NIC", nic);
        body.put("Password", password);
        body.put("Role", role);

        String response = ApiClient.post("auth/register", body.toString(), null).get();
        return new JSONObject(response);
    }
}
