package www.dpremiumhomes.com.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.adapters.DashboardAdapter;
import www.dpremiumhomes.com.helpers.SessionManager;
import www.dpremiumhomes.com.models.CommunityProjectItem;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private static final String API_URL =
            "https://premium-api.dvalleybd.com/projects.php?action=get-all-projects";

    private RecyclerView recyclerViewProjects;
    private DashboardAdapter projectAdapter;
    private final List<CommunityProjectItem> projectList = new ArrayList<>();
    private RequestQueue requestQueue;

    // Stats
    private TextView tvTotalPrice, tvPaymentReceived, tvDuePayment;

    // Loader
    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    public DashboardFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewProjects = view.findViewById(R.id.recyclerView_projects);
        MaterialButton btnViewAllHeader = view.findViewById(R.id.btnViewAllHeader);
        MaterialButton btnViewAllProjects = view.findViewById(R.id.btnViewAllProjects);

        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvPaymentReceived = view.findViewById(R.id.tv_payment_received);
        tvDuePayment = view.findViewById(R.id.tv_due_payment);

        // Open Projects Fragment
        View.OnClickListener openProjectsListener = v -> openProjectsFragment();
        btnViewAllHeader.setOnClickListener(openProjectsListener);
        btnViewAllProjects.setOnClickListener(openProjectsListener);

        // RecyclerView
        recyclerViewProjects.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        projectAdapter = new DashboardAdapter(
                requireContext(),
                projectList,
                item -> {
                    // Handle dashboard project click
                    // Example: open PropertyViewActivity
                    Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                }
        );

        recyclerViewProjects.setAdapter(projectAdapter);

        loadUserDataFromSession();
        fetchProjects();
    }

    private void loadUserDataFromSession() {
        SessionManager session = new SessionManager(requireContext());
        if (!session.isLoggedIn()) return;

        try {
            JSONObject flatDetails = session.getFlatDetails();
            Set<String> savedProperties = session.getSavedProperties();

            if (!savedProperties.isEmpty()) {
                Iterator<String> iterator = savedProperties.iterator();
                String firstFlatId = iterator.next();

                if (flatDetails.has(firstFlatId)) {
                    JSONObject property = flatDetails.getJSONObject(firstFlatId);

                    tvTotalPrice.setText(property.optString("price", "৳0"));
                    tvPaymentReceived.setText(property.optString("partial_payment", "৳0"));
                    tvDuePayment.setText(property.optString("due_payment", "৳0"));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Session parse error", e);
        }
    }

    private void fetchProjects() {
        startLoading();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    try {
                        if (response.optBoolean("success")) {

                            projectList.clear();
                            JSONArray allProperties = response.optJSONArray("allProperties");

                            if (allProperties != null) {
                                for (int i = 0; i < allProperties.length(); i++) {
                                    JSONObject p = allProperties.getJSONObject(i);

                                    CommunityProjectItem item =
                                            new CommunityProjectItem(
                                                    String.valueOf(p.optInt("id")),
                                                    p.optString("image"),
                                                    p.optString("name"),
                                                    p.optString("location"),
                                                    p.optString("types"),
                                                    !"sold".equalsIgnoreCase(p.optString("tag")),
                                                    p.optString("priceRange", "Contact for price"),
                                                    p.optString("community", "")
                                            );

                                    projectList.add(item);
                                }
                            }

                            projectAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Failed to load projects", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON error", e);
                    } finally {
                        finishLoading();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error", error);
                    Toast.makeText(requireContext(),
                            "Server connection failed", Toast.LENGTH_SHORT).show();
                    finishLoading();
                }
        );

        requestQueue.add(request);
    }

    private void openProjectsFragment() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ProjectsFragment())
                .addToBackStack(null)
                .commit();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
    }
}
