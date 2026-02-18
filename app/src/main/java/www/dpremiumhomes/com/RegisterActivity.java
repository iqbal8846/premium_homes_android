package www.dpremiumhomes.com;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextView btnRegister, tvLogin;

    private Toast mismatchToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // Real-time smart prefix validation for Confirm Password
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString().trim();
                String confirm = s.toString().trim();

                // Cancel previous toast
                if (mismatchToast != null) {
                    mismatchToast.cancel();
                }

                // Always clear previous error first
                tilConfirmPassword.setError(null);

                // If either field is empty → no validation
                if (password.isEmpty() || confirm.isEmpty()) {
                    return;
                }

                // Check if the current confirm text is a valid prefix of the password
                if (!password.startsWith(confirm)) {
                    // Mismatch in prefix → impossible to match anymore → show error + toast
                    tilConfirmPassword.setError("Passwords do not match");
                    mismatchToast = Toast.makeText(RegisterActivity.this,
                            "Passwords do not match", Toast.LENGTH_SHORT);
                    mismatchToast.show();
                }
                // Else: confirm is still a valid prefix (e.g. "7" for "77") → stay silent
            }
        });

        // Re-check when password is edited (in case user changes it after typing confirm)
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (etConfirmPassword.length() > 0) {
                    // Trigger re-check in confirm field
                    etConfirmPassword.setText(etConfirmPassword.getText());
                }
            }
        });

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean hasError = false;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Full name is required");
            hasError = true;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            hasError = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            if (mismatchToast != null) mismatchToast.cancel();
            mismatchToast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT);
            mismatchToast.show();
            hasError = true;
        }

        if (hasError) return;

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mismatchToast != null) {
            mismatchToast.cancel();
        }
        super.onDestroy();
    }
}