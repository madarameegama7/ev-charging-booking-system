package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.ApiClient;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EVOwnerProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnUpdate, btnDeactivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evowner_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeactivate = findViewById(R.id.btnDeactivate);

        // Prefill with stored info (optional from SharedPrefs or SQLite)
//        etName.setText(SharedPrefsHelper.getUserName(this));
//        etEmail.setText(SharedPrefsHelper.getUserEmail(this));
//        etPhone.setText(SharedPrefsHelper.getUserPhone(this));

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnDeactivate.setOnClickListener(v -> confirmDeactivation());
    }

    private void updateProfile() {
        String nic = SharedPrefsHelper.getNic(this);
        String token = SharedPrefsHelper.getToken(this);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        new UpdateProfileTask(nic, name, email, phone, password, token).execute();
    }

    private void confirmDeactivation() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String nic = SharedPrefsHelper.getNic(this);
                    String token = SharedPrefsHelper.getToken(this);
                    new DeactivateTask(nic, token).execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // AsyncTask for updating profile
    private class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String nic, name, email, phone, password, token;

        UpdateProfileTask(String nic, String name, String email, String phone, String password, String token) {
            this.nic = nic;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                JSONObject json = new JSONObject();
                json.put("nic", nic);
                json.put("name", name);
                json.put("email", email);
                json.put("phone", phone);
                if (!password.isEmpty()) json.put("passwordHash", password);

                String response = ApiClient.put("api/user/profile/" + nic, json.toString(), token);
                return response.contains("Profile updated successfully");

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EVOwnerProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EVOwnerProfileActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask for deactivating account
    private class DeactivateTask extends AsyncTask<Void, Void, Boolean> {
        private final String nic, token;

        DeactivateTask(String nic, String token) {
            this.nic = nic;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String response = ApiClient.put("api/user/profile/" + nic + "/deactivate", "{}", token);
                return response.contains("Account deactivated successfully");

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EVOwnerProfileActivity.this, "Account deactivated.", Toast.LENGTH_SHORT).show();
                SharedPrefsHelper.clear(EVOwnerProfileActivity.this);
                Intent i = new Intent(EVOwnerProfileActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(EVOwnerProfileActivity.this, "Failed to deactivate account.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
