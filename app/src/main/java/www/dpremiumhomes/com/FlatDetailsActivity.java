package www.dpremiumhomes.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import org.json.JSONObject;

import www.dpremiumhomes.com.helpers.SessionManager;

public class FlatDetailsActivity extends AppCompatActivity {

    private static final String TAG = "FLAT_DETAILS";

    // UI Components
    private LinearLayout layoutBack;
    private WebView webView;
    private ProgressBar loadingIndicator;
    private TextView tvError, tvLive, tvPropertyName, tvCameraName, tvLocation;
    private TextView textOwnerName, textOwnerPhone, textOwnerEmail;
    private TextView receiptBtn, totalExpenditure;

    // Camera selection
    private LinearLayout camera1, camera2;
    private TextView txt1, txt2;
    private ImageView img1, img2;

    // Data
    private SessionManager sessionManager;
    private JSONObject flatData;
    private String propertyId = "";

    // Video links
    private String videoLink1 = "";
    private String videoLink2 = "";
    private String moneyReceiptLink = "";
    private String purchaseDetailsLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_details);

        // Get property ID from intent
        if (getIntent().hasExtra("property_id")) {
            propertyId = getIntent().getStringExtra("property_id");
            Log.d(TAG, "Received property ID: " + propertyId);
        }

        initViews();
        setupWebView();
        loadFlatData();
        setupClickListeners();
    }

    private void initViews() {
        // Top bar
        layoutBack = findViewById(R.id.layoutBack);

        // Camera selection
        camera1 = findViewById(R.id.camera1);
        camera2 = findViewById(R.id.camera2);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);

        // WebView and loading
        webView = findViewById(R.id.webView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        tvError = findViewById(R.id.tvError);
        tvLive = findViewById(R.id.tvLive);

        // Text views
        tvPropertyName = findViewById(R.id.tvPropertyName);
        tvCameraName = findViewById(R.id.tvCameraName);
        tvLocation = findViewById(R.id.tvLocation);
        textOwnerName = findViewById(R.id.text_owner_name);
        textOwnerPhone = findViewById(R.id.text_owner_phone);
        textOwnerEmail = findViewById(R.id.text_owner_email);

        // Buttons
        receiptBtn = findViewById(R.id.receiptid);
        totalExpenditure = findViewById(R.id.totalExpenditure);

        sessionManager = new SessionManager(this);
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                tvLive.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvLive.setVisibility(View.GONE);
                Log.e(TAG, "WebView error: " + description);
            }
        });
    }

    private void loadFlatData() {
        try {
            // First try to get flat by ID
            if (!propertyId.isEmpty()) {
                flatData = sessionManager.getFlatById(propertyId);
                Log.d(TAG, "Looking for flat with ID: " + propertyId);
            }

            // If not found, get first flat
            if (flatData == null) {
                flatData = sessionManager.getFirstFlat();
                Log.d(TAG, "Using first flat");
            }

            if (flatData == null) {
                Log.e(TAG, "No flat data found");
                Toast.makeText(this, "No flat details found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "Flat data loaded: " + flatData.toString());

            // Extract data
            String propertyName = flatData.optString("name", "The Premium Garden View");
            String location = flatData.optString("location", "Location not specified");
            String flatNo = flatData.optString("flatNo", "");
            String ownerName = sessionManager.getName();
            String ownerPhone = sessionManager.getPhone();
            String ownerEmail = sessionManager.getEmail();

            // Links
            videoLink1 = flatData.optString("videoLink", "");
            videoLink2 = flatData.optString("videoLink2", "");
            moneyReceiptLink = flatData.optString("moneyReceiptLink", "");
            purchaseDetailsLink = flatData.optString("purchaseDetailsLink", "");

            // Set data to views
            tvPropertyName.setText(propertyName);

            String locationText = propertyName;
            if (!flatNo.isEmpty()) {
                locationText += " - Flat " + flatNo;
            }
            tvLocation.setText(locationText);

            textOwnerName.setText(ownerName);
            textOwnerPhone.setText(ownerPhone);
            textOwnerEmail.setText(ownerEmail);

            // Initialize with Camera 1
            selectCamera1();

            // Load first video if available
            if (!videoLink1.isEmpty()) {
                loadVideo(videoLink1);
            } else if (!videoLink2.isEmpty()) {
                loadVideo(videoLink2);
                selectCamera2();
            } else {
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("No video stream available");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading flat data", e);
            Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVideo(String url) {
        if (url == null || url.isEmpty()) {
            loadingIndicator.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Video not available");
            return;
        }

        // Check if it's an HLS stream (m3u8)
        if (url.contains(".m3u8")) {
            // For HLS streams, you might need to use a video player or specific HLS player
            // For now, try to load in WebView
            String videoHtml = "<html><body style='margin:0;padding:0;'>" +
                    "<video width='100%' height='100%' controls autoplay>" +
                    "<source src='" + url + "' type='application/x-mpegURL'>" +
                    "</video></body></html>";
            webView.loadData(videoHtml, "text/html", "utf-8");
        } else {
            // Regular URL
            webView.loadUrl(url);
        }

        loadingIndicator.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvLive.setVisibility(View.GONE);
    }

    private void selectCamera1() {
        tvCameraName.setText("Camera 1");

        // Camera 1 selected style
        txt1.setTextColor(ContextCompat.getColor(this, R.color.white));
        ImageViewCompat.setImageTintList(img1, ContextCompat.getColorStateList(this, R.color.white));
        camera1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gradiant_dark_green));

        // Camera 2 normal style
        txt2.setTextColor(ContextCompat.getColor(this, R.color.gray_500));
        ImageViewCompat.setImageTintList(img2, ContextCompat.getColorStateList(this, R.color.gray_500));
        camera2.setBackgroundResource(android.R.color.transparent);
    }

    private void selectCamera2() {
        tvCameraName.setText("Camera 2");

        // Camera 2 selected style
        txt2.setTextColor(ContextCompat.getColor(this, R.color.white));
        ImageViewCompat.setImageTintList(img2, ContextCompat.getColorStateList(this, R.color.white));
        camera2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gradiant_dark_green));

        // Camera 1 normal style
        txt1.setTextColor(ContextCompat.getColor(this, R.color.gray_500));
        ImageViewCompat.setImageTintList(img1, ContextCompat.getColorStateList(this, R.color.gray_500));
        camera1.setBackgroundResource(android.R.color.transparent);
    }

    private void setupClickListeners() {
        // Back button
        layoutBack.setOnClickListener(v -> finish());

        // Camera selection
        camera1.setOnClickListener(v -> {
            selectCamera1();
            if (!videoLink1.isEmpty()) {
                loadVideo(videoLink1);
            } else {
                Toast.makeText(this, "Camera 1 not available", Toast.LENGTH_SHORT).show();
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        camera2.setOnClickListener(v -> {
            selectCamera2();
            if (!videoLink2.isEmpty()) {
                loadVideo(videoLink2);
            } else {
                Toast.makeText(this, "Camera 2 not available", Toast.LENGTH_SHORT).show();
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        // Money receipt button
        receiptBtn.setOnClickListener(v -> {
            if (!moneyReceiptLink.isEmpty()) {
                openLink(moneyReceiptLink);
            } else {
                Toast.makeText(this, "Money receipt not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Total expenditure button
        totalExpenditure.setOnClickListener(v -> {
            if (!purchaseDetailsLink.isEmpty()) {
                openLink(purchaseDetailsLink);
            } else {
                Toast.makeText(this, "Purchase details not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening link: " + e.getMessage());
            Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}