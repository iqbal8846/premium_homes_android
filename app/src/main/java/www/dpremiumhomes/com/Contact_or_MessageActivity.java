package www.dpremiumhomes.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class Contact_or_MessageActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etSubject, etMessage;
    private CardView cardCorporate, cardSite, cardAti, cardZonal;
    private ImageView btnBack, btnFacebook, btnLinkedIn, btnYouTube, btnInstagram;
    private TextView tvPhone, tvEmail, tvAddress, tvBusinessHours, tvOfficeName;

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

        // CardViews (replaced Buttons)
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
    }

    private void initializeOfficeData() {


        // Corporate Office
        officeList.add(new OfficeInfo(
                "Corporate Office",
                "+8801958253300",
                "info@dpremiumhomes.com",
                "Land View Commercial Center, 9th Floor 28 Gulshan North C/A, Gulshan Circle-2, Dhaka",
                "Sunday - Thursday: 9:00 AM - 6:00 PM\nSaturday: 10:00 AM - 4:00 PM\nFriday: Closed"
        ));

        // Site Office
        officeList.add(new OfficeInfo(
                "Site Office",
                "+8801958253301",
                "site@dpremiumhomes.com",
                "2nd & 3rd Floor, Tokyo Plaza, Ashulia Model Town Khagan Bazar, Dhaka",
                "Monday - Saturday: 9:00 AM - 7:00 PM\nSunday: Closed"
        ));

        // Ati Society (Site Office 2)
        officeList.add(new OfficeInfo(
                "Ati Society (Site Office 2)",
                "+8801958253302",
                "ati@dpremiumhomes.com",
                "House 04 (Upazila Settlement Office Building), 2nd floor, Avenue Road-1, Ati Model Town Society, Dhaka",
                "Monday - Friday: 10:00 AM - 6:00 PM\nSaturday: 10:00 AM - 4:00 PM\nSunday: Closed"
        ));

        // Zonal Office
        officeList.add(new OfficeInfo(
                "Zonal Office",
                "+8801958253303",
                "zonal@dpremiumhomes.com",
                "23/2 ,SEL HUQ Skypark 4th floor,Oposite of Wonderland (Shishumela), Dhaka",
                "Monday - Saturday: 9:00 AM - 6:00 PM\nSunday: 10:00 AM - 4:00 PM"
        ));


    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Submit button (you might need to find this by ID if you still have it)
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
    }

    private void showOfficeInfo(int tabIndex) {
        OfficeInfo office = officeList.get(tabIndex);

        // Update UI with office information
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

        // Reset all text colors to gray
        TextView tvCorporate = cardCorporate.findViewById(android.R.id.text1);
        TextView tvSite = cardSite.findViewById(android.R.id.text1);
        TextView tvAti = cardAti.findViewById(android.R.id.text1);
        TextView tvZonal = cardZonal.findViewById(android.R.id.text1);

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

    // Rest of your methods remain the same...
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
        intent.setData(Uri.parse("https://www.instagram.com/"));
        startActivity(intent);
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