package com.premium_homes.tech;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.premium_homes.tech.helpers.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_ACTIVITY";
    private static final String LOGIN_URL = "https://premium-api.dvalleybd.com/get-user.php?action=authenticate-user";

    private TextInputEditText etEmail, etPassword;
    private TextView btnLogin, tvRegister, tvForgot;

    private SessionManager session;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session first
        session = new SessionManager(this);

        // Check if user is already logged in and redirect
        if (session.isLoggedIn()) {
            Log.d(TAG, "User already logged in, redirecting to MainActivity");
            goToMainActivity();
            return; // Important: return to prevent further execution
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        requestQueue = Volley.newRequestQueue(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgot = findViewById(R.id.tv_forgot);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> openContactActivity("register"));
        tvForgot.setOnClickListener(v -> openContactActivity("forgot"));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        setLoadingState(true);

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                this::handleLoginResponse,
                this::handleLoginError) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-tech-form-urlencoded");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void handleLoginResponse(String response) {
        setLoadingState(false);
        Log.d(TAG, "Login Response: " + response);

        try {
            JSONObject json = new JSONObject(response);
            boolean success = json.optBoolean("success", false);
            String message = json.optString("message", "Unknown response");

            if (success) {
                processSuccessfulLogin(json);
            } else {
                showToast("Login Failed: " + message);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
            showToast("Error parsing response");
        }
    }

    private void processSuccessfulLogin(JSONObject json) throws JSONException {
        JSONObject user = json.getJSONObject("user");
        String token = json.optString("token", "");

        // Save user session
        session.saveUser(
                user.optString("id"),
                user.optString("name"),
                user.optString("email"),
                user.optString("phone"),
                token,
                user
        );

        // Debug: Print session data
        session.debugPrint();

        showToast("Login Successful!");
        goToMainActivity();
    }

    private void handleLoginError(VolleyError error) {
        setLoadingState(false);
        Log.e(TAG, "Login Error: " + error.toString());

        String errorMsg = "Network Error";
        if (error.networkResponse != null) {
            errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
        }
        showToast(errorMsg);
    }

    private void setLoadingState(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? "Logging in..." : "Login");
    }

    private void openContactActivity(String type) {
        Intent intent = new Intent(LoginActivity.this, Contact_or_MessageActivity.class);
        intent.putExtra("type", type); // Pass the type (register or forgot)
        startActivity(intent);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When returning to LoginActivity (after logout), ensure we're not logged in
        if (session.isLoggedIn()) {
            // This shouldn't happen, but just in case
            session.logout();
        }
    }
}