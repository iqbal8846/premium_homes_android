package www.dpremiumhomes.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.Set;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.helpers.SessionManager;

public class AuctionFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    private ImageView ivPropertyImage;
    private TextView tvProjectName, tvArea, tvPrice;
    private MaterialButton btnAuction;

    public
    AuctionFragment() {
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

        initViews(view);
        loadDataFromSession();

        finishLoading();
        return view;
    }

    private void initViews(View view) {
        ivPropertyImage = view.findViewById(R.id.iv_property_image);
        tvProjectName = view.findViewById(R.id.tv_project_name);
        tvArea = view.findViewById(R.id.tv_area);   // create ID if missing
        tvPrice = view.findViewById(R.id.tv_price);
        btnAuction = view.findViewById(R.id.btn_auction);
    }

    private void loadDataFromSession() {
        SessionManager session = new SessionManager(requireContext());

        if (!session.isLoggedIn()) return;

        try {
            Set<String> savedProperties = session.getSavedProperties();
            JSONObject flatDetails = session.getFlatDetails();

            if (savedProperties.isEmpty()) return;

            // Take first saved property
            String propertyId = savedProperties.iterator().next();

            if (!flatDetails.has(propertyId)) return;

            JSONObject flat = flatDetails.getJSONObject(propertyId);

            String flatNo = flat.optString("flatNo", "N/A");
            String flatSize = flat.optString("flatSize", "N/A");
            String price = flat.optString("price", "0");

            // Bind UI
            tvProjectName.setText("The Premium Green Valley");
            tvArea.setText(flatSize);
            tvPrice.setText("৳ " + price);

            btnAuction.setOnClickListener(v -> {
                // Later: open auction / sell flow
            });

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
