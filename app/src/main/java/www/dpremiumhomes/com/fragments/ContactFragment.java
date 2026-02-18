package www.dpremiumhomes.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;

public class ContactFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    private EditText nameEditText, phoneEditText, emailEditText, messageEditText;
    private CardView submitContactBtn;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // Initialize views
        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        messageEditText = view.findViewById(R.id.messageEditText);
        submitContactBtn = view.findViewById(R.id.submitContactBtn);

        // Submit button click
        submitContactBtn.setOnClickListener(v -> submitForm());
        finishLoading();

        return view;
    }

    private void submitForm() {
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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        // Success
        Toast.makeText(getContext(), "Request submitted successfully!\nWe'll contact you soon.", Toast.LENGTH_LONG).show();

        // Clear form
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        messageEditText.setText("");
    }


    // Loader helpers
    private void startLoading() {
        loadingTasks++;
        loaderHidden = false;
        if (isAdded()) ((MainActivity) requireActivity()).showLoader();
    }

    private void finishLoading() {
        loadingTasks--;
        if (loadingTasks <= 0 && !loaderHidden) {
            loaderHidden = true;
            if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
        }
    }

    private void failLoading() {
        finishLoading();
    }

    /**
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) requestQueue.cancelAll(TAG);
        if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
    }**/
}