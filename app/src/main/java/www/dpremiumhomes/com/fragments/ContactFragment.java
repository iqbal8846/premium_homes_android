package www.dpremiumhomes.com.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;

public class ContactFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    private EditText nameEditText, phoneEditText, emailEditText, messageEditText;
    private CardView submitContactBtn;
    private ProgressBar progressBar;
    private TextView submitText;

    private RequestQueue requestQueue;
    private static final String TAG = "CONTACT_REQ";

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        messageEditText = view.findViewById(R.id.messageEditText);

        submitContactBtn = view.findViewById(R.id.submitContactBtn);
        progressBar = view.findViewById(R.id.submitProgress);
        submitText = view.findViewById(R.id.submitText);

        requestQueue = Volley.newRequestQueue(requireContext());

        submitContactBtn.setOnClickListener(v -> submitForm());

        finishLoading();

        return view;
    }

    private void submitForm() {

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

    private void sendContactToApi(String name, String phone, String email, String message) {

        setLoading(true);

        String url = "https://api.tphl-erp.dvalleybd.com/api/v1/contact/create";

        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("email", email);
            json.put("phone", phone);
            json.put("subject", "Property Inquiry");
            json.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,

                response -> {

                    setLoading(false);

                    try {

                        boolean success = response.getBoolean("success");
                        String msg = response.getString("message");

                        if (success) {

                            clearForm();
                            showSuccessDialog(msg);

                        } else {
                            showErrorDialog(msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorDialog("Response parsing error!");
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

    private void setLoading(boolean isLoading) {

        submitContactBtn.setEnabled(!isLoading);

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        submitText.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void clearForm() {
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        messageEditText.setText("");
    }

    private void showSuccessDialog(String message) {

        if (!isAdded()) return;

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_contact_success);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        TextView msg = dialog.findViewById(R.id.dialogMessage);
        TextView okBtn = dialog.findViewById(R.id.dialogOkBtn);

        msg.setText(message);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        // animation
        dialog.findViewById(R.id.dialogMessage)
                .startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_scale_in));

        dialog.show();
    }

    private void showErrorDialog(String message) {

        if (!isAdded()) return;

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_error);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        TextView msg = dialog.findViewById(R.id.errorMessage);
        TextView retry = dialog.findViewById(R.id.btnTryAgain);

        msg.setText(message);

        retry.setOnClickListener(v -> {
            dialog.dismiss();
            submitForm(); // 🔁 retry API call
        });

        dialog.show();
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




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) requestQueue.cancelAll(TAG);
    }
}