package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.AuthApi;
import com.example.evchargingapp.utils.SharedPrefHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText etNic;
    private Spinner spRole;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNic = findViewById(R.id.etNic);
        spRole = findViewById(R.id.spRole);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> {
            String nic = etNic.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();

            if (nic.isEmpty()) {
                Toast.makeText(this, "Please enter NIC", Toast.LENGTH_SHORT).show();
                return;
            }

            new LoginTask().execute(nic, role);
        });
    }

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String nic = params[0];
            String role = params[1];
            HttpURLConnection conn = null;

            try {
                URL url = new URL(AuthApi.LOGIN_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("nic", nic);
                body.put("role", role);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader;
                if (responseCode == 200) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return new JSONObject(response.toString());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);

            if (result == null) {
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                return;
            }

            if (result.has("token")) {
                try {
                    String token = result.getString("token");
                    String role = result.getString("role");
                    String nic = result.getString("nic");

                    // Save token & role & nic in SharedPref
                    SharedPrefHelper.saveToken(LoginActivity.this, token);
                    SharedPrefHelper.saveRole(LoginActivity.this, role);
                    SharedPrefHelper.saveNic(LoginActivity.this, nic);

                    // Redirect based on role
                    Intent intent;
                    if (role.equalsIgnoreCase("Owner")) {
                        intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, StationOperatorDashboardActivity.class);
                    }
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Invalid NIC or role", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
