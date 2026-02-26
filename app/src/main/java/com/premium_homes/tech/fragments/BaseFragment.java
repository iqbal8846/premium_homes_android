package com.premium_homes.tech.fragments;

import androidx.fragment.app.Fragment;
import com.premium_homes.tech.MainActivity;

public abstract class BaseFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    protected void startLoading() {
        loadingTasks++;
        loaderHidden = false;
        if (isAdded()) {
            ((MainActivity) requireActivity()).showLoader();
        }
    }

    protected void finishLoading() {
        loadingTasks--;
        if (loadingTasks <= 0 && !loaderHidden) {
            loaderHidden = true;
            if (isAdded()) {
                ((MainActivity) requireActivity()).hideLoader();
            }
        }
    }

    protected void failLoading() {
        finishLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Safety: hide loader if fragment is destroyed
        if (isAdded()) {
            ((MainActivity) requireActivity()).hideLoader();
        }
    }
}
