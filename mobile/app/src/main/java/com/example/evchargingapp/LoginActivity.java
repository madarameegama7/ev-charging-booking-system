/*
 * File: LoginActivity.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Handles user login with NIC and password using DBHelper.
 */

package com.example.evchargingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.evchargingapp.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginNIC, etLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup App Bar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Bind UI elements
        etLoginNIC = findViewById(R.id.etLoginNIC);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Handle login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nic = etLoginNIC.getText().toString().trim();
                String password = etLoginPassword.getText().toString().trim();

                if (nic.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean valid = dbHelper.validateUserObject(nic, password);
                    if (valid) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Navigate to HomeActivity (dummy for now)
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("USER_NIC", nic);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid NIC or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Navigate to RegisterActivity
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
