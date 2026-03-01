package com.premium_homes.tech;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Contact_or_MessageActivity extends AppCompatActivity {

    // Form Fields
    private EditText etName, etEmail, etPhone, etSubject, etMessage;
    private CardView btnSubmit;
    private ProgressBar progressBar;
    private TextView btnSubmitText;
    private ImageView btnSubmitIcon;

    // Office Info Views
    private CardView cardCorporate, cardSite, cardAti, cardZonal;
    private ImageView btnBack, btnFacebook, btnLinkedIn, btnYouTube, btnInstagram;
    private TextView tvPhone, tvEmail, tvAddress, tvBusinessHours, tvOfficeName;
    private LinearLayout privacy_policy;

    // Volley RequestQueue
    private RequestQueue requestQueue;
    private static final String TAG = "ContactActivity";

    // API URL
    private static final String CONTACT_API_URL = "https://api.tphl-erp.dvalleybd.com/api/v1/contact/create";

    // Tab data list
    private List<OfficeInfo> officeList = new ArrayList<>();
    private int currentTab = 0; // 0: Corporate, 1: Site, 2: Ati Society, 3: Zonal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_or_message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize office data
        initializeOfficeData();

        // Initialize views
        initViews();

        // Set click listeners
        setupClickListeners();

        // Show default office (Corporate Office)
        showOfficeInfo(currentTab);
    }

    private void initViews() {
        // EditText fields
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);

        // Submit Button components
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
        btnSubmitText = findViewById(R.id.btnSubmitText);
        btnSubmitIcon = findViewById(R.id.btnSubmitIcon);

        // CardViews
        cardCorporate = findViewById(R.id.cardCorporate);
        cardSite = findViewById(R.id.cardSite);
        cardAti = findViewById(R.id.cardAti);
        cardZonal = findViewById(R.id.cardZonal);

        // Icons
        btnBack = findViewById(R.id.btnBack);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnLinkedIn = findViewById(R.id.btnLinkedIn);
        btnYouTube = findViewById(R.id.btnYouTube);
        btnInstagram = findViewById(R.id.btnInstagram);

        // TextViews
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvBusinessHours = findViewById(R.id.tvBusinessHours);
        tvOfficeName = findViewById(R.id.tvOfficeName);

        privacy_policy=findViewById(R.id.privacy_policy);
    }

    private void initializeOfficeData() {
        // Corporate Office
        officeList.add(new OfficeInfo(
                "Corporate Office",
                "+8801958253300",
                "info@premium_homes.tech",
                "Land View Commercial Center, 9th Floor 28 Gulshan North C/A, Gulshan Circle-2, Dhaka",
                "Sunday - Thursday: 9:00 AM - 6:00 PM\nSaturday: 10:00 AM - 4:00 PM\nFriday: Closed"
        ));

        // Site Office
        officeList.add(new OfficeInfo(
                "Site Office",
                "+8801958253301",
                "site@premium_homes.tech",
                "2nd & 3rd Floor, Tokyo Plaza, Ashulia Model Town Khagan Bazar, Dhaka",
                "Monday - Saturday: 9:00 AM - 7:00 PM\nSunday: Closed"
        ));

        // Ati Society (Site Office 2)
        officeList.add(new OfficeInfo(
                "Ati Society (Site Office 2)",
                "+8801958253302",
                "ati@premium_homes.tech",
                "House 04 (Upazila Settlement Office Building), 2nd floor, Avenue Road-1, Ati Model Town Society, Dhaka",
                "Monday - Friday: 10:00 AM - 6:00 PM\nSaturday: 10:00 AM - 4:00 PM\nSunday: Closed"
        ));

        // Zonal Office
        officeList.add(new OfficeInfo(
                "Zonal Office",
                "+8801958253303",
                "zonal@premium_homes.tech",
                "23/2 ,SEL HUQ Skypark 4th floor,Oposite of Wonderland (Shishumela), Dhaka",
                "Monday - Saturday: 9:00 AM - 6:00 PM\nSunday: 10:00 AM - 4:00 PM"
        ));
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Submit button
        btnSubmit.setOnClickListener(v -> submitForm());

        // CardView click listeners
        cardCorporate.setOnClickListener(v -> {
            currentTab = 0;
            selectOfficeType(0);
            showOfficeInfo(0);
        });
        cardSite.setOnClickListener(v -> {
            currentTab = 1;
            selectOfficeType(1);
            showOfficeInfo(1);
        });
        cardAti.setOnClickListener(v -> {
            currentTab = 2;
            selectOfficeType(2);
            showOfficeInfo(2);
        });
        cardZonal.setOnClickListener(v -> {
            currentTab = 3;
            selectOfficeType(3);
            showOfficeInfo(3);
        });

        // Social media buttons
        btnFacebook.setOnClickListener(v -> openFacebook());
        btnLinkedIn.setOnClickListener(v -> openLinkedIn());
        btnYouTube.setOnClickListener(v -> openYouTube());
        btnInstagram.setOnClickListener(v -> openInstagram());

        // Phone click
        tvPhone.setOnClickListener(v -> {
            String phoneNumber = officeList.get(currentTab).getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });

        // Email click
        tvEmail.setOnClickListener(v -> {
            String emailAddress = officeList.get(currentTab).getEmail();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + emailAddress));
            startActivity(intent);
        });

        //Privacy Policy
        privacy_policy.setOnClickListener(v ->{
            startActivity(new Intent(Contact_or_MessageActivity.this,PrivacyPolicy.class));
        });
    }

    /**
     * Contact Form Methods
     */

    private void submitForm() {
        // Get form data
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        // Validate form
        if (!validateForm(name, email, phone, subject, message)) {
            return;
        }

        // Send to API
        sendContactToApi(name, email, phone, subject, message);
    }

    private boolean validateForm(String name, String email, String phone, String subject, String message) {
        // Validate Name
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return false;
        }

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Validate Phone
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return false;
        }

        // Validate Subject
        if (TextUtils.isEmpty(subject)) {
            etSubject.setError("Subject is required");
            etSubject.requestFocus();
            return false;
        }

        // Validate Message
        if (TextUtils.isEmpty(message)) {
            etMessage.setError("Message is required");
            etMessage.requestFocus();
            return false;
        }

        return true;
    }

    private void sendContactToApi(String name, String email, String phone, String subject, String message) {
        // Show loading state
        showLoading(true);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("phone", phone);
            jsonBody.put("subject", subject);
            jsonBody.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
            showLoading(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                CONTACT_API_URL,
                jsonBody,
                response -> handleSuccessResponse(response),
                error -> handleErrorResponse(error)
        );

        // Set retry policy for better network handling
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Set tag for cancellation
        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void handleSuccessResponse(JSONObject response) {
        showLoading(false);

        try {
            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                clearForm();
            } else {
                Toast.makeText(this,
                        "Failed to submit. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error parsing response",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorResponse(VolleyError error) {
        showLoading(false);

        String errorMessage = "Network error. Please try again.";

        if (error.networkResponse != null) {
            switch (error.networkResponse.statusCode) {
                case 400:
                    errorMessage = "Invalid request. Please check your input.";
                    break;
                case 500:
                    errorMessage = "Server error. Please try again later.";
                    break;
                case 404:
                    errorMessage = "API endpoint not found.";
                    break;
            }
        } else if (error instanceof TimeoutError) {
            errorMessage = "Request timeout. Please check your connection and try again.";
        } else if (error instanceof NoConnectionError) {
            errorMessage = "No internet connection. Please check your network.";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnSubmitText.setVisibility(View.GONE);
            btnSubmitIcon.setVisibility(View.GONE);
            btnSubmit.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSubmitText.setVisibility(View.VISIBLE);
            btnSubmitIcon.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(true);
        }
    }

    private void clearForm() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etSubject.setText("");
        etMessage.setText("");

        // Clear errors and focus
        etName.clearFocus();
        etEmail.clearFocus();
        etPhone.clearFocus();
        etSubject.clearFocus();
        etMessage.clearFocus();
    }

    private void showOfficeInfo(int tabIndex) {
        OfficeInfo office = officeList.get(tabIndex);
        tvOfficeName.setText(office.getName());
        tvPhone.setText(office.getPhone());
        tvEmail.setText(office.getEmail());
        tvAddress.setText(office.getAddress());
        tvBusinessHours.setText(office.getBusinessHours());
    }

    private void selectOfficeType(int type) {
        // Reset all cards to white
        cardCorporate.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardSite.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardAti.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardZonal.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        // Get text views from cards (assuming they have text views with id text1)
        TextView tvCorporate = cardCorporate.findViewById(android.R.id.text1);
        TextView tvSite = cardSite.findViewById(android.R.id.text1);
        TextView tvAti = cardAti.findViewById(android.R.id.text1);
        TextView tvZonal = cardZonal.findViewById(android.R.id.text1);

        // Reset text colors
        if (tvCorporate != null) tvCorporate.setTextColor(getResources().getColor(android.R.color.darker_gray));
        if (tvSite != null) tvSite.setTextColor(getResources().getColor(android.R.color.darker_gray));
        if (tvAti != null) tvAti.setTextColor(getResources().getColor(android.R.color.darker_gray));
        if (tvZonal != null) tvZonal.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Reset elevation
        cardCorporate.setCardElevation(1);
        cardSite.setCardElevation(1);
        cardAti.setCardElevation(1);
        cardZonal.setCardElevation(1);

        // Set selected card
        switch (type) {
            case 0: // Corporate Office
                cardCorporate.setCardBackgroundColor(getResources().getColor(R.color.emerald_600));
                if (tvCorporate != null) tvCorporate.setTextColor(getResources().getColor(android.R.color.white));
                cardCorporate.setCardElevation(4);
                break;
            case 1: // Site Office
                cardSite.setCardBackgroundColor(getResources().getColor(R.color.emerald_600));
                if (tvSite != null) tvSite.setTextColor(getResources().getColor(android.R.color.white));
                cardSite.setCardElevation(4);
                break;
            case 2: // Ati Society
                cardAti.setCardBackgroundColor(getResources().getColor(R.color.emerald_600));
                if (tvAti != null) tvAti.setTextColor(getResources().getColor(android.R.color.white));
                cardAti.setCardElevation(4);
                break;
            case 3: // Zonal Office
                cardZonal.setCardBackgroundColor(getResources().getColor(R.color.emerald_600));
                if (tvZonal != null) tvZonal.setTextColor(getResources().getColor(android.R.color.white));
                cardZonal.setCardElevation(4);
                break;
        }
    }

    private void openFacebook() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.facebook.com/thepremiumhomesltd/"));
        startActivity(intent);
    }

    private void openLinkedIn() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.linkedin.com/company/thepremiumhomes"));
        startActivity(intent);
    }

    private void openYouTube() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.youtube.com/@ThePremiumHomesLTD"));
        startActivity(intent);
    }

    private void openInstagram() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.instagram.com/thepremiumhomes.ltd"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel all pending requests
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    // Model class for office information
    private static class OfficeInfo {
        private String name;
        private String phone;
        private String email;
        private String address;
        private String businessHours;

        public OfficeInfo(String name, String phone, String email, String address, String businessHours) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.businessHours = businessHours;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public String getBusinessHours() { return businessHours; }
    }
}