package www.dpremiumhomes.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.adapters.ReviewsAdapter;
import www.dpremiumhomes.com.models.ReviewModel;

public class ReviewsFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        startLoading(); // start loader

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.reviewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ReviewModel> list = new ArrayList<>();

        // Add sample reviews
        list.add(new ReviewModel(
                "qM5_UKR-vzk",
                "ফ্ল্যাট কেনার আগে ভিডিও টা একবার দেখে নিতে পারেন । RJ Kebria I SOCA I",
                "RJ Kibria",
                "Property Investor",
                "March 2024"
        ));

        list.add(new ReviewModel(
                "zxcvNVqIl-0",
                "Customer review",
                "Karim Rahman",
                "Property Investor",
                "February 2024"
        ));

        ReviewsAdapter adapter = new ReviewsAdapter(getViewLifecycleOwner(), list);
        recyclerView.setAdapter(adapter);

        finishLoading(); // finish loader

        return view;
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
