/*
 * File: UpdateProfileActivity.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Allows EV owners to update their profile and deactivate their account.
 */

package com.example.evchargingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.database.DBHelper;
import com.example.evchargingapp.models.User;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnUpdate, btnDeactivate;
    private DBHelper dbHelper;
    private String userNIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        dbHelper = new DBHelper(this);

        // Get user NIC from Intent
        userNIC = getIntent().getStringExtra("USER_NIC");
        if (userNIC == null) {
            Toast.makeText(this, "No user found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Bind UI
        etName = findViewById(R.id.etUpdateName);
        etEmail = findViewById(R.id.etUpdateEmail);
        etPassword = findViewById(R.id.etUpdatePassword);
        btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnDeactivate = findViewById(R.id.btnDeactivateAccount);

        // Load existing user data
        loadUserData();

        // Update button click
        btnUpdate.setOnClickListener(v -> updateProfile());

        // Deactivate button click
        btnDeactivate.setOnClickListener(v -> confirmDeactivate());
    }

    private void loadUserData() {
        User user = dbHelper.getUserObjectByNIC(userNIC);
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        }
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int updated = dbHelper.updateUser(userNIC, name, email, password);
        if (updated > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeactivate() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account?")
                .setPositiveButton("Yes", (dialog, which) -> deactivateAccount())
                .setNegativeButton("No", null)
                .show();
    }

    private void deactivateAccount() {
        int res = dbHelper.setUserActive(userNIC, false);
        if (res > 0) {
            Toast.makeText(this, "Account deactivated", Toast.LENGTH_SHORT).show();
            // Navigate to login
            Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to deactivate account", Toast.LENGTH_SHORT).show();
        }
    }
}
