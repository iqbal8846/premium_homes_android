package www.dpremiumhomes.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.util.Iterator;

import www.dpremiumhomes.com.FlatDetailsActivity;
import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.helpers.SessionManager;

public class MyFlatFragment extends Fragment {

    private static final String TAG = "MY_FLAT_FRAGMENT";

    // UI Components
    private ImageView ivPropertyImage;
    private TextView tvPropertyName, tvLocation, tvFlatNo, tvFlatSize, tvPrice;
    private Button viewDetails;

    // Data
    private SessionManager sessionManager;
    private String propertyId = "";
    private JSONObject flatData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_flat, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
        loadFlatData();
    }

    private void initViews(View view) {
        ivPropertyImage = view.findViewById(R.id.ivPropertyImage);
        tvPropertyName = view.findViewById(R.id.tvPropertyName);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvFlatNo = view.findViewById(R.id.tvFlatNo);
        tvFlatSize = view.findViewById(R.id.tvFlatSize);
        tvPrice = view.findViewById(R.id.tvPrice);
        viewDetails = view.findViewById(R.id.viewDetails);

        sessionManager = new SessionManager(requireContext());
    }

    private void setupClickListeners() {
        viewDetails.setOnClickListener(v -> {
            if (!propertyId.isEmpty() && flatData != null) {
                Intent intent = new Intent(requireContext(), FlatDetailsActivity.class);
                intent.putExtra("property_id", propertyId);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Property details not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFlatData() {
        showLoading(true);

        try {
            // Debug: Print session data
            Log.d(TAG, "Loading flat data...");
            sessionManager.debugPrint();

            // Get flat details
            JSONObject flatDetails = sessionManager.getFlatDetails();
            Log.d(TAG, "Flat details from session: " + flatDetails.toString());

            // Check if flats exist
            if (flatDetails == null || flatDetails.length() == 0) {
                Log.d(TAG, "No flats found in session");
                showEmptyState();
                return;
            }

            // Get the first property ID and data
            Iterator<String> keys = flatDetails.keys();
            if (!keys.hasNext()) {
                Log.d(TAG, "No keys in flatDetails");
                showEmptyState();
                return;
            }

            // Get first property
            propertyId = keys.next();
            flatData = flatDetails.getJSONObject(propertyId);

            Log.d(TAG, "Loading flat ID: " + propertyId);
            Log.d(TAG, "Flat data: " + flatData.toString());

            // Display flat data
            displayFlatData();

        } catch (Exception e) {
            Log.e(TAG, "Error loading flat data", e);
            Toast.makeText(requireContext(), "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showEmptyState();
        } finally {
            showLoading(false);
        }
    }

    private void displayFlatData() {
        if (flatData == null) return;

        try {
            // Extract data with defaults
            String propertyName = flatData.optString("name", "Premium Property");
            String location = flatData.optString("location", "Location not specified");
            String flatNo = flatData.optString("flatNo", "N/A");
            String flatSize = flatData.optString("flatSize", "N/A");
            String price = flatData.optString("price", "0");
            String imageUrl = flatData.optString("image", "");

            // Format price if needed (remove commas or format as needed)
            String formattedPrice = formatPrice(price);

            // Set text values
            tvPropertyName.setText(propertyName);
            tvLocation.setText(location);
            tvFlatNo.setText("Flat No: " + flatNo);
            tvFlatSize.setText("Size: " + flatSize);
            tvPrice.setText("৳ " + formattedPrice);

            // Load image
            loadPropertyImage(imageUrl);

            Log.d(TAG, "Displayed flat: " + propertyName + ", " + location);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying flat data", e);
        }
    }

    private String formatPrice(String price) {
        if (price == null || price.isEmpty()) return "0";
        // Remove any existing commas and add proper formatting if needed
        return price.replace(",", "");
    }

    private void loadPropertyImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("--") && !imageUrl.equals("null")) {
            // Construct full URL if it's a relative path
            String fullImageUrl;
            if (imageUrl.startsWith("http")) {
                fullImageUrl = imageUrl;
            } else {
                // Make sure the base URL is correct for your server
                String baseUrl = "https://premium-api.dvalleybd.com";
                // Remove leading slash if present to avoid double slashes
                String cleanPath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
                fullImageUrl = baseUrl + "/" + cleanPath;
            }

            Log.d(TAG, "Loading image from: " + fullImageUrl);

            Glide.with(this)
                    .load(fullImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.sample_image)
                    .error(R.drawable.sample_image)
                    .centerCrop()
                    .into(ivPropertyImage);
        } else {
            Log.d(TAG, "No image URL provided, using placeholder");
            ivPropertyImage.setImageResource(R.drawable.sample_image);
        }
    }

    private void showEmptyState() {
        // Instead of hiding views, we can show a message in the UI
        tvPropertyName.setText("No Property Found");
        tvLocation.setText("You don't have any properties assigned yet");
        tvFlatNo.setVisibility(View.GONE);
        tvFlatSize.setVisibility(View.GONE);
        tvPrice.setVisibility(View.GONE);
        viewDetails.setVisibility(View.GONE);
        ivPropertyImage.setImageResource(R.drawable.sample_image);
    }

    private void showLoading(boolean show) {
        if (isAdded()) {
            if (show) {
                ((MainActivity) requireActivity()).showLoader();
            } else {
                ((MainActivity) requireActivity()).hideLoader();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment resumes
        loadFlatData();
    }
}