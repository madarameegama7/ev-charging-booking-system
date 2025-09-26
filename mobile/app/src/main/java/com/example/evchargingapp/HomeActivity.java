/*
 * File: HomeActivity.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Temporary home screen after successful login.
 */

package com.example.evchargingapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        String nic = getIntent().getStringExtra("USER_NIC");
        tv.setText("Welcome! Logged in as: " + nic);
        tv.setTextSize(18f);
        setContentView(tv);
    }
}
