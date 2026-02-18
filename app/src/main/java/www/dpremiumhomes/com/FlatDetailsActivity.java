package www.dpremiumhomes.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import org.json.JSONException;
import org.json.JSONObject;

import www.dpremiumhomes.com.helpers.SessionManager;

public class FlatDetailsActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    // UI
    private WebView webView;
    private ProgressBar loading;

    private TextView tvPropertyName, tvCameraName, tvLocation;
    private TextView tvOwnerName, tvOwnerPhone, tvOwnerEmail;
    private TextView txt1, txt2;

    private ImageView img1, img2;

    private TextView receiptBtn, totalExpenditureBtn;
    private LinearLayout camera1, camera2, layoutBack;

    // Links
    private String videoLink1 = "";
    private String videoLink2 = "";
    private String moneyReceiptLink = "";
    private String purchaseDetailsLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_details);

        setStatusBarColor();

        sessionManager = new SessionManager(this);

        initViews();
        setupWebView();
        loadSessionData();
        setupClicks();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(this, R.color.primary)
            );
        }
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        loading = findViewById(R.id.loadingIndicator);

        tvPropertyName = findViewById(R.id.tvPropertyName);
        tvCameraName = findViewById(R.id.tvCameraName);
        tvLocation = findViewById(R.id.tvLocation);

        tvOwnerName = findViewById(R.id.text_owner_name);
        tvOwnerPhone = findViewById(R.id.text_owner_phone);
        tvOwnerEmail = findViewById(R.id.text_owner_email);

        receiptBtn = findViewById(R.id.receiptid);
        totalExpenditureBtn = findViewById(R.id.totalExpenditure);

        camera1 = findViewById(R.id.camera1);
        camera2 = findViewById(R.id.camera2);
        layoutBack = findViewById(R.id.layoutBack);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
    }

    private void loadSessionData() {
        JSONObject flatDetails = sessionManager.getFlatDetails();
        JSONObject flat = flatDetails.optJSONObject("15");

        if (flat == null) {
            Toast.makeText(this, "Flat details not found", Toast.LENGTH_SHORT).show();
            return;
        }

        videoLink1 = flat.optString("videoLink");
        videoLink2 = flat.optString("videoLink2");
        moneyReceiptLink = flat.optString("moneyReceiptLink");
        purchaseDetailsLink = flat.optString("purchaseDetailsLink");

        tvPropertyName.setText("The Premium Garden View");
        tvCameraName.setText("Camera 1");
        tvLocation.setText("Flat " + flat.optString("flatNo"));

        tvOwnerName.setText(sessionManager.getName());
        tvOwnerPhone.setText(sessionManager.getPhone());
        tvOwnerEmail.setText(sessionManager.getEmail());

        selectCamera1();
        loadVideo(videoLink1);

    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(
                    WebView view,
                    int errorCode,
                    String description,
                    String failingUrl
            ) {
                loading.setVisibility(View.GONE);
                Toast.makeText(FlatDetailsActivity.this,
                        "Failed to load video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVideo(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Video not available", Toast.LENGTH_SHORT).show();
            return;
        }

        webView.stopLoading();
        webView.loadUrl("about:blank");

        loading.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private void setupClicks() {

        layoutBack.setOnClickListener(v -> finish());

        camera1.setOnClickListener(v -> {
            selectCamera1();
            loadVideo(videoLink1);
        });

        camera2.setOnClickListener(v -> {
            selectCamera2();
            loadVideo(videoLink2);
        });

        receiptBtn.setOnClickListener(v -> {
            if (!moneyReceiptLink.isEmpty()) {
                openLink(moneyReceiptLink);
            } else {
                Toast.makeText(this, "Receipt not available", Toast.LENGTH_SHORT).show();
            }
        });

        totalExpenditureBtn.setOnClickListener(v -> {
            if (!purchaseDetailsLink.isEmpty()) {
                openLink(purchaseDetailsLink);
            } else {
                Toast.makeText(this, "Details not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectCamera1() {
        tvCameraName.setText("Camera 1");

        txt1.setTextColor(ContextCompat.getColor(this, R.color.white));
        ImageViewCompat.setImageTintList(img1,
                ContextCompat.getColorStateList(this, R.color.white));
        camera1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gradiant_dark_green));

        txt2.setTextColor(ContextCompat.getColor(this, R.color.gray_500));
        ImageViewCompat.setImageTintList(img2,
                ContextCompat.getColorStateList(this, R.color.gray_500));
        camera2.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_white_card));
    }

    private void selectCamera2() {
        tvCameraName.setText("Camera 2");

        txt2.setTextColor(ContextCompat.getColor(this, R.color.white));
        ImageViewCompat.setImageTintList(img2,
                ContextCompat.getColorStateList(this, R.color.white));
        camera2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gradiant_dark_green));

        txt1.setTextColor(ContextCompat.getColor(this, R.color.gray_500));
        ImageViewCompat.setImageTintList(img1,
                ContextCompat.getColorStateList(this, R.color.gray_500));
        camera1.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_white_card));
    }

    private void openLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No app found to open link", Toast.LENGTH_SHORT).show();
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
