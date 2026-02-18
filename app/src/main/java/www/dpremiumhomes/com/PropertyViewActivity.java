package www.dpremiumhomes.com;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import www.dpremiumhomes.com.adapters.AmenityAdapter;
import www.dpremiumhomes.com.adapters.SpecificationAdapter;
import www.dpremiumhomes.com.adapters.UnitAdapter;
import www.dpremiumhomes.com.models.AmenityItem;
import www.dpremiumhomes.com.models.SpecificationItem;
import www.dpremiumhomes.com.models.UnitItem;

public class PropertyViewActivity extends AppCompatActivity {

    /* ======================= API ======================= */
    private static final String PROPERTY_DETAILS_URL =
            "https://premium-api.dvalleybd.com/projects.php?action=get-property-by-id&id=";

    /* ======================= UI ======================= */
    private TextView propertyLocation, propertyTitle, propertyBedrooms, propertyName,
            propertyDescription, tvApartmentsNumber, tvShareNumber, mapLocation;

    private ImageView propertyHeroImage, propertyImage2, propertyImage3, mapImage;
    private CardView savePropertyBtn, contactAgentBtn; // Changed from Button to CardView
    private CardView downloadBrochureBtn, submitContactBtn; // Changed from Button to CardView
    //private Button submitContactBtn; // This remains Button
    private FloatingActionButton fabSave;
    private ProgressBar progressBar;
    private View mainContent;

    private EditText nameEditText, phoneEditText, emailEditText, messageEditText;

    private RecyclerView specsRecyclerView, unitsRecyclerView, amenitiesRecyclerView;

    /* ======================= DATA ======================= */
    private String propertyId;
    private RequestQueue requestQueue;

    private SpecificationAdapter specificationAdapter;
    private UnitAdapter unitAdapter;
    private AmenityAdapter amenityAdapter;

    /* ======================= LIFECYCLE ======================= */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_property_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

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

        requestQueue = Volley.newRequestQueue(this);
        fetchPropertyDetails();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            mainContent.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
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

        // All these are CardView in XML, so use CardView type
        savePropertyBtn = findViewById(R.id.savePropertyBtn);
        contactAgentBtn = findViewById(R.id.contactAgentBtn);
        downloadBrochureBtn = findViewById(R.id.downloadBrochureBtn);

        // Only this one is actually a Button in XML
        submitContactBtn = findViewById(R.id.submitContactBtn);

        fabSave = findViewById(R.id.fabSave);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);

        progressBar = findViewById(R.id.progressBar);
        mainContent = findViewById(R.id.mainContent);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        specsRecyclerView = findViewById(R.id.specificationsRecyclerView);
        unitsRecyclerView = findViewById(R.id.unitsList);
        amenitiesRecyclerView = findViewById(R.id.luxuryApartments_amenities);
    }

    private void initRecyclerViews() {
        // Specifications RecyclerView
        specsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        specificationAdapter = new SpecificationAdapter(new ArrayList<>());
        specsRecyclerView.setAdapter(specificationAdapter);
        specsRecyclerView.setNestedScrollingEnabled(false);

        // Units RecyclerView
        unitsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        unitAdapter = new UnitAdapter(new ArrayList<>());
        unitsRecyclerView.setAdapter(unitAdapter);
        unitsRecyclerView.setNestedScrollingEnabled(false);

        // Amenities RecyclerView (Grid with 2 columns)
        amenitiesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        amenityAdapter = new AmenityAdapter(new ArrayList<>());
        amenitiesRecyclerView.setAdapter(amenityAdapter);
        amenitiesRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initClickListeners() {
        savePropertyBtn.setOnClickListener(v ->
                Toast.makeText(this, "Property saved!", Toast.LENGTH_SHORT).show());

        contactAgentBtn.setOnClickListener(v ->
                Toast.makeText(this, "Contacting agent...", Toast.LENGTH_SHORT).show());

        fabSave.setOnClickListener(v ->
                Toast.makeText(this, "Saved to favorites!", Toast.LENGTH_SHORT).show());

        downloadBrochureBtn.setOnClickListener(v ->
                Toast.makeText(this, "Downloading brochure...", Toast.LENGTH_SHORT).show());

        submitContactBtn.setOnClickListener(v -> submitContactForm());

        findViewById(R.id.viewOnMapBtn).setOnClickListener(v ->
                Toast.makeText(this, "Opening map...", Toast.LENGTH_SHORT).show());
    }

    /* ======================= API ======================= */

    private void fetchPropertyDetails() {
        showLoading(true);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                PROPERTY_DETAILS_URL + propertyId,
                null,
                response -> {
                    showLoading(false);
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

        requestQueue.add(request);
    }

    /* ======================= DATA BINDING ======================= */

    private void bindPropertyData(JSONObject data) throws JSONException {
        // Bind basic property data
        propertyLocation.setText(data.optString("location").toUpperCase());
        propertyTitle.setText(data.optString("name"));
        propertyName.setText(data.optString("name"));
        propertyBedrooms.setText(data.optString("types"));
        propertyDescription.setText(data.optString("description"));
        tvApartmentsNumber.setText(data.optString("apartmentCount"));
        mapLocation.setText(data.optString("fullLocation"));

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

            // Note: The API uses "img" field, not "icon"
            String imageUrl = amenityObj.optString("img", "");
            String name = amenityObj.optString("name", "");

            if (!name.isEmpty()) {
                AmenityItem amenityItem = new AmenityItem(name, imageUrl);
                amenityList.add(amenityItem);
            }
        }

        // Update the adapter with amenities data
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
                units.add(new UnitItem(
                        label,
                        value,
                        bedroom,
                        bathroom,
                        price
                ));
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

    /* ======================= HELPERS ======================= */

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

    private void submitContactForm() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

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

        // Basic email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        // Show success message
        Toast.makeText(this, "Request submitted successfully! We'll contact you soon.", Toast.LENGTH_SHORT).show();

        // Clear form
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        messageEditText.setText("");
    }


    private void showError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) requestQueue.stop();
    }
}