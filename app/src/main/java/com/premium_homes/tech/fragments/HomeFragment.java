package com.premium_homes.tech.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.premium_homes.tech.Contact_or_MessageActivity;
import com.premium_homes.tech.FragmentNavigator;
import com.premium_homes.tech.MainActivity;
import com.premium_homes.tech.PropertyViewActivity;
import com.premium_homes.tech.R;
import com.premium_homes.tech.adapters.BlogAdapter;
import com.premium_homes.tech.adapters.CommunityProjectAdapter;
import com.premium_homes.tech.adapters.DirectVideoAdapter;
import com.premium_homes.tech.adapters.ProgressAdapter;
import com.premium_homes.tech.adapters.ProjectAdapter;
import com.premium_homes.tech.models.BlogItem;
import com.premium_homes.tech.models.CommunityProjectItem;
import com.premium_homes.tech.models.DirectVideoModel;
import com.premium_homes.tech.models.ProgressItem;
import com.premium_homes.tech.models.ProjectModel;

public class HomeFragment extends Fragment {

    private NestedScrollView nestedScrollView;
    private LinearLayout projectsByCommunitySection;
    private boolean isHeaderChanged = false;
    private static final int HEADER_CHANGE_THRESHOLD = 100;

    // Views
    private RecyclerView blogPostsLists, liveProgressList, rvLeftToRight, rvRightToLeft,
            recyclerView, featuredProjectLists, reviewLists;
    private CardView allCommunity, premiumSmartCity, ashuliaModel, premiumRoyalCity, bashundharaRA;
    private ImageSlider imageSlider;
    private Button contact;
    private TextView projectsView, seeAll, viewReviews;
    private EditText searchEditText;
    private LinearLayout mainLayout;

    // Adapters
    private ProjectAdapter leftAdapter, rightAdapter, featuredAdapter;
    private CommunityProjectAdapter communityAdapter;
    private BlogAdapter blogAdapter;
    private ProgressAdapter progressAdapter;
    private DirectVideoAdapter directVideoAdapter;

    // Data
    private List<ProjectModel> projectBaseList = new ArrayList<>();
    private List<CommunityProjectItem> allCommunityProjects = new ArrayList<>();
    private List<CommunityProjectItem> communityBaseList = new ArrayList<>();
    private List<BlogItem> blogList = new ArrayList<>();
    private CardView currentSelectedTab = null;

    // Handler and Auto-scroll
    private Handler handler = new Handler();
    private boolean isPaused = false;
    private boolean isCommunityUserScrolling = false;
    private boolean isFeaturedUserScrolling = false;
    private int communityCurrentPosition = 0;
    private int featuredCurrentPosition = 0;
    private final int speed = 2;
    private final long SCROLL_DELAY_MS = 3000;
    private final long FEATURED_SCROLL_DELAY_MS = 4000;

    // Loading state management
    private int loadingTasks = 0;
    private boolean loaderHidden = false;
    private boolean isFragmentVisible = false;

    private View rootView;
    private RequestQueue requestQueue;

    // Runnable for auto-scrolling
    private Runnable scrollRight, scrollLeft, communityAutoScrollRunnable, featuredAutoScrollRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Fragment create হওয়ার সাথে সাথে loader show করবে
        if (getActivity() != null) {
            ((MainActivity) getActivity()).showLoader();
            Log.d("HomeFragment", "Loader shown in onCreateView");
        }

        initViews(rootView);
        setupScrollListener();

        // শুরুতে mainLayout GONE রাখবে
        if (mainLayout != null) {
            mainLayout.setVisibility(View.GONE);
            Log.d("HomeFragment", "mainLayout set to GONE initially");
        }

        setupBlogRecyclerView();
        setupTabClickListeners();
        setupAutoScrollingRows();
        setupCommunitySlider();
        setupFeaturedSlider();

        // API calls that need loader
        startLoading();
        setupImageSliderFromApi();

        startLoading();
        loadProjectsFromApi();

        startLoading();
        loadBlogs();

        // No loader needed (local/static)
        setupClientReviews();
        LiveProgress();

        setupClickListeners();

        return rootView;
    }

    private void initViews(View view) {
        contact = view.findViewById(R.id.contact);
        imageSlider = view.findViewById(R.id.imageSlider);
        rvLeftToRight = view.findViewById(R.id.rvLeftToRight);
        rvRightToLeft = view.findViewById(R.id.rvRightToLeft);
        recyclerView = view.findViewById(R.id.projectLists);
        featuredProjectLists = view.findViewById(R.id.featuredProjectLists);
        reviewLists = view.findViewById(R.id.reviewLists);
        blogPostsLists = view.findViewById(R.id.blogPostsLists);
        liveProgressList = view.findViewById(R.id.liveProgressList);
        projectsView = view.findViewById(R.id.projectsView);
        seeAll = view.findViewById(R.id.seeAll);
        searchEditText = view.findViewById(R.id.searchEditText);
        viewReviews = view.findViewById(R.id.viewReviews);
        mainLayout = view.findViewById(R.id.layoutMain); // XML-এ এই ID যোগ করতে হবে

        // Initialize tab views
        allCommunity = view.findViewById(R.id.allCommunity);
        premiumSmartCity = view.findViewById(R.id.premiumSmartCity);
        ashuliaModel = view.findViewById(R.id.ashuliaModel);
        premiumRoyalCity = view.findViewById(R.id.premiumRoyalCity);
        bashundharaRA = view.findViewById(R.id.bashundharaRA);

        // In initViews method, add:
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        projectsByCommunitySection = rootView.findViewById(R.id.projectsByCommunitySection);

        Log.d("HomeFragment", "Views initialized, mainLayout: " + (mainLayout != null));
    }

    //Scroll Listener
    private void setupScrollListener() {
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (projectsByCommunitySection != null && getActivity() != null) {
                        // Get the position of the Projects by Community section
                        int[] location = new int[2];
                        projectsByCommunitySection.getLocationOnScreen(location);
                        int sectionTop = location[1];

                        // Calculate how much of the section is visible
                        int screenHeight = getResources().getDisplayMetrics().heightPixels;
                        int scrollViewTop = nestedScrollView.getTop();

                        // When the section reaches near the top of the screen
                        if (sectionTop <= scrollViewTop + HEADER_CHANGE_THRESHOLD) {
                            if (!isHeaderChanged) {
                                changeHeaderColor(true); // Change to new color
                                isHeaderChanged = true;
                            }
                        } else {
                            if (isHeaderChanged) {
                                changeHeaderColor(false); // Revert to original color
                                isHeaderChanged = false;
                            }
                        }
                    }
                }
            });
        }
    }

    //change header color
    private void changeHeaderColor(boolean shouldChange) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            // You can define this interface method in MainActivity
            if (shouldChange) {
                // Change to your desired color (e.g., white or primary color)
                mainActivity.changeHeaderBackground(R.color.white); // Or R.color.primary
            } else {
                // Revert to original color
                mainActivity.changeHeaderBackground(R.color.primary); // Or original color
            }
        }
    }

    private void setupClickListeners() {
        contact.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Contact_or_MessageActivity.class));
        });

        projectsView.setOnClickListener(v -> {
            if (getActivity() instanceof FragmentNavigator) {
                ((FragmentNavigator) getActivity()).openProjectsFragment();
            }
        });

        seeAll.setOnClickListener(v -> {
            if (getActivity() instanceof FragmentNavigator) {
                ((FragmentNavigator) getActivity()).openProjectsFragment();
            }
        });

        viewReviews.setOnClickListener(v -> {
            if (getActivity() instanceof FragmentNavigator) {
                ((FragmentNavigator) getActivity()).openReviewsFragment();
            }
        });

        searchEditText.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString().trim();
            if (getActivity() instanceof FragmentNavigator) {
                ((FragmentNavigator) getActivity()).openSearchFragment(searchText);
            }
        });
    }

//CCTV live progress
    private void LiveProgress() {
        List<ProgressItem> list = new ArrayList<>();
        list.add(new ProgressItem(
                "See Our Progress",
                "Live Now Checkout Now!",
                R.drawable.construction_image
        ));
        list.add(new ProgressItem(
                "Virtual Tour",
                "Coming Soon",
                R.drawable.construction_image
        ));
        list.add(new ProgressItem(
                "Project Map",
                "Coming Soon",
                R.drawable.construction_image
        ));

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        liveProgressList.setLayoutManager(layoutManager);
        progressAdapter = new ProgressAdapter(getActivity(), list);
        liveProgressList.setAdapter(progressAdapter);
    }

    private void setupBlogRecyclerView() {
        if (blogPostsLists == null) {
            Log.e("BlogSetup", "blogPostsLists is null");
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false);
        blogPostsLists.setLayoutManager(layoutManager);
        blogAdapter = new BlogAdapter(getActivity(), blogList);
        blogPostsLists.setAdapter(blogAdapter);
        requestQueue = Volley.newRequestQueue(getActivity());
    }

    private void loadBlogs() {
        String url = "https://premium-api.dvalleybd.com/blogs.php?action=get-all-blogs";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("BlogResponse", "Blog API response received");

                        if (response.getBoolean("success")) {
                            JSONArray blogsArray = response.getJSONArray("blogs");
                            Log.d("Blogs", "Found " + blogsArray.length() + " blogs");

                            blogList.clear();
                            for (int i = 0; i < blogsArray.length(); i++) {
                                JSONObject blogObject = blogsArray.getJSONObject(i);
                                BlogItem blog = new BlogItem();
                                blog.setId(blogObject.optInt("id", i + 1));
                                blog.setTitle(blogObject.optString("title", "No Title"));
                                blog.setImage(blogObject.optString("image", ""));
                                blog.setAuthor(blogObject.optString("author", "Admin"));
                                blog.setDate(blogObject.optString("date", ""));
                                blogList.add(blog);
                            }

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (blogAdapter != null) {
                                        blogAdapter.updateList(blogList);
                                        Log.d("Blogs", "Blog adapter updated");
                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        Log.e("Blogs", "Error parsing blogs: " + e.getMessage());
                    } finally {
                        finishLoading();
                    }
                },
                error -> {
                    Log.e("Blogs", "Volley error: " + error.getMessage());
                    failLoading();
                }
        );

        if (requestQueue != null) {
            requestQueue.add(request);
        }
    }

    private void setupTabClickListeners() {
        allCommunity.setOnClickListener(v -> {
            filterProjectsByCommunity("All");
            setSelectedTab(allCommunity);
        });

        premiumSmartCity.setOnClickListener(v -> {
            filterProjectsByCommunity("The Premium Smart City");
            setSelectedTab(premiumSmartCity);
        });

        ashuliaModel.setOnClickListener(v -> {
            filterProjectsByCommunity("Ashulia Model Town");
            setSelectedTab(ashuliaModel);
        });

        premiumRoyalCity.setOnClickListener(v -> {
            filterProjectsByCommunity("The Premium Royal City");
            setSelectedTab(premiumRoyalCity);
        });

        bashundharaRA.setOnClickListener(v -> {
            filterProjectsByCommunity("Bashundhara Residential Area");
            setSelectedTab(bashundharaRA);
        });
    }

    private void filterProjectsByCommunity(String communityName) {
        List<CommunityProjectItem> filteredList;

        if (communityName.equals("All")) {
            filteredList = new ArrayList<>(allCommunityProjects);
        } else {
            filteredList = new ArrayList<>();
            for (CommunityProjectItem item : allCommunityProjects) {
                if (item.getCommunity().equalsIgnoreCase(communityName)) {
                    filteredList.add(item);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No projects found for " + communityName, Toast.LENGTH_SHORT).show();
        }

        List<CommunityProjectItem> infiniteList = createInfiniteCommunityList(filteredList);
        communityAdapter.updateList(infiniteList);

        communityCurrentPosition = infiniteList.size() / 2;
        recyclerView.scrollToPosition(communityCurrentPosition);
    }

    private void setSelectedTab(CardView selectedCard) {
        resetTabStyles();

        if (selectedCard != null) {
            selectedCard.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primaryDark));
            TextView textView = (TextView) selectedCard.getChildAt(0);
            if (textView != null) {
                textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            }
            currentSelectedTab = selectedCard;
        }
    }

    private void resetTabStyles() {
        CardView[] tabs = {allCommunity, premiumSmartCity, ashuliaModel, premiumRoyalCity, bashundharaRA};

        for (CardView tab : tabs) {
            tab.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            TextView textView = (TextView) tab.getChildAt(0);
            if (textView != null) {
                textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryDark));
            }
        }
    }

    private void loadProjectsFromApi() {
        String url = "https://premium-api.dvalleybd.com/projects.php?action=get-all-projects";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    projectBaseList.clear();
                    allCommunityProjects.clear();
                    communityBaseList.clear();

                    try {
                        JSONArray arr = response.getJSONArray("allProperties");
                        Log.d("HomeFragment", "Projects API response received: " + arr.length() + " projects");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            String id = o.optString("id", String.valueOf(i));
                            if (id.isEmpty()) {
                                id = o.optString("property_id", String.valueOf(i));
                            }

                            projectBaseList.add(new ProjectModel(
                                    id,
                                    o.getString("image"),
                                    o.getString("name"),
                                    o.getString("location")
                            ));

                            CommunityProjectItem communityItem = new CommunityProjectItem(
                                    id,
                                    o.getString("image"),
                                    o.getString("name"),
                                    o.getString("location"),
                                    o.getString("types"),
                                    o.optString("tag").equalsIgnoreCase("ON SALE"),
                                    o.getString("community"),
                                    o.getString("community")
                            );

                            communityBaseList.add(communityItem);
                            allCommunityProjects.add(communityItem);
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                updateAdapters();
                                updateTabCounts();
                                setSelectedTab(allCommunity);
                            });
                        }

                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing projects: " + e.getMessage());
                    } finally {
                        finishLoading();
                    }
                },
                error -> {
                    Log.e("HomeFragment", "Projects API error: " + error.getMessage());
                    failLoading();
                }
        );

        Volley.newRequestQueue(getActivity()).add(request);
    }

    private void updateTabCounts() {
        updateSingleTabCount(premiumSmartCity, "The Premium Smart City");
        updateSingleTabCount(ashuliaModel, "Ashulia Model Town");
        updateSingleTabCount(premiumRoyalCity, "The Premium Royal City");
        updateSingleTabCount(bashundharaRA, "Bashundhara Residential Area");

        TextView allText = (TextView) allCommunity.getChildAt(0);
        if (allText != null) {
            allText.setText("All (" + allCommunityProjects.size() + ")");
        }
    }

    private void updateSingleTabCount(CardView cardView, String communityName) {
        TextView textView = (TextView) cardView.getChildAt(0);
        if (textView == null) return;

        int count = 0;
        for (CommunityProjectItem item : allCommunityProjects) {
            if (item.getCommunity().equalsIgnoreCase(communityName)) {
                count++;
            }
        }

        String displayText;
        switch (communityName) {
            case "The Premium Smart City":
                displayText = "Smart City (" + count + ")";
                break;
            case "Ashulia Model Town":
                displayText = "Ashulia (" + count + ")";
                break;
            case "The Premium Royal City":
                displayText = "Royal City (" + count + ")";
                break;
            case "Bashundhara Residential Area":
                displayText = "Bashundhara (" + count + ")";
                break;
            default:
                displayText = communityName + " (" + count + ")";
        }

        textView.setText(displayText);
    }

    private List<ProjectModel> getInfiniteProjects() {
        List<ProjectModel> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) list.addAll(projectBaseList);
        return list;
    }

    private List<CommunityProjectItem> getInfiniteCommunity() {
        List<CommunityProjectItem> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) list.addAll(communityBaseList);
        return list;
    }

    private List<CommunityProjectItem> createInfiniteCommunityList(List<CommunityProjectItem> originalList) {
        if (originalList.isEmpty()) {
            return new ArrayList<>();
        }

        List<CommunityProjectItem> infiniteList = new ArrayList<>();
        int copies = 5;
        for (int i = 0; i < copies; i++) {
            infiniteList.addAll(originalList);
        }

        return infiniteList;
    }

    private void updateAdapters() {
        if (leftAdapter != null && rightAdapter != null && featuredAdapter != null && communityAdapter != null) {
            leftAdapter.updateList(getInfiniteProjects());
            rightAdapter.updateList(getInfiniteProjects());

            featuredAdapter.updateList(getInfiniteProjects());
            featuredCurrentPosition = featuredAdapter.getItemCount() / 2;
            featuredProjectLists.scrollToPosition(featuredCurrentPosition);

            communityAdapter.updateList(getInfiniteCommunity());
            communityCurrentPosition = communityAdapter.getItemCount() / 2;
            recyclerView.scrollToPosition(communityCurrentPosition);
        }
    }

    private void setupAutoScrollingRows() {
        rvLeftToRight.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rvRightToLeft.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, true));

        leftAdapter = new ProjectAdapter(getActivity(), new ArrayList<>(), project ->
                openPropertyView(project.getId())
        );

        rightAdapter = new ProjectAdapter(getActivity(), new ArrayList<>(), project ->
                openPropertyView(project.getId())
        );

        rvLeftToRight.setAdapter(leftAdapter);
        rvRightToLeft.setAdapter(rightAdapter);

        rvLeftToRight.setOnTouchListener((v, e) -> {
            isPaused = e.getAction() == MotionEvent.ACTION_DOWN;
            return false;
        });

        rvRightToLeft.setOnTouchListener((v, e) -> {
            isPaused = e.getAction() == MotionEvent.ACTION_DOWN;
            return false;
        });

        // Initialize runnables
        scrollRight = new Runnable() {
            @Override
            public void run() {
                if (!isPaused && isFragmentVisible) {
                    rvLeftToRight.scrollBy(speed, 0);
                }
                if (handler != null && isFragmentVisible) {
                    handler.postDelayed(this, 20);
                }
            }
        };

        scrollLeft = new Runnable() {
            @Override
            public void run() {
                if (!isPaused && isFragmentVisible) {
                    rvRightToLeft.scrollBy(-speed, 0);
                }
                if (handler != null && isFragmentVisible) {
                    handler.postDelayed(this, 20);
                }
            }
        };
    }

    private void openPropertyView(String projectId) {
        Intent intent = new Intent(getActivity(), PropertyViewActivity.class);
        intent.putExtra("propertyId", projectId);
        startActivity(intent);
    }

    private void setupCommunitySlider() {
        communityAdapter = new CommunityProjectAdapter(getActivity(), new ArrayList<>(), project ->
                openPropertyView(project.getId())
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(communityAdapter);
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        recyclerView.setOnTouchListener((v, e) -> {
            isCommunityUserScrolling = e.getAction() == MotionEvent.ACTION_DOWN;
            return false;
        });

        communityAutoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isCommunityUserScrolling && communityAdapter.getItemCount() > 0 && isFragmentVisible) {
                    communityCurrentPosition++;
                    if (communityCurrentPosition >= communityAdapter.getItemCount() - 1) {
                        communityCurrentPosition = communityAdapter.getItemCount() / 2;
                        recyclerView.scrollToPosition(communityCurrentPosition);
                    } else {
                        recyclerView.smoothScrollToPosition(communityCurrentPosition);
                    }
                }
                if (handler != null && isFragmentVisible) {
                    handler.postDelayed(this, SCROLL_DELAY_MS);
                }
            }
        };
    }

    private void setupFeaturedSlider() {
        featuredAdapter = new ProjectAdapter(getActivity(), new ArrayList<>(), project ->
                openPropertyView(project.getId())
        );

        featuredProjectLists.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        featuredProjectLists.setAdapter(featuredAdapter);
        new PagerSnapHelper().attachToRecyclerView(featuredProjectLists);

        featuredProjectLists.setOnTouchListener((v, e) -> {
            isFeaturedUserScrolling = e.getAction() == MotionEvent.ACTION_DOWN;
            return false;
        });

        featuredAutoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFeaturedUserScrolling && featuredAdapter.getItemCount() > 0 && isFragmentVisible) {
                    featuredCurrentPosition++;
                    if (featuredCurrentPosition >= featuredAdapter.getItemCount() - 1)
                        featuredCurrentPosition = featuredAdapter.getItemCount() / 2;
                    featuredProjectLists.smoothScrollToPosition(featuredCurrentPosition);
                }
                if (handler != null && isFragmentVisible) {
                    handler.postDelayed(this, FEATURED_SCROLL_DELAY_MS);
                }
            }
        };
    }

    private void setupImageSliderFromApi() {

        String url = "https://premium-api.dvalleybd.com/slides.php?action=get-all-slides";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {

                    try {
                        if (response.getBoolean("success")) {

                            JSONArray arr = response.getJSONArray("slides");
                            ArrayList<SlideModel> slides = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++) {

                                JSONObject obj = arr.getJSONObject(i);

                                String title = obj.getString("title");
                                String subtitle = obj.getString("subtitle");
                                String image = obj.getString("image");

                                // title + subtitle একসাথে caption হিসেবে দেখাবে
                                String caption = title + "\n" + subtitle;

                                slides.add(new SlideModel(image, caption, ScaleTypes.FIT));
                            }

                            imageSlider.setImageList(slides, ScaleTypes.FIT);
                            Log.d("HomeFragment", "Image slider loaded with " + slides.size() + " slides");

                        } else {
                            Log.e("HomeFragment", "API success false");
                        }

                    } catch (Exception e) {
                        Log.e("HomeFragment", "Parsing error: " + e.getMessage());
                    } finally {
                        finishLoading();
                    }

                },
                error -> {
                    Log.e("HomeFragment", "API error: " + error.getMessage());
                    failLoading();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }


    /**
    private void setupClientReviews() {
        reviewLists.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        ArrayList<DirectVideoModel> list = new ArrayList<>();
        list.add(new DirectVideoModel("Customer Review", "zxcvNVqIl-0"));
        list.add(new DirectVideoModel("ফ্ল্যাট কেনার আগে ভিডিও টা একবার দেখে নিতে পারেন", "qM5_UKR-vzk"));
        directVideoAdapter = new DirectVideoAdapter(getActivity(), getActivity(), list);
        reviewLists.setAdapter(directVideoAdapter);
    }
    **/

    private void setupClientReviews() {

        reviewLists.setLayoutManager(
                new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false)
        );

        ArrayList<DirectVideoModel> list = new ArrayList<>();

        String url = "https://premium-api.dvalleybd.com/reviews.php?action=get-all-reviews";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {

                    try {

                        JSONArray arr = response.getJSONArray("reviews");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject obj = arr.getJSONObject(i);

                            list.add(new DirectVideoModel(
                                    obj.getString("name"),
                                    obj.getString("role"),
                                    obj.getString("project"),
                                    obj.getString("review"),
                                    obj.getString("date"),
                                    obj.getInt("rating"),
                                    obj.getString("videoUrl")
                            ));
                        }

                        directVideoAdapter = new DirectVideoAdapter(getActivity(), getViewLifecycleOwner(), list);
                        reviewLists.setAdapter(directVideoAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(getActivity()).add(request);
    }

    // Three helpers for progressBar - FIXED VERSION
    private void startLoading() {
        loadingTasks++;
        Log.d("HomeFragment", "startLoading: tasks=" + loadingTasks);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (!loaderHidden) {
                    ((MainActivity) getActivity()).showLoader();
                }
            });
        }
    }

    private void finishLoading() {
        loadingTasks--;
        Log.d("HomeFragment", "finishLoading: tasks=" + loadingTasks);

        if (loadingTasks <= 0 && !loaderHidden) {
            loaderHidden = true;
            Log.d("HomeFragment", "All loading tasks completed");

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // 1. Hide loader
                    ((MainActivity) getActivity()).hideLoader();

                    // 2. Show main layout
                    if (mainLayout != null) {
                        mainLayout.setVisibility(View.VISIBLE);
                        Log.d("HomeFragment", "mainLayout set to VISIBLE");
                    }

                    // 3. Start auto-scrolling
                    startAutoScrolling();
                });
            }
        }
    }

    private void failLoading() {
        loadingTasks--;
        Log.d("HomeFragment", "failLoading: tasks=" + loadingTasks);

        if (loadingTasks <= 0 && !loaderHidden) {
            loaderHidden = true;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Hide loader even if failed
                    ((MainActivity) getActivity()).hideLoader();

                    // Still show layout with error message
                    if (mainLayout != null) {
                        mainLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Failed to load some data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void startAutoScrolling() {
        if (handler != null && isFragmentVisible) {
            handler.post(scrollRight);
            handler.post(scrollLeft);
            handler.postDelayed(communityAutoScrollRunnable, SCROLL_DELAY_MS);
            handler.postDelayed(featuredAutoScrollRunnable, FEATURED_SCROLL_DELAY_MS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;

        // OnResume-এ শুধু auto-scrolling restart করবে যদি ইতিমধ্যে data loaded থাকে
        if (loaderHidden && mainLayout != null && mainLayout.getVisibility() == View.VISIBLE) {
            startAutoScrolling();
        } else if (!loaderHidden) {
            // Still loading, show loader
            if (getActivity() != null) {
                ((MainActivity) getActivity()).showLoader();
            }
        }

        Log.d("HomeFragment", "onResume: loaderHidden=" + loaderHidden + ", loadingTasks=" + loadingTasks);
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;

        // Stop auto-scrolling
        if (handler != null) {
            handler.removeCallbacks(scrollRight);
            handler.removeCallbacks(scrollLeft);
            handler.removeCallbacks(communityAutoScrollRunnable);
            handler.removeCallbacks(featuredAutoScrollRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentVisible = false;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        // Reset state when fragment is destroyed
        loadingTasks = 0;
        loaderHidden = false;

        Log.d("HomeFragment", "onDestroyView: state reset");
    }
}