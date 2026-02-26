package com.premium_homes.tech;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.premium_homes.tech.helpers.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView ivProfilePicture;
    private ImageView backButton;
    private TextView tvFullName, tvEmail, tvPhone, tvAddress;
    private TextView tvFullNameDisplay, tvEmailDisplay;
    private TextView tvBanglaNotice;
    private Button btnDeleteProfile;

    private SessionManager session;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

        // Initialize SessionManager
        session = new SessionManager(this);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize Views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Load user data from SessionManager
        loadUserData();
    }

    private void initViews() {
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        tvFullName = findViewById(R.id.tv_full_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvFullNameDisplay = findViewById(R.id.tv_full_name_display);
        tvEmailDisplay = findViewById(R.id.tv_email_display);
        tvBanglaNotice = findViewById(R.id.tv_bangla_notice);
        backButton = findViewById(R.id.id_back);
        btnDeleteProfile = findViewById(R.id.btn_delete_profile);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
        btnDeleteProfile.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadUserData() {
        // Get data from session
        String name = session.getName();
        String email = session.getEmail();
        String phone = session.getPhone();
        // Address not stored yet, set blank or get from API later
        String address = "";

        // Set TextViews with user data
        if (name != null && !name.isEmpty()) {
            tvFullName.setText(name);
            tvFullNameDisplay.setText(name);
        } else {
            tvFullName.setText("Not provided");
            tvFullNameDisplay.setText("User");
        }

        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
            tvEmailDisplay.setText(email);
        } else {
            tvEmail.setText("Not provided");
            tvEmailDisplay.setText("email@example.tech");
        }

        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText(phone);
        } else {
            tvPhone.setText("Not provided");
        }

        tvAddress.setText(address.isEmpty() ? "Not provided" : address);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently removed.");

        builder.setPositiveButton("Yes, Delete", (dialog, which) -> {
            // Show loading dialog
            AlertDialog loadingDialog = showLoadingDialog();

            // Submit delete request
            submitDeleteRequest(loadingDialog);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);
        builder.show();
    }

    private AlertDialog showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Processing your request...");
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void submitDeleteRequest(AlertDialog loadingDialog) {
        String url = "https://premium-api.dvalleybd.com/account-delete-request.php";

        // Get user ID from session manager
        int userId = session.getUserIdInt();

        // Create JSON body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            loadingDialog.dismiss();
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    loadingDialog.dismiss();
                    handleDeleteResponse(response);
                },
                error -> {
                    loadingDialog.dismiss();
                    handleDeleteError(error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // Add auth token if needed
                String token = session.getToken();
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        // Set retry policy
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                0,     // No retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add request to queue
        requestQueue.add(jsonObjectRequest);
    }

    private void handleDeleteResponse(JSONObject response) {
        try {
            String message = "Your request is submitted. We will delete your account soon and email you to know.";
            boolean success = response.optBoolean("success", true);

            if (success) {
                // Try to get message from response if available
                if (response.has("message")) {
                    message = response.getString("message");
                }
                showSuccessDialog(message);
            } else {
                String errorMsg = response.optString("error", "Failed to submit delete request");
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Show default success message even if response parsing fails
            showSuccessDialog("Your request is submitted. We will delete your account soon and email you to know.");
        }
    }

    private void handleDeleteError(VolleyError error) {
        String errorMessage = "Network error. Please try again.";

        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            if (statusCode == 400) {
                errorMessage = "Invalid request. Please contact support.";
            } else if (statusCode == 401) {
                errorMessage = "Unauthorized. Please login again.";
            } else if (statusCode == 404) {
                errorMessage = "Service not available.";
            } else if (statusCode >= 500) {
                errorMessage = "Server error. Please try again later.";
            }
        } else if (error.getMessage() != null) {
            if (error.getMessage().contains("timeout")) {
                errorMessage = "Request timeout. Please check your connection.";
            } else if (error.getMessage().contains("network")) {
                errorMessage = "Network error. Please check your connection.";
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create custom layout for dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_success, null);
        builder.setView(dialogView);

        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        tvMessage.setText(message);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();

            // Logout user
            session.logout();

            // Navigate to login screen
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel all pending requests
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}