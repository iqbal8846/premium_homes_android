package com.premium_homes.tech;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PrivacyPolicy extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private ImageView backButton;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy_policy);

        // Initialize views
        initializeViews();

        // Set up window insets listener
        setupWindowInsets();

        // Set up back button
        setupBackButton();

        // Configure and load WebView
        setupWebView();

        // Load the privacy policy URL
        loadPrivacyPolicy();
    }

    private void initializeViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        errorMessage = findViewById(R.id.errorMessage);
        backButton = findViewById(R.id.backButton);

        // Initially hide error message
        errorMessage.setVisibility(View.GONE);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();

        // Enable JavaScript (might be needed for Google Docs)
        webSettings.setJavaScriptEnabled(true);

        // Enable zoom controls
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Improve rendering
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Enable DOM storage (useful for some sites)
        webSettings.setDomStorageEnabled(true);

        // Set WebViewClient to handle page navigation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Show progress bar when page starts loading
                progressBar.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide progress bar when page finishes loading
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // Show error message if there's an error loading the page
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
            }

            // For newer Android versions
            @Override
            public void onReceivedError(WebView view, android.webkit.WebResourceRequest request,
                                        android.webkit.WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Only show error for main frame requests
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadPrivacyPolicy() {
        // Google Doc URL
        String policyUrl = "https://dpremiumhomes.com/privacy-policy";

        // Load the URL in WebView
        webView.loadUrl(policyUrl); // Using pub version for better mobile viewing
    }

    @Override
    public void onBackPressed() {
        // Handle WebView navigation when possible
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}