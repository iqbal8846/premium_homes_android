package com.premium_homes.tech.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.premium_homes.tech.MainActivity;
import com.premium_homes.tech.PropertyViewActivity;
import com.premium_homes.tech.R;
import com.premium_homes.tech.adapters.SearchAdapter;
import com.premium_homes.tech.models.SearchModel;

public class SearchFragment extends Fragment {

    // UI
    private LinearLayout layoutCommunitiesHeader, layoutCommunitiesOptions;
    private LinearLayout layoutPriceRangeHeader, layoutPriceRangeOptions;
    private ImageView ivCommunitiesArrow, ivPriceArrow;
    private TextView tvResetAll, tvProjectsCount, SearchText;
    private CardView btnAdvancedSearch, searchTextCard;
    private RecyclerView recyclerView;

    // State
    private boolean isCommunitiesExpanded = false;
    private boolean isPriceExpanded = false;
    private int loadingTasks = 0;
    private boolean loaderHidden = false;

    // Data
    public final ArrayList<SearchModel> fullList = new ArrayList<>();
    private final ArrayList<SearchModel> filteredList = new ArrayList<>();

    // Fragment checkbox filters
    private final Set<String> selectedCommunities = new HashSet<>();
    private final Set<String> selectedPriceRanges = new HashSet<>();

    private SearchAdapter adapter;

    // Advanced Filters (shared with BottomSheet)
    public final Set<String> selectedBaths = new HashSet<>();
    public final Set<String> selectedBeds = new HashSet<>();
    public final Set<String> selectedFlatSizes = new HashSet<>();
    public final Set<String> selectedBalconies = new HashSet<>();
    public final Set<String> selectedLocations = new HashSet<>();

    private static final String API_URL =
            "https://premium-api.dvalleybd.com/projects.php?action=get-all-projects";
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // সার্চ কোয়েরি রিসিভ করা
        if (getArguments() != null) {
            currentSearchQuery = getArguments().getString("search_query", "").trim();
            Log.d("SearchFragment", "Received search query: " + currentSearchQuery);
        }

        initViews(view);
        setupRecycler();
        setupFragmentCheckboxFilters(view);
        setupClicks();

        startLoading();
        loadProjects();

        // যদি সার্চ কোয়েরি থাকে তাহলে উপরে দেখাও
        if (!currentSearchQuery.isEmpty()) {
            searchTextCard.setVisibility(VISIBLE);
            SearchText.setText("Search results for: \"" + currentSearchQuery + "\"");
        } else {
            SearchText.setText("All Properties");
            searchTextCard.setVisibility(GONE);
        }

        return view;
    }

    private void initViews(View v) {
        layoutCommunitiesHeader = v.findViewById(R.id.layout_communities_header);
        layoutCommunitiesOptions = v.findViewById(R.id.layout_communities_options);
        ivCommunitiesArrow = v.findViewById(R.id.iv_communities_arrow);

        layoutPriceRangeHeader = v.findViewById(R.id.layout_price_range_header);
        layoutPriceRangeOptions = v.findViewById(R.id.layout_price_range_options);
        ivPriceArrow = v.findViewById(R.id.iv_price_arrow);

        tvResetAll = v.findViewById(R.id.tv_reset_all);
        tvProjectsCount = v.findViewById(R.id.tv_projects_count);
        btnAdvancedSearch = v.findViewById(R.id.custom_advanced_search_btn);
        recyclerView = v.findViewById(R.id.recycler_projects);
        SearchText = v.findViewById(R.id.SearchText);
        searchTextCard = v.findViewById(R.id.searchTextCard);
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SearchAdapter(requireContext(), filteredList);

        adapter.setOnItemClickListener(model -> {
            Intent intent = new Intent(requireActivity(), PropertyViewActivity.class);
            intent.putExtra("propertyId", String.valueOf(model.getId()));
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupClicks() {
        layoutCommunitiesHeader.setOnClickListener(v -> {
            isCommunitiesExpanded = !isCommunitiesExpanded;
            toggle(layoutCommunitiesOptions, ivCommunitiesArrow, isCommunitiesExpanded);
        });

        layoutPriceRangeHeader.setOnClickListener(v -> {
            isPriceExpanded = !isPriceExpanded;
            toggle(layoutPriceRangeOptions, ivPriceArrow, isPriceExpanded);
        });

        tvResetAll.setOnClickListener(v -> resetAll());

        btnAdvancedSearch.setOnClickListener(v -> {
            AdvancedSearchBottomSheet sheet = new AdvancedSearchBottomSheet();
            sheet.show(getChildFragmentManager(), "AdvancedSearch");
        });
    }

    private void loadProjects() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("allProperties");
                        fullList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);

                            // Parse filters object with the correct property names from the actual data
                            JSONObject filters = o.optJSONObject("filters");
                            List<String> bathrooms = new ArrayList<>();
                            List<String> flatSizes = new ArrayList<>();
                            List<String> balconies = new ArrayList<>();
                            List<String> filterLocations = new ArrayList<>();

                            if (filters != null) {
                                // FIX: Parse bathroomCounts instead of bathrooms
                                if (filters.has("bathroomCounts")) {
                                    try {
                                        JSONArray bathArray = filters.optJSONArray("bathroomCounts");
                                        if (bathArray != null) {
                                            for (int j = 0; j < bathArray.length(); j++) {
                                                String bathValue = bathArray.optString(j, "");
                                                if (!bathValue.isEmpty()) {
                                                    bathrooms.add(bathValue);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("SearchFragment", "Error parsing bathroomCounts: " + e.getMessage());
                                    }
                                }

                                // FIX: Parse availableSizes instead of flatSizes
                                if (filters.has("availableSizes")) {
                                    try {
                                        JSONArray sizeArray = filters.optJSONArray("availableSizes");
                                        if (sizeArray != null) {
                                            for (int j = 0; j < sizeArray.length(); j++) {
                                                String sizeValue = sizeArray.optString(j, "");
                                                if (!sizeValue.isEmpty()) {
                                                    flatSizes.add(sizeValue);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("SearchFragment", "Error parsing availableSizes: " + e.getMessage());
                                    }
                                }

                                // FIX: Parse balconyCounts instead of balconies
                                if (filters.has("balconyCounts")) {
                                    try {
                                        JSONArray balconyArray = filters.optJSONArray("balconyCounts");
                                        if (balconyArray != null) {
                                            for (int j = 0; j < balconyArray.length(); j++) {
                                                String balconyValue = balconyArray.optString(j, "");
                                                if (!balconyValue.isEmpty()) {
                                                    balconies.add(balconyValue);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("SearchFragment", "Error parsing balconyCounts: " + e.getMessage());
                                    }
                                }

                                // FIX: Parse locations (this one is correct in the data)
                                if (filters.has("locations")) {
                                    try {
                                        JSONArray locArray = filters.optJSONArray("locations");
                                        if (locArray != null) {
                                            for (int j = 0; j < locArray.length(); j++) {
                                                String locValue = locArray.optString(j, "");
                                                if (!locValue.isEmpty()) {
                                                    filterLocations.add(locValue);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("SearchFragment", "Error parsing locations: " + e.getMessage());
                                    }
                                }
                            }

                            // Create SearchModel with required fields
                            SearchModel model = new SearchModel(
                                    o.getInt("id"),
                                    o.getString("name"),
                                    o.getString("location"),
                                    o.getString("types"),
                                    o.getString("image"),
                                    o.optString("tag", ""),
                                    o.getString("community"),
                                    o.getString("priceRange"),
                                    bathrooms,
                                    flatSizes,
                                    balconies,
                                    filterLocations,
                                    o.optString("fullLocation", "")
                            );

                            fullList.add(model);
                        }

                        Log.d("SearchFragment", "Loaded " + fullList.size() + " projects");
                        applyCurrentFilters();

                    } catch (Exception e) {
                        Log.e("SearchFragment", "Error parsing JSON: " + e.getMessage());
                        e.printStackTrace();
                        finishLoading();
                    }
                    finishLoading();
                },
                error -> {
                    Log.e("SearchFragment", "Volley error: " + error.getMessage());
                    error.printStackTrace();
                    finishLoading();
                });

        // Add retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    // Apply filters based on current selected sets
    private void applyCurrentFilters() {
        filteredList.clear();

        for (SearchModel m : fullList) {
            if (matchesCurrentFilters(m)) {
                filteredList.add(m);
            }
        }

        Log.d("SearchFragment", "Full list: " + fullList.size() +
                ", Filtered: " + filteredList.size() +
                ", Search query: " + currentSearchQuery);

        adapter.updateList(filteredList);
        updateCount();
    }

    private boolean matchesCurrentFilters(SearchModel m) {
        // === সার্চ টেক্সট ফিল্টার ===
        if (!currentSearchQuery.isEmpty()) {
            String query = currentSearchQuery.toLowerCase();
            boolean matchesSearch = m.getName().toLowerCase().contains(query) ||
                    m.getLocation().toLowerCase().contains(query) ||
                    m.getTypes().toLowerCase().contains(query) ||
                    m.getCommunity().toLowerCase().contains(query) ||
                    m.getFullLocation().toLowerCase().contains(query);

            if (!matchesSearch) return false;
        }

        // Advanced search filters
        if (!selectedBaths.isEmpty()) {
            boolean bathMatch = false;
            for (String bath : selectedBaths) {
                String bathNum = bath.replaceAll("[^0-9]", "");
                if (m.getBathrooms().contains(bathNum)) {
                    bathMatch = true;
                    break;
                }
            }
            if (!bathMatch) return false;
        }

        if (!selectedBeds.isEmpty()) {
            boolean bedMatch = false;
            for (String bed : selectedBeds) {
                String bedNum = bed.replaceAll("[^0-9]", "");
                if (containsNumber(m.getFlatSizes(), bedNum)) {
                    bedMatch = true;
                    break;
                }
            }
            if (!bedMatch) return false;
        }

        if (!selectedFlatSizes.isEmpty()) {
            boolean sizeMatch = false;
            for (String size : selectedFlatSizes) {
                if (containsSize(m.getFlatSizes(), size)) {
                    sizeMatch = true;
                    break;
                }
            }
            if (!sizeMatch) return false;
        }

        if (!selectedBalconies.isEmpty()) {
            boolean balconyMatch = false;
            for (String balcony : selectedBalconies) {
                String balconyNum = balcony.replaceAll("[^0-9]", "");
                if (m.getBalconies().contains(balconyNum)) {
                    balconyMatch = true;
                    break;
                }
            }
            if (!balconyMatch) return false;
        }

        if (!selectedLocations.isEmpty()) {
            boolean locationMatch = false;
            for (String location : selectedLocations) {
                if (containsLocation(m, location)) {
                    locationMatch = true;
                    break;
                }
            }
            if (!locationMatch) return false;
        }

        // Fragment filters
        if (!selectedCommunities.isEmpty() &&
                !selectedCommunities.contains(m.getCommunity())) {
            return false;
        }

        if (!selectedPriceRanges.isEmpty() &&
                !selectedPriceRanges.contains(m.getPriceRange())) {
            return false;
        }

        return true;
    }

    private boolean containsNumber(List<String> list, String number) {
        if (list == null) return false;
        for (String item : list) {
            if (item.contains(number)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSize(List<String> sizes, String size) {
        if (sizes == null) return false;
        for (String s : sizes) {
            if (s.equalsIgnoreCase(size) || s.contains(size)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLocation(SearchModel model, String location) {
        // Check in filterLocations
        if (model.getFilterLocations() != null) {
            for (String loc : model.getFilterLocations()) {
                if (loc.toLowerCase().contains(location.toLowerCase())) {
                    return true;
                }
            }
        }

        // Check in location and fullLocation
        if (model.getLocation().toLowerCase().contains(location.toLowerCase())) {
            return true;
        }

        if (model.getFullLocation().toLowerCase().contains(location.toLowerCase())) {
            return true;
        }

        return false;
    }

    private void setupFragmentCheckboxFilters(View v) {
        // Communities
        int[] communityIds = {
                R.id.cb_smart_city,
                R.id.cb_ashulia,
                R.id.cb_royal_city,
                R.id.cb_bashundhara
        };

        String[] communityValues = {
                "The Premium Smart City",
                "Ashulia Model Town",
                "The Premium Royal City",
                "Bashundhara Residential Area"
        };

        for (int i = 0; i < communityIds.length; i++) {
            MaterialCheckBox cb = v.findViewById(communityIds[i]);
            final String value = communityValues[i];
            cb.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) selectedCommunities.add(value);
                else selectedCommunities.remove(value);
                applyCurrentFilters();
            });
        }

        // Price Range
        int[] priceIds = {
                R.id.cb_under_50,
                R.id.cb_50_1cr,
                R.id.cb_1_2cr,
                R.id.cb_2_5cr,
                R.id.cb_5plus
        };

        String[] priceValues = {
                "Under 50 Lac BDT",
                "50 Lac - 1 Crore BDT",
                "1 Crore - 2 Crore BDT",
                "2 Crore - 5 Crore BDT",
                "Above 5 Crore BDT"
        };

        for (int i = 0; i < priceIds.length; i++) {
            MaterialCheckBox cb = v.findViewById(priceIds[i]);
            final String value = priceValues[i];
            cb.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) selectedPriceRanges.add(value);
                else selectedPriceRanges.remove(value);
                applyCurrentFilters();
            });
        }
    }

    private void resetAll() {
        // Clear all filter sets
        selectedBaths.clear();
        selectedBeds.clear();
        selectedFlatSizes.clear();
        selectedBalconies.clear();
        selectedLocations.clear();
        selectedCommunities.clear();
        selectedPriceRanges.clear();
        currentSearchQuery = "";
        searchTextCard.setVisibility(GONE);
        SearchText.setText("All Properties");

        // Uncheck fragment checkboxes
        uncheckAll(layoutCommunitiesOptions);
        uncheckAll(layoutPriceRangeOptions);

        applyCurrentFilters();
    }

    private void uncheckAll(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof MaterialCheckBox) {
                ((MaterialCheckBox) v).setChecked(false);
            }
        }
    }

    private void updateCount() {
        tvProjectsCount.setText(String.valueOf(filteredList.size()));
    }

    private void toggle(LinearLayout layout, ImageView arrow, boolean open) {
        layout.setVisibility(open ? VISIBLE : GONE);
        arrow.animate().rotation(open ? 180f : 0f).setDuration(200).start();
    }

    // =====================================================
    // ADVANCED SEARCH BOTTOM SHEET
    // =====================================================

    public static class AdvancedSearchBottomSheet extends BottomSheetDialogFragment {

        private ChipGroup chipBath, chipBed, chipFlatSize, chipBalcony, chipLocation;
        private TextView tvBathCount, tvBedCount, tvFlatSizeCount, tvBalconyCount, tvLocationCount;
        private TextView tvShowingCount;
        private EditText searchEditText;
        private String searchQuery = "";

        // Live preview sets
        private final Set<String> previewBaths = new HashSet<>();
        private final Set<String> previewBeds = new HashSet<>();
        private final Set<String> previewFlatSizes = new HashSet<>();
        private final Set<String> previewBalconies = new HashSet<>();
        private final Set<String> previewLocations = new HashSet<>();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.dialog_search, container, false);

            chipBath = v.findViewById(R.id.chip_bathroom);
            chipBed = v.findViewById(R.id.chip_bed);
            chipFlatSize = v.findViewById(R.id.chip_flat_size);
            chipBalcony = v.findViewById(R.id.chip_balcony);
            chipLocation = v.findViewById(R.id.chip_location);

            tvBathCount = v.findViewById(R.id.tv_bathroom_count);
            tvBedCount = v.findViewById(R.id.tv_bed_count);
            tvFlatSizeCount = v.findViewById(R.id.tv_flat_size_count);
            tvBalconyCount = v.findViewById(R.id.tv_balcony_count);
            tvLocationCount = v.findViewById(R.id.tv_location_count);

            tvShowingCount = v.findViewById(R.id.tv_showing_count);
            searchEditText = v.findViewById(R.id.searchEditText);

            CardView closeBtn = v.findViewById(R.id.iv_close);
            TextView clearBtn = v.findViewById(R.id.tv_clear_all);
            TextView applyBtn = v.findViewById(R.id.btn_apply);

            setupChips();
            setupSearchEditText();
            setupListeners();

            closeBtn.setOnClickListener(view -> dismissAllowingStateLoss());

            clearBtn.setOnClickListener(view -> {
                clearAllChips();
                searchEditText.setText("");
                searchQuery = "";
                updatePreviewAndBadges();
            });

            applyBtn.setOnClickListener(view -> {
                SearchFragment parent = (SearchFragment) getParentFragment();
                if (parent != null) {
                    parent.selectedBaths.clear();
                    parent.selectedBeds.clear();
                    parent.selectedFlatSizes.clear();
                    parent.selectedBalconies.clear();
                    parent.selectedLocations.clear();

                    parent.selectedBaths.addAll(previewBaths);
                    parent.selectedBeds.addAll(previewBeds);
                    parent.selectedFlatSizes.addAll(previewFlatSizes);
                    parent.selectedBalconies.addAll(previewBalconies);
                    parent.selectedLocations.addAll(previewLocations);

                    // Apply search query if exists
                    if (!searchQuery.isEmpty()) {
                        parent.currentSearchQuery = searchQuery;
                        parent.SearchText.setText("Search results for: \"" + searchQuery + "\"");
                        parent.searchTextCard.setVisibility(VISIBLE);
                    } else {
                        parent.currentSearchQuery = "";
                        parent.searchTextCard.setVisibility(GONE);
                        parent.SearchText.setText("All Properties");
                    }

                    parent.applyCurrentFilters();
                }
                dismissAllowingStateLoss();
            });

            // Restore current applied filters into chips
            restoreCurrentFilters();

            updatePreviewAndBadges();

            return v;
        }

        @Override
        public void onStart() {
            super.onStart();

            Dialog dialog = getDialog();
            if (dialog != null) {
                View bottomSheet =
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                if (bottomSheet != null) {
                    BottomSheetBehavior<View> behavior =
                            BottomSheetBehavior.from(bottomSheet);

                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    behavior.setSkipCollapsed(true);
                    behavior.setDraggable(true);
                }
            }
        }

        private void setupSearchEditText() {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuery = s.toString().trim();
                    updatePreviewAndBadges();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        private void restoreCurrentFilters() {
            SearchFragment parent = (SearchFragment) getParentFragment();
            if (parent == null) return;

            selectChips(chipBath, parent.selectedBaths);
            selectChips(chipBed, parent.selectedBeds);
            selectChips(chipFlatSize, parent.selectedFlatSizes);
            selectChips(chipBalcony, parent.selectedBalconies);
            selectChips(chipLocation, parent.selectedLocations);

            // Sync preview sets
            previewBaths.addAll(parent.selectedBaths);
            previewBeds.addAll(parent.selectedBeds);
            previewFlatSizes.addAll(parent.selectedFlatSizes);
            previewBalconies.addAll(parent.selectedBalconies);
            previewLocations.addAll(parent.selectedLocations);

            // Set search query if exists
            if (!parent.currentSearchQuery.isEmpty()) {
                searchEditText.setText(parent.currentSearchQuery);
                searchQuery = parent.currentSearchQuery;
            }
        }

        private void selectChips(ChipGroup group, Set<String> selected) {
            for (int i = 0; i < group.getChildCount(); i++) {
                Chip chip = (Chip) group.getChildAt(i);
                String chipText = chip.getText().toString();
                if (selected.contains(chipText)) {
                    chip.setChecked(true);
                }
            }
        }

        private void setupChips() {
            // Bathroom chips
            addChips(chipBath, "1 Bath", "2 Bath", "3 Bath", "4 Bath", "5+ Bath");

            // Bed chips - Based on BEDROOMS in types field
            addChips(chipBed, "1 BED", "2 BED", "3 BED", "4 BED", "5 BED", "STUDIO");

            // Flat Size chips - Your specific sizes
            addChips(chipFlatSize, "410", "415", "435", "440", "450", "530", "580",
                    "585", "650", "660", "725", "765", "775", "790", "800", "825",
                    "830", "850", "870", "1000", "1005", "1010", "1100", "1230",
                    "1250", "1260", "1310", "1395", "1460", "1520", "1540", "1550",
                    "1565", "1600", "1615", "1630", "1655", "1680", "1790", "2055",
                    "2150", "2190", "2250", "2600", "3100"
            );

            // Balcony chips
            addChips(chipBalcony, "1 Balcony", "2 Balcony", "3 Balcony", "4 Balcony", "6 Balcony", "7 Balcony");

            // Location chips - Your specific locations
            addChips(chipLocation,
                    "Ashulia Model Town",
                    "Bashundhara Residential Area",
                    "The Premium Smart City",
                    "The Premium Royal City",
                    "Ati Model Town",
                    "D Block, Ashulia Model Town",
                    "Road: 10/A Block: F Plot: 8",
                    "Road: 11 Block: K ,Plot: 14");
        }

        private void addChips(ChipGroup group, String... texts) {
            for (String text : texts) {
                Chip chip = new Chip(requireContext());
                chip.setText(text);
                chip.setCheckable(true);
                chip.setChipStrokeColorResource(R.color.gray_300);
                chip.setChipStrokeWidth(1.5f);
                chip.setChipBackgroundColorResource(android.R.color.white);

                group.addView(chip);
            }
        }

        private void setupListeners() {
            ChipGroup.OnCheckedStateChangeListener listener = (group, checkedIds) -> {
                previewBaths.clear();
                previewBeds.clear();
                previewFlatSizes.clear();
                previewBalconies.clear();
                previewLocations.clear();

                previewBaths.addAll(getCheckedTexts(chipBath));
                previewBeds.addAll(getCheckedTexts(chipBed));
                previewFlatSizes.addAll(getCheckedTexts(chipFlatSize));
                previewBalconies.addAll(getCheckedTexts(chipBalcony));
                previewLocations.addAll(getCheckedTexts(chipLocation));

                updatePreviewAndBadges();
            };

            chipBath.setOnCheckedStateChangeListener(listener);
            chipBed.setOnCheckedStateChangeListener(listener);
            chipFlatSize.setOnCheckedStateChangeListener(listener);
            chipBalcony.setOnCheckedStateChangeListener(listener);
            chipLocation.setOnCheckedStateChangeListener(listener);
        }

        private Set<String> getCheckedTexts(ChipGroup group) {
            Set<String> set = new HashSet<>();
            for (int id : group.getCheckedChipIds()) {
                Chip chip = group.findViewById(id);
                if (chip != null) {
                    set.add(chip.getText().toString());
                }
            }
            return set;
        }

        private void updatePreviewAndBadges() {
            updateBadge(tvBathCount, previewBaths.size());
            updateBadge(tvBedCount, previewBeds.size());
            updateBadge(tvFlatSizeCount, previewFlatSizes.size());
            updateBadge(tvBalconyCount, previewBalconies.size());
            updateBadge(tvLocationCount, previewLocations.size());

            int count = calculateLiveFilteredCount();
            tvShowingCount.setText("Showing " + count + " projects");
        }

        private int calculateLiveFilteredCount() {
            SearchFragment parent = (SearchFragment) getParentFragment();
            if (parent == null || parent.fullList.isEmpty()) {
                return parent != null ? parent.fullList.size() : 0;
            }

            int count = 0;
            for (SearchModel model : parent.fullList) {
                boolean matches = true;

                // Search text filter
                if (!searchQuery.isEmpty()) {
                    String query = searchQuery.toLowerCase();
                    boolean matchesSearch = model.getName().toLowerCase().contains(query) ||
                            model.getLocation().toLowerCase().contains(query) ||
                            model.getTypes().toLowerCase().contains(query) ||
                            model.getCommunity().toLowerCase().contains(query) ||
                            model.getFullLocation().toLowerCase().contains(query);
                    if (!matchesSearch) matches = false;
                }

                // Bath filter
                if (!previewBaths.isEmpty()) {
                    boolean bathMatch = false;
                    for (String bath : previewBaths) {
                        String bathNum = bath.replaceAll("[^0-9]", "");
                        if (model.getBathrooms().contains(bathNum)) {
                            bathMatch = true;
                            break;
                        }
                    }
                    if (!bathMatch) matches = false;
                }

                // Bed filter
                if (!previewBeds.isEmpty()) {
                    boolean bedMatch = false;
                    for (String bed : previewBeds) {
                        String bedNum = bed.replaceAll("[^0-9]", "");
                        if (containsNumber(model.getFlatSizes(), bedNum) ||
                                model.getTypes().toLowerCase().contains(bed.toLowerCase())) {
                            bedMatch = true;
                            break;
                        }
                    }
                    if (!bedMatch) matches = false;
                }

                // Flat Size filter
                if (!previewFlatSizes.isEmpty()) {
                    boolean sizeMatch = false;
                    for (String size : previewFlatSizes) {
                        if (containsSize(model.getFlatSizes(), size)) {
                            sizeMatch = true;
                            break;
                        }
                    }
                    if (!sizeMatch) matches = false;
                }

                // Balcony filter
                if (!previewBalconies.isEmpty()) {
                    boolean balconyMatch = false;
                    for (String balcony : previewBalconies) {
                        String balconyNum = balcony.replaceAll("[^0-9]", "");
                        if (model.getBalconies().contains(balconyNum)) {
                            balconyMatch = true;
                            break;
                        }
                    }
                    if (!balconyMatch) matches = false;
                }

                // Location filter
                if (!previewLocations.isEmpty()) {
                    boolean locationMatch = false;
                    for (String location : previewLocations) {
                        if (containsLocation(model, location)) {
                            locationMatch = true;
                            break;
                        }
                    }
                    if (!locationMatch) matches = false;
                }

                if (matches) count++;
            }
            return count;
        }

        private boolean containsNumber(List<String> list, String number) {
            if (list == null) return false;
            for (String item : list) {
                if (item.contains(number)) {
                    return true;
                }
            }
            return false;
        }

        private boolean containsSize(List<String> sizes, String size) {
            if (sizes == null) return false;
            for (String s : sizes) {
                if (s.equalsIgnoreCase(size) || s.contains(size)) {
                    return true;
                }
            }
            return false;
        }

        private boolean containsLocation(SearchModel model, String location) {
            if (model.getFilterLocations() != null) {
                for (String loc : model.getFilterLocations()) {
                    if (loc.toLowerCase().contains(location.toLowerCase())) {
                        return true;
                    }
                }
            }

            if (model.getLocation().toLowerCase().contains(location.toLowerCase())) {
                return true;
            }

            if (model.getFullLocation().toLowerCase().contains(location.toLowerCase())) {
                return true;
            }

            return false;
        }

        private void updateBadge(TextView badge, int count) {
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisibility(VISIBLE);
            } else {
                badge.setVisibility(GONE);
            }
        }

        private void clearAllChips() {
            chipBath.clearCheck();
            chipBed.clearCheck();
            chipFlatSize.clearCheck();
            chipBalcony.clearCheck();
            chipLocation.clearCheck();

            previewBaths.clear();
            previewBeds.clear();
            previewFlatSizes.clear();
            previewBalconies.clear();
            previewLocations.clear();
            searchQuery = "";
            searchEditText.setText("");

            updatePreviewAndBadges();
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