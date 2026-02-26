package com.premium_homes.tech;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.premium_homes.tech.adapters.AmenityAdapter;
import com.premium_homes.tech.adapters.SpecificationAdapter;
import com.premium_homes.tech.adapters.UnitAdapter;
import com.premium_homes.tech.helpers.SessionManager;
import com.premium_homes.tech.models.AmenityItem;
import com.premium_homes.tech.models.SpecificationItem;
import com.premium_homes.tech.models.UnitItem;

public class PropertyViewActivity extends AppCompatActivity {

    /* ======================= API ======================= */
    private static final String PROPERTY_DETAILS_URL =
            "https://premium-api.dvalleybd.com/projects.php?action=get-property-by-id&id=";

    /* ======================= UI ======================= */
    private SessionManager session;
    private TextView propertyLocation, propertyTitle, propertyBedrooms, propertyName,
            propertyDescription, tvApartmentsNumber, tvShareNumber, mapLocation, submitText;

    private ImageView propertyHeroImage, propertyImage2, propertyImage3, mapImage, fabSave1;
    private CardView savePropertyBtn, contactAgentBtn, downloadBrochureBtn, submitContactBtn, openBrochureBtn;
    private FloatingActionButton fabSave;
    private ProgressBar progressBar, submitProgress, downloadProgressBar;
    private TextView downloadProgressText;
    private LinearLayout downloadButtonContent;
    private View mainContent;

    private EditText nameEditText, phoneEditText, emailEditText, messageEditText;
    private RecyclerView specsRecyclerView, unitsRecyclerView, amenitiesRecyclerView;

    /* ======================= DATA ======================= */
    private String propertyId;
    private String propertyName_str = "";
    private String brochureLink = "";
    private String fileName = "";
    private RequestQueue requestQueue;
    private static final String TAG = "PropertyViewActivity";
    private long downloadId = -1;
    private DownloadManager downloadManager;

    private SpecificationAdapter specificationAdapter;
    private UnitAdapter unitAdapter;
    private AmenityAdapter amenityAdapter;

    /* ======================= BROADCAST RECEIVER ======================= */
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completedDownloadId == downloadId) {
                    checkDownloadStatus();
                }
            }
        }
    };

    /* ======================= LIFECYCLE ======================= */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_property_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

        session = new SessionManager(this);
        requestQueue = Volley.newRequestQueue(this);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Register download receiver with proper flags for Android 14+
        registerDownloadReceiver();

        applyInsets();
        initViews();
        initRecyclerViews();
        initClickListeners();

        propertyId = getIntent().getStringExtra("propertyId");
        if (propertyId == null || propertyId.isEmpty()) {
            Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchPropertyDetails();
    }

    /**
     * Register download receiver with proper flags for Android 14+
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            // For Android 13 and above, specify RECEIVER_EXPORTED
            registerReceiver(downloadReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            // For older versions, regular registration is fine
            registerReceiver(downloadReceiver, filter);
        }
    }

    /* ======================= UI SETUP ======================= */


    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });
    }

    private void initViews() {
        propertyLocation = findViewById(R.id.propertyLocation);
        propertyTitle = findViewById(R.id.propertyTitle);
        propertyBedrooms = findViewById(R.id.propertyBedrooms);
        propertyName = findViewById(R.id.propertyName);
        propertyDescription = findViewById(R.id.propertyDescription);
        tvApartmentsNumber = findViewById(R.id.tvApartmentsNumber);
        tvShareNumber = findViewById(R.id.tvShareNumber);
        mapLocation = findViewById(R.id.mapLocation);

        propertyHeroImage = findViewById(R.id.propertyHeroImage);
        propertyImage2 = findViewById(R.id.propertyImage2);
        propertyImage3 = findViewById(R.id.propertyImage3);
        mapImage = findViewById(R.id.mapImage);

        savePropertyBtn = findViewById(R.id.savePropertyBtn);
        contactAgentBtn = findViewById(R.id.contactAgentBtn);
        downloadBrochureBtn = findViewById(R.id.downloadBrochureBtn);
        openBrochureBtn = findViewById(R.id.openBrochureBtn);
        submitContactBtn = findViewById(R.id.submitContactBtn);
        fabSave = findViewById(R.id.fabSave);
        fabSave1 = findViewById(R.id.fabSave1);


        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);

        progressBar = findViewById(R.id.progressBar);
        submitProgress = findViewById(R.id.submitProgress);
        submitText = findViewById(R.id.submitText);
        downloadProgressBar = findViewById(R.id.downloadProgressBar);
        downloadProgressText = findViewById(R.id.downloadProgressText);
        downloadButtonContent = findViewById(R.id.downloadButtonContent);
        mainContent = findViewById(R.id.mainContent);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void initRecyclerViews() {
        // Specifications RecyclerView
        specsRecyclerView = findViewById(R.id.specificationsRecyclerView);
        specsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        specificationAdapter = new SpecificationAdapter(new ArrayList<>());
        specsRecyclerView.setAdapter(specificationAdapter);
        specsRecyclerView.setNestedScrollingEnabled(false);

        // Units RecyclerView
        unitsRecyclerView = findViewById(R.id.unitsList);
        unitsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        unitAdapter = new UnitAdapter(new ArrayList<>());
        unitsRecyclerView.setAdapter(unitAdapter);
        unitsRecyclerView.setNestedScrollingEnabled(false);

        // Amenities RecyclerView (Grid with 2 columns)
        amenitiesRecyclerView = findViewById(R.id.luxuryApartments_amenities);
        amenitiesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        amenityAdapter = new AmenityAdapter(new ArrayList<>());
        amenitiesRecyclerView.setAdapter(amenityAdapter);
        amenitiesRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initClickListeners() {
        savePropertyBtn.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                savePropertyToWishlist();
                fabSave1.setImageResource(R.drawable.ic_favorite);
            } else {
                showLoginDialog();
            }
        });

        contactAgentBtn.setOnClickListener(v -> callAgent());
        fabSave.setOnClickListener(v -> {
            /**openFavoritesActivity(); **/
            if (session.isLoggedIn()) {
                savePropertyToWishlist();
                fabSave.setImageResource(R.drawable.ic_favorite);
            } else {
                showLoginDialog();
            }


        } );
        downloadBrochureBtn.setOnClickListener(v -> downloadBrochure());
        openBrochureBtn.setOnClickListener(v -> openDownloadedBrochure());
        submitContactBtn.setOnClickListener(v -> submitContactForm());

        findViewById(R.id.viewOnMapBtn).setOnClickListener(v ->
                Toast.makeText(this, "Opening map...", Toast.LENGTH_SHORT).show()
        );
    }

    /* ======================= DOWNLOAD BROCHURE WITH PROGRESS ======================= */

    private void downloadBrochure() {
        if (brochureLink == null || brochureLink.isEmpty()) {
            Toast.makeText(this, "Brochure not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if file already exists
        fileName = propertyName_str.replaceAll("[^a-zA-Z0-9]", "_") + "_Brochure.pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);

        if (file.exists()) {
            // File already downloaded, show open button
            showOpenButton();
            Toast.makeText(this, "Brochure already downloaded", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Show progress on button
            showDownloadProgress(true);

            // Convert Google Drive link to direct download link if needed
            String downloadUrl = convertToDirectDownloadLink(brochureLink);

            // Create DownloadManager request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setTitle(propertyName_str + " Brochure");
            request.setDescription("Downloading property brochure...");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);

            // Enqueue download
            downloadId = downloadManager.enqueue(request);

            // Start checking progress
            startDownloadProgressCheck();

        } catch (Exception e) {
            e.printStackTrace();
            showDownloadProgress(false);
            Toast.makeText(this, "Failed to start download", Toast.LENGTH_SHORT).show();

            // Fallback: Open in browser
            openBrochureInBrowser();
        }
    }

    private void startDownloadProgressCheck() {
        new Thread(() -> {
            boolean downloading = true;
            while (downloading) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(q);

                if (cursor != null && cursor.moveToFirst()) {
                    int bytes_downloaded = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int status = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        runOnUiThread(() -> {
                            showDownloadProgress(false);
                            Toast.makeText(PropertyViewActivity.this,
                                    "Download complete!", Toast.LENGTH_LONG).show();
                            showOpenButton();
                        });
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false;
                        runOnUiThread(() -> {
                            showDownloadProgress(false);
                            Toast.makeText(PropertyViewActivity.this,
                                    "Download failed", Toast.LENGTH_SHORT).show();
                        });
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        if (bytes_total > 0) {
                            int progress = (int) ((bytes_downloaded * 100L) / bytes_total);
                            runOnUiThread(() -> updateDownloadProgress(progress));
                        }
                    }
                    cursor.close();
                }

                try {
                    Thread.sleep(1000); // Update every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showDownloadProgress(boolean show) {
        if (show) {
            downloadButtonContent.setVisibility(View.GONE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadProgressText.setVisibility(View.VISIBLE);
            downloadBrochureBtn.setEnabled(false);
            downloadProgressText.setText("Starting...");
        } else {
            downloadButtonContent.setVisibility(View.VISIBLE);
            downloadProgressBar.setVisibility(View.GONE);
            downloadProgressText.setVisibility(View.GONE);
            downloadBrochureBtn.setEnabled(true);
        }
    }

    private void updateDownloadProgress(int progress) {
        downloadProgressText.setText(progress + "%");
        downloadProgressBar.setProgress(progress);
    }

    private void showOpenButton() {
        openBrochureBtn.setVisibility(View.VISIBLE);
        downloadBrochureBtn.setVisibility(View.GONE);
    }

    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);

        if (cursor != null && cursor.moveToFirst()) {
            int status = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            int reason = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON));

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                runOnUiThread(() -> {
                    showDownloadProgress(false);
                    Toast.makeText(this, "Download complete!", Toast.LENGTH_LONG).show();
                    showOpenButton();
                });
            } else if (status == DownloadManager.STATUS_FAILED) {
                runOnUiThread(() -> {
                    showDownloadProgress(false);
                    Toast.makeText(this, "Download failed: " + reason, Toast.LENGTH_SHORT).show();
                });
            }
            cursor.close();
        }
    }

    /**
     * Converts Google Drive sharing links to direct download links
     */
    private String convertToDirectDownloadLink(String driveLink) {
        if (driveLink.contains("drive.google.tech")) {
            String fileId = extractGoogleDriveFileId(driveLink);
            if (fileId != null) {
                return "https://drive.google.com/uc?export=download&id=" + fileId;
            }
        }
        return driveLink;
    }

    /**
     * Extracts file ID from Google Drive link
     */
    private String extractGoogleDriveFileId(String driveLink) {
        try {
            if (driveLink.contains("/d/")) {
                int start = driveLink.indexOf("/d/") + 3;
                int end = driveLink.indexOf("/", start);
                if (end == -1) end = driveLink.length();
                return driveLink.substring(start, end);
            }
            else if (driveLink.contains("id=")) {
                String[] parts = driveLink.split("id=");
                if (parts.length > 1) {
                    String id = parts[1];
                    if (id.contains("&")) {
                        id = id.substring(0, id.indexOf("&"));
                    }
                    return id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fallback method: Open brochure in browser
     */
    private void openBrochureInBrowser() {
        if (brochureLink != null && !brochureLink.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(brochureLink));
            startActivity(intent);
        }
    }

    /**
     * Opens downloaded brochure if exists
     */
    private void openDownloadedBrochure() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);

        if (file.exists()) {
            try {
                Uri uri = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "File not found. Please download again.", Toast.LENGTH_SHORT).show();
            downloadBrochureBtn.setVisibility(View.VISIBLE);
            openBrochureBtn.setVisibility(View.GONE);
        }
    }

    /* ======================= API CALLS ======================= */

    private void fetchPropertyDetails() {
        showLoading(true);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                PROPERTY_DETAILS_URL + propertyId,
                null,
                response -> {
                    showLoading(false);
                    Log.d("API_RESPONSE", response.toString());

                    try {
                        if (response.getBoolean("success")) {
                            JSONObject property = response.getJSONObject("property");
                            bindPropertyData(property);
                        } else {
                            showError(response.getString("message"));
                        }
                    } catch (JSONException e) {
                        showError(e.getMessage());
                    }
                },
                error -> {
                    showLoading(false);
                    showError(error.getMessage());
                });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void savePropertyToWishlist() {

        int userId = Integer.parseInt(session.getUserId());
        int propertyId = Integer.parseInt(this.propertyId);

        String url = "https://premium-api.dvalleybd.com/get-favourites.php?action=add-favourite";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("SAVE_RESPONSE", response);
                    Toast.makeText(this, "Property Saved Successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("SAVE_ERROR", error.toString());
                    Toast.makeText(this, "Failed to Save Property", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(userId));
                params.put("propertyId", String.valueOf(propertyId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void sendContactToApi(String name, String phone, String email, String message) {
        setLoading(true);

        String url = "https://api.tphl-erp.dvalleybd.com/api/v1/contact/create";
        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("email", email);
            json.put("phone", phone);
            json.put("subject", "Property Inquiry - " + propertyName_str);
            json.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    setLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            showSuccessDialog(response.getString("message"));
                            clearForm();
                        } else {
                            showErrorDialog("Submission failed! Try again.");
                        }
                    } catch (JSONException e) {
                        showErrorDialog("Parsing error!");
                    }
                },
                error -> {
                    setLoading(false);
                    String msg = "Network error! Please try again.";
                    if (error.networkResponse != null) {
                        msg = "Server error: " + error.networkResponse.statusCode;
                    }
                    showErrorDialog(msg);
                });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    /* ======================= DATA BINDING ======================= */

    private void bindPropertyData(JSONObject data) throws JSONException {
        // Store property name for filename
        propertyName_str = data.optString("name", "Property");

        // Bind basic property data
        propertyLocation.setText(data.optString("location").toUpperCase());
        propertyTitle.setText(propertyName_str);
        propertyName.setText(propertyName_str);
        propertyBedrooms.setText(data.optString("types"));
        propertyDescription.setText(data.optString("description"));
        tvApartmentsNumber.setText(data.optString("apartmentCount"));
        mapLocation.setText(data.optString("fullLocation"));
        brochureLink = data.optString("brochureLink");

        // Check if brochure already downloaded
        fileName = propertyName_str.replaceAll("[^a-zA-Z0-9]", "_") + "_Brochure.pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            showOpenButton();
        }

        // Load images
        loadImage(data.optString("image"), propertyHeroImage);
        loadImage(data.optString("image2"), propertyImage2);
        loadImage(data.optString("image3"), propertyImage3);
        loadImage(data.optString("mapImage"), mapImage);

        // Bind specifications
        if (data.has("specifications")) {
            bindSpecifications(data.getJSONArray("specifications"));
        }

        // Extract and bind units
        if (data.has("specifications")) {
            extractAndBindUnits(data);
        }

        // Bind amenities
        if (data.has("amenities")) {
            bindAmenities(data.getJSONArray("amenities"));
        }
    }

    private void bindSpecifications(JSONArray array) throws JSONException {
        List<SpecificationItem> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            list.add(new SpecificationItem(
                    obj.optString("label"),
                    obj.optString("value")
            ));

            if ("Total Share".equalsIgnoreCase(obj.optString("label"))) {
                tvShareNumber.setText(obj.optString("value"));
            }
        }

        specificationAdapter.updateList(list);
    }

    private void bindAmenities(JSONArray amenitiesArray) throws JSONException {
        List<AmenityItem> amenityList = new ArrayList<>();

        for (int i = 0; i < amenitiesArray.length(); i++) {
            JSONObject amenityObj = amenitiesArray.getJSONObject(i);
            String imageUrl = amenityObj.optString("img", "");
            String name = amenityObj.optString("name", "");

            if (!name.isEmpty()) {
                amenityList.add(new AmenityItem(name, imageUrl));
            }
        }

        amenityAdapter.updateList(amenityList);
    }

    private void extractAndBindUnits(JSONObject data) throws JSONException {
        JSONArray specs = data.getJSONArray("specifications");
        List<UnitItem> units = new ArrayList<>();

        String bedroom = getSpecValue(specs, "Bedroom");
        String bathroom = getSpecValue(specs, "Bathroom");
        String price = data.optString("priceRange");

        for (int i = 0; i < specs.length(); i++) {
            JSONObject s = specs.getJSONObject(i);
            String label = s.optString("label", "");
            String value = s.optString("value", "");

            if (label.toLowerCase().contains("unit") && value.toLowerCase().contains("sft")) {
                units.add(new UnitItem(label, value, bedroom, bathroom, price));
            }
        }

        unitAdapter.updateList(units);
    }

    private String getSpecValue(JSONArray array, String key) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (key.equalsIgnoreCase(obj.optString("label"))) {
                return obj.optString("value");
            }
        }
        return "N/A";
    }

    /* ======================= UI HELPERS ======================= */

    private void loadImage(String url, ImageView view) {
        if (url != null && !url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.sample_image1)
                    .error(R.drawable.about_sample)
                    .fit()
                    .centerCrop()
                    .into(view);
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null && mainContent != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mainContent.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setLoading(boolean isLoading) {
        if (submitContactBtn != null && submitProgress != null && submitText != null) {
            submitContactBtn.setEnabled(!isLoading);
            submitProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            submitText.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void clearForm() {
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        messageEditText.setText("");
        nameEditText.clearFocus();
        phoneEditText.clearFocus();
        emailEditText.clearFocus();
        messageEditText.clearFocus();
    }

    /* ======================= DIALOGS ======================= */

    private void showLoginDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_login_required);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView btnLoginNow = dialog.findViewById(R.id.btnLoginNow);
        TextView btnContactNow = dialog.findViewById(R.id.btnContactNow);

        btnLoginNow.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(PropertyViewActivity.this, LoginActivity.class));
        });

        btnContactNow.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(PropertyViewActivity.this, Contact_or_MessageActivity.class));
        });

        dialog.show();
    }

    private void showSuccessDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_contact_success);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView msg = dialog.findViewById(R.id.dialogMessage);
        TextView ok = dialog.findViewById(R.id.dialogOkBtn);
        msg.setText(message);
        ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_error);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView msg = dialog.findViewById(R.id.errorMessage);
        TextView retry = dialog.findViewById(R.id.btnTryAgain);
        msg.setText(message);
        retry.setOnClickListener(v -> {
            dialog.dismiss();
            submitContactForm();
        });
        dialog.show();
    }

    private void callAgent() {
        String phoneNumber = "+8801958253300";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void openFavoritesActivity() {
        startActivity(new Intent(PropertyViewActivity.this, FavoritesActivity.class));
    }

    private void submitContactForm() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        sendContactToApi(name, phone, email, message);
    }

    private void showError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
    }

    /* ======================= LIFECYCLE ======================= */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
        try {
            unregisterReceiver(downloadReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}