package com.premium_homes.tech.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import com.premium_homes.tech.MainActivity;
import com.premium_homes.tech.R;
import com.premium_homes.tech.helpers.SessionManager;

public class AuctionFragment extends Fragment {

    private ImageView ivPropertyImage;
    private TextView tvProjectName, tvArea, tvPrice;
    private MaterialButton btnAuction;

    private SessionManager sessionManager;

    public AuctionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_auction, container, false);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        loadDataFromSession();

        return view;
    }

    private void initViews(View view) {

        ivPropertyImage = view.findViewById(R.id.iv_property_image);
        tvProjectName = view.findViewById(R.id.tv_project_name);
        tvArea = view.findViewById(R.id.tv_area);
        tvPrice = view.findViewById(R.id.tv_price);
        btnAuction = view.findViewById(R.id.btn_auction);
    }

    private void loadDataFromSession() {

        if (!sessionManager.isLoggedIn()) return;

        try {
            startLoading();

            // ✅ get first flat
            JSONObject flat = sessionManager.getFirstFlat();

            if (flat == null) {
                finishLoading();
                return;
            }

            String flatNo = flat.optString("flatNo", "N/A");
            String flatSize = flat.optString("flatSize", "N/A");
            String price = flat.optString("price", "0");
            String projectName = flat.optString("projectName", "The Premium Green Valley");

            // ✅ Bind UI
            tvProjectName.setText(projectName);
            tvArea.setText(flatSize);
            tvPrice.setText("৳ " + price);

            btnAuction.setOnClickListener(v -> {
                // TODO → Auction / Sell flow
                Toast.makeText(getContext(), "Upcoming feature", Toast.LENGTH_SHORT).show();
            });

            finishLoading();

        } catch (Exception e) {
            e.printStackTrace();
            finishLoading();
        }
    }

    // ---------------- LOADER ----------------

    private void startLoading() {
        if (isAdded()) {
            ((MainActivity) requireActivity()).showLoader();
        }
    }

    private void finishLoading() {
        if (isAdded()) {
            ((MainActivity) requireActivity()).hideLoader();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
    }
}