package com.premium_homes.tech.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.premium_homes.tech.MainActivity;
import com.premium_homes.tech.R;
import com.premium_homes.tech.adapters.AllProjectAdapter;
import com.premium_homes.tech.models.AllProjectModel;

public class ProjectsFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;
    private RecyclerView projectsView;
    private View contentLayout; // Contains header + RecyclerView

    private AllProjectAdapter adapter;
    private List<AllProjectModel> allProjectList;
    private RequestQueue requestQueue;

    private static final String API_URL =
            "https://premium-api.dvalleybd.com/projects.php?action=get-all-projects";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects, container, false);

        // Find views
        projectsView = root.findViewById(R.id.projectsView);
        contentLayout = root.findViewById(R.id.contentLayout);

        // Setup RecyclerView
        projectsView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allProjectList = new ArrayList<>();
        adapter = new AllProjectAdapter(requireContext(),allProjectList);
        projectsView.setAdapter(adapter);

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(requireContext());

        startLoading();
        // Start loading data
        loadAllProjects();

        return root;
    }

    private void loadAllProjects() {

        contentLayout.setVisibility(View.GONE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray propertiesArray = response.getJSONArray("allProperties");
                            allProjectList.clear();

                            for (int i = 0; i < propertiesArray.length(); i++) {
                                JSONObject property = propertiesArray.getJSONObject(i);

                                String id = property.getString("id");
                                String imageUrl = property.getString("image");
                                String title = property.getString("name");
                                String location = property.getString("location");
                                String bedrooms = property.getString("types");
                                String community = property.optString("community", "Premium Homes");
                                String tag = property.optString("tag", "");

                                AllProjectModel project = new AllProjectModel(
                                        id,
                                        imageUrl,
                                        title,
                                        location,
                                        bedrooms,
                                        community,
                                        tag
                                );

                                allProjectList.add(project);
                            }

                            adapter.notifyDataSetChanged();

                            // Hide loading, show content
                            contentLayout.setVisibility(View.VISIBLE);

                        } else {
                            String message = response.getString("message");
                            showErrorAndRevealContent(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorAndRevealContent("Failed to parse data");
                    }
                    finishLoading();
                },
                error -> {
                    error.printStackTrace();
                    showErrorAndRevealContent("Network error. Please check your connection.");
                    finishLoading();

                });

        requestQueue.add(request);
    }

    private void showErrorAndRevealContent(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        contentLayout.setVisibility(View.VISIBLE); // Show header even if empty
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