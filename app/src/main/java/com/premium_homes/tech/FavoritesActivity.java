package com.premium_homes.tech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.premium_homes.tech.adapters.FavoritesAdapter;
import com.premium_homes.tech.helpers.SessionManager;
import com.premium_homes.tech.models.FavoriteModel;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView btnClearAll;
    private ImageView btnGoBack;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private List<FavoriteModel> list = new ArrayList<>();
    private FavoritesAdapter adapter;
    private SessionManager session;

    private static final String BASE = "https://premium-api.dvalleybd.com/get-favourites.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupListeners();
        loadFavorites();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerFavorites);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnClearAll = findViewById(R.id.btnClearAll);
        btnGoBack = findViewById(R.id.btnGoBack);
        progressBar = findViewById(R.id.progressBar);

        session = new SessionManager(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritesAdapter(this, list, this::removeFavorite);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnClearAll.setOnClickListener(v -> clearAllFavorites());
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // ===============================
    // LOAD FAVORITES
    // ===============================
    private void loadFavorites() {
        showLoading(true);

        String url = BASE + "?action=get-favourites&userId=" + session.getUserId();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    showLoading(false);
                    Log.d("FAV_LOAD", response);

                    try {
                        JSONObject json = new JSONObject(response);

                        if (!json.getBoolean("success")) {
                            Toast.makeText(this,
                                    json.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray array = json.getJSONArray("favouriteProperties");
                        list.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            list.add(new FavoriteModel(
                                    obj.getString("id"),
                                    obj.getString("name"),
                                    obj.getString("priceRange"),
                                    obj.getString("location"),
                                    obj.getString("image")
                            ));
                        }

                        adapter.notifyDataSetChanged();
                        updateEmptyState();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    showLoading(false);
                    Log.e("FAV_LOAD_ERROR", error.toString());
                    Toast.makeText(this, "Load Failed", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateEmptyState() {
        tvEmpty.setVisibility(list.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    // ===============================
    // REMOVE SINGLE FAVORITE
    // ===============================
    private void removeFavorite(String propertyId) {
        showLoading(true);

        // Send parameters in URL for DELETE request
        String url = BASE + "?action=remove-favourite&userId=" + session.getUserId()
                + "&propertyId=" + propertyId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("REMOVE_RESPONSE", response);
                    Toast.makeText(this, "Favorite removed successfully", Toast.LENGTH_SHORT).show();
                    loadFavorites(); // Reload the list
                },
                error -> {
                    showLoading(false);
                    Log.e("REMOVE_ERROR", error.toString());
                    Toast.makeText(this, "Remove Failed. Please try again.", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ===============================
    // CLEAR ALL FAVORITES
    // ===============================
    private void clearAllFavorites() {
        if (list.isEmpty()) {
            Toast.makeText(this, "No favorites to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Send parameters in URL for DELETE request
        String url = BASE + "?action=clear-favourites&userId=" + session.getUserId();

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("CLEAR_RESPONSE", response);
                    Toast.makeText(this, "All Favorites Removed", Toast.LENGTH_SHORT).show();
                    loadFavorites(); // Reload the list
                },
                error -> {
                    showLoading(false);
                    Log.e("CLEAR_ERROR", error.toString());
                    Toast.makeText(this, "Clear Failed. Please try again.", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}