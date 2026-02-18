package www.dpremiumhomes.com.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

import www.dpremiumhomes.com.FlatDetailsActivity;
import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.PropertyViewActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.helpers.SessionManager;

public class MyFlatFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;


    private TextView viewDetails, tvFlatNo, tvFlatSize, tvPrice;
    private String propertyId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_flat, container, false);

        tvFlatNo = view.findViewById(R.id.tvFlatNo);
        tvFlatSize = view.findViewById(R.id.tvFlatSize);
        tvPrice = view.findViewById(R.id.tvPrice);
        viewDetails = view.findViewById(R.id.viewDetails);

        viewDetails.setOnClickListener(v -> {
            // Handle view details button click
            startActivity(new Intent(requireContext(), FlatDetailsActivity.class));
        });


        loadFlatData();

        finishLoading();

        return view;
    }

    private void loadFlatData() {
        SessionManager sessionManager = new SessionManager(requireContext());

        JSONObject flatDetails = sessionManager.getFlatDetails();

        if (flatDetails.length() == 0) {
            tvFlatNo.setText("No flat found");
            return;
        }

        try {
            // Because flatDetails keys are dynamic (15, 20, etc)
            Iterator<String> keys = flatDetails.keys();

            if (keys.hasNext()) {
                String propertyId = keys.next(); // "15"

                JSONObject flat = flatDetails.getJSONObject(propertyId);

                String flatNo = flat.optString("flatNo", "--");
                String flatSize = flat.optString("flatSize", "--");
                String price = flat.optString("price", "--");
                String location = flat.optString("location", "--");
                String name = flat.optString("name", "--");
                String image = flat.optString("image", "--");




                tvFlatNo.setText("Flat No: " + flatNo);
                tvFlatSize.setText("Size: " + flatSize);
                tvPrice.setText("৳ " + price);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Loader helpers
    private void startLoading() {
        loadingTasks++;
        loaderHidden = false;
        if (isAdded()) {
            ((MainActivity) requireActivity()).showLoader();
        }
    }

    private void finishLoading() {
        loadingTasks--;
        if (loadingTasks <= 0 && !loaderHidden) {
            loaderHidden = true;
            if (isAdded()) {
                ((MainActivity) requireActivity()).hideLoader();
            }
        }
    }

    private void failLoading() {
        finishLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
    }
}
