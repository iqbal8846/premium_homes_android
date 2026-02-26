package com.premium_homes.tech.fragments;

import android.os.Bundle;
import android.util.Log;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.premium_homes.tech.MainActivity;
import com.premium_homes.tech.R;
import com.premium_homes.tech.adapters.BlogAdapter;
import com.premium_homes.tech.models.BlogItem;

public class BlogsFragment extends Fragment {

    private RecyclerView blogsList;
    private BlogAdapter adapter;
    private final List<BlogItem> blogItems = new ArrayList<>();

    private RequestQueue requestQueue;

    // Loader handling
    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    private static final String TAG = "BlogsFragment";
    private static final String API_URL =
            "https://premium-api.dvalleybd.com/blogs.php?action=get-all-blogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blogs, container, false);

        blogsList = view.findViewById(R.id.blogsList);
        blogsList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        blogsList.setHasFixedSize(true);

        adapter = new BlogAdapter(requireContext(), blogItems);
        blogsList.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        loadBlogs(); // This will handle loader

        return view;
    }

    private void loadBlogs() {
        startLoading(); // start loader

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {

                            JSONArray blogs = response.getJSONArray("blogs");
                            blogItems.clear();

                            for (int i = 0; i < blogs.length(); i++) {
                                JSONObject obj = blogs.getJSONObject(i);

                                BlogItem blog = new BlogItem();
                                blog.setId(obj.optInt("id", i + 1));
                                blog.setTitle(obj.optString("title", "No Title"));
                                blog.setExcerpt(obj.optString("excerpt", ""));
                                blog.setImage(obj.optString("image", ""));
                                blog.setAuthor(obj.optString("author", "Admin"));
                                blog.setDate(obj.optString("date", ""));
                                blog.setComments(obj.optInt("comments", 0));
                                blog.setReadTime(obj.optString("readTime", ""));

                                blogItems.add(blog);
                            }

                            if (isAdded() && adapter != null) {
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.e(TAG, "API returned success=false");
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "No blogs found", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing blogs", e);
                        if (isAdded()) Toast.makeText(requireContext(), "Error processing blogs", Toast.LENGTH_SHORT).show();
                    } finally {
                        finishLoading(); // always finish loader
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error", error);
                    if (isAdded()) Toast.makeText(requireContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                    finishLoading(); // always finish loader
                }
        );

        requestQueue.add(request);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) requestQueue.cancelAll(TAG);
        if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
    }
}
