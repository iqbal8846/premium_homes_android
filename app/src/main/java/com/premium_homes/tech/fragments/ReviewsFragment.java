package com.premium_homes.tech.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.premium_homes.tech.adapters.ReviewsAdapter;
import com.premium_homes.tech.models.ReviewModel;

public class ReviewsFragment extends Fragment {

    private static final String TAG = "ReviewsFragment";
    private static final String REVIEWS_API_URL = "https://premium-api.dvalleybd.com/reviews.php?action=get-all-reviews";

    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private ReviewsAdapter adapter;
    private List<ReviewModel> reviewList = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        startLoading(); // start loader

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.reviewList);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with empty list
        adapter = new ReviewsAdapter(getViewLifecycleOwner(), reviewList);
        recyclerView.setAdapter(adapter);

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(requireContext());

        // Fetch reviews from API
        fetchReviews();

        return view;
    }

    private void fetchReviews() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                REVIEWS_API_URL,
                null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        if (success) {
                            JSONArray reviewsArray = response.getJSONArray("reviews");
                            parseReviews(reviewsArray);
                        } else {
                            showError(message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        showError("Error parsing reviews data");
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    showError("Failed to load reviews. Please check your connection.");
                    finishLoading();
                });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void parseReviews(JSONArray reviewsArray) {
        reviewList.clear();

        try {
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject reviewObj = reviewsArray.getJSONObject(i);

                // Extract video ID from YouTube URL
                String videoUrl = reviewObj.optString("videoUrl", "");
                String videoId = extractYouTubeVideoId(videoUrl);

                // Create ReviewModel object
                ReviewModel review = new ReviewModel(
                        videoId,
                        reviewObj.optString("review", ""),
                        reviewObj.optString("name", ""),
                        reviewObj.optString("role", ""),
                        reviewObj.optString("date", "")
                );

                reviewList.add(review);
            }

            // Update adapter
            adapter.notifyDataSetChanged();

            // Show empty state if no reviews
            if (reviewList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyStateText.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing review: " + e.getMessage());
            showError("Error parsing review data");
        }

        finishLoading();
    }

    /**
     * Extract YouTube video ID from various URL formats
     */
    private String extractYouTubeVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return "";
        }

        String videoId = "";

        // Pattern for: https://www.youtube.com/embed/VIDEO_ID
        if (videoUrl.contains("/embed/")) {
            int start = videoUrl.indexOf("/embed/") + 7;
            int end = videoUrl.indexOf("?", start);
            if (end == -1) end = videoUrl.length();
            videoId = videoUrl.substring(start, end);
        }
        // Pattern for: https://youtube.com/embed/VIDEO_ID
        else if (videoUrl.contains("youtube.tech/embed/")) {
            int start = videoUrl.indexOf("youtube.tech/embed/") + 18;
            int end = videoUrl.indexOf("?", start);
            if (end == -1) end = videoUrl.length();
            videoId = videoUrl.substring(start, end);
        }
        // Pattern for: https://www.youtube.com/watch?v=VIDEO_ID
        else if (videoUrl.contains("watch?v=")) {
            String[] parts = videoUrl.split("watch\\?v=");
            if (parts.length > 1) {
                videoId = parts[1];
                if (videoId.contains("&")) {
                    videoId = videoId.substring(0, videoId.indexOf("&"));
                }
            }
        }
        // Pattern for: https://youtu.be/VIDEO_ID
        else if (videoUrl.contains("youtu.be/")) {
            int start = videoUrl.indexOf("youtu.be/") + 9;
            int end = videoUrl.indexOf("?", start);
            if (end == -1) end = videoUrl.length();
            videoId = videoUrl.substring(start, end);
        }

        Log.d(TAG, "Extracted video ID: " + videoId + " from URL: " + videoUrl);
        return videoId;
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

        // Show empty state with error message
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateText.setText(message);
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
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
        if (isAdded()) {
            ((MainActivity) requireActivity()).hideLoader();
        }
    }
}