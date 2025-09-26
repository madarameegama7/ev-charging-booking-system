/*
 * File: RegisterActivity.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Handles user registration (UI + DB insert)
 */

package com.example.evchargingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.database.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNIC, etName, etEmail, etPassword;
    private Button btnRegister;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Bind UI elements
        etNIC = findViewById(R.id.etNIC);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Register button click
        btnRegister.setOnClickListener(v -> {
            String nic = etNIC.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Basic validation
            if (nic.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.addUser(nic, name, email, password);

            if (success) {
                Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
