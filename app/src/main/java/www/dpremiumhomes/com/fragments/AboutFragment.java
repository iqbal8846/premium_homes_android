package www.dpremiumhomes.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.adapters.PartnersAdapter;

public class AboutFragment extends BaseFragment {

    private RecyclerView partnersList;
    private Button btnExplore;

    // Partner logos (drawable only – no model needed)
    private final int[] partnerImages = {
            R.drawable.ksrm_logo,
            R.drawable.holcim_logo,
            R.drawable.shah_cement_logo,
            R.drawable.crown_cement_logo,
            R.drawable.bsrm_logo,
            R.drawable.bashundhara_logo,
            R.drawable.gph_ispat_logo
    };

    public AboutFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Init views
        partnersList = view.findViewById(R.id.partnersList);
        btnExplore = view.findViewById(R.id.btn_explore);

        setupPartnersRecycler();
        setupExploreButton();
        finishLoading();

        return view;
    }

    private void setupPartnersRecycler() {
        partnersList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        partnersList.setAdapter(new PartnersAdapter(partnerImages));
        partnersList.setNestedScrollingEnabled(false); // important for ScrollView
    }

    private void setupExploreButton() {
        btnExplore.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProjectsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
