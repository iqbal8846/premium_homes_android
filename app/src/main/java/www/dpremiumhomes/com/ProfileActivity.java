package www.dpremiumhomes.com;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import www.dpremiumhomes.com.helpers.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView ivProfilePicture;
    private ImageView backButton;
    private TextInputEditText etFullName, etEmail, etPhone, etAddress;
    private MaterialButton btnSaveChanges;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }



        // Initialize SessionManager
        session = new SessionManager(this);

        // Initialize Views
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        backButton = findViewById(R.id.id_back);


        backButton.setOnClickListener(v -> onBackPressed());


        // Load user data from SessionManager
        loadUserData();


        // Save changes click listener
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadUserData() {
        // Get data from session
        String name = session.getName();
        String email = session.getEmail();
        String phone = session.getPhone();
        // Address not stored yet, set blank or get from API later
        String address = "";

        // Set UI fields
        etFullName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etAddress.setText(address);
    }

    private void saveProfileChanges() {

        String updatedName = etFullName.getText().toString().trim();
        String updatedEmail = etEmail.getText().toString().trim();
        String updatedPhone = etPhone.getText().toString().trim();
        String updatedAddress = etAddress.getText().toString().trim();

        if (updatedName.isEmpty()) {
            etFullName.setError("Name is required");
            return;
        }

        if (updatedEmail.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        // ✅ Keep existing flatDetails from session
        session.saveUser(
                session.getUserId(),
                updatedName,
                updatedEmail,
                updatedPhone,
                session.getToken(),
                session.getFlatDetails()   // 🔥 THIS FIXES THE ERROR
        );

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

}
