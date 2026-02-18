package www.dpremiumhomes.com;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import www.dpremiumhomes.com.helpers.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextView btnLogin, tvRegister, tvForgot;

    private static final String LOGIN_URL = "https://premium-api.dvalleybd.com/get-user.php?action=authenticate-user";

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        session = new SessionManager(this);

        // Auto-redirect if already logged in
        if (session.isLoggedIn()) {
            goToMainActivity();
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgot = findViewById(R.id.tv_forgot);

        // Click listeners
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        tvForgot.setOnClickListener(v -> Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show());

        // Pre-fill for testing (remove in production!)
        etEmail.setText("tpgv1a@dpremiumhomes.com");
        etPassword.setText("user1235");
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                    Log.d("LoginResponse", response);

                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "Unknown response");

                        if (success) {
                            JSONObject user = json.getJSONObject("user");

                            // Save session
                            session.saveUser(
                                    user.optString("id"),
                                    user.optString("name"),
                                    user.optString("email"),
                                    user.optString("phone"),
                                    json.optString("token"), // token
                                    user // pass the entire user JSON object
                            );

                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();

                        } else {
                            Toast.makeText(this, "Login Failed: " + message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "Response Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("LoginResponse", e.toString());
                    }

                },
                error -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");

                    String errorMsg = "Network Error";
                    if (error.networkResponse != null) {
                        errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }) {

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
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        queue.add(request);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Clear backstack so user cannot go back to login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
