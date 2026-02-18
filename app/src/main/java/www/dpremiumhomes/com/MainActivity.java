package www.dpremiumhomes.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import www.dpremiumhomes.com.fragments.*;
import www.dpremiumhomes.com.helpers.SessionManager;

public class MainActivity extends AppCompatActivity implements FragmentNavigator {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton, btnDashboardMenu;
    private ImageView logo;
    private FrameLayout drawerContainer;
    private CardView logoHeader, dashboardHeader;
    private ImageButton btnDashboardBack;
    private ImageButton navHome, navProjects, navContact, navAbout;
    private FloatingActionButton fabMain;
    private ProgressBar progressBar;

    private Fragment currentFragment;
    private boolean isDashboardActive = false;

    @Override
    public void openProjectsFragment() {
        loadFragment(new ProjectsFragment(), true);
    }

    @Override
    public void openReviewsFragment() {
        loadFragment(new ReviewsFragment(), true);
    }

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment fragment =
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (fragment == null) return;

            Log.d("MainActivity", "BackStackChanged -> " + fragment.getClass().getSimpleName());

            currentFragment = fragment;

            if (fragment instanceof DashboardFragment) {
                showDashboardHeader();
                loadDashboardMenu();
                setBottomNavForDashboard();
            } else {
                showLogoHeader();
                loadNormalMenu();
                updateBottomNavigation(fragment);
            }
        });


        setStatusBarColor();
        initViews();
        setupNavigationButtons();
        setupDrawer();
        setupFABs();
        setupBottomNavigation();

        // Decide initial fragment based on intent (e.g., after login)
        boolean openDashboard = getIntent().getBooleanExtra("open_dashboard", false);

        if (openDashboard && session.isLoggedIn()) {
            loadFragment(new DashboardFragment(), false);
            showDashboardHeader();
            loadDashboardMenu();
            setBottomNavForDashboard();
        } else {
            loadFragment(new HomeFragment(), false);
            showLogoHeader();
            loadNormalMenu();
            setBottomNavSelected(navHome);
        }

        setupBackPressedHandler();
    }

    /* -------------------- INITIALIZATION -------------------- */
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(this, R.color.primary)
            );
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menuButton);
        btnDashboardMenu = findViewById(R.id.btnDashboardMenu);
        drawerContainer = findViewById(R.id.drawer_container);

        logoHeader = findViewById(R.id.logoHeader);
        dashboardHeader = findViewById(R.id.dashboardHeader);
        btnDashboardBack = findViewById(R.id.btnDashboardBack);
        logo = findViewById(R.id.logo);

        navHome = findViewById(R.id.nav_home);
        navProjects = findViewById(R.id.nav_projects);
        fabMain = findViewById(R.id.fabMain);
        navContact = findViewById(R.id.nav_favorite);
        navAbout = findViewById(R.id.nav_profile);
        progressBar = findViewById(R.id.ProgressBar01);
    }

    /* -------------------- NAVIGATION BUTTONS -------------------- */
    private void setupNavigationButtons() {
        menuButton.setOnClickListener(v -> {
            loadNormalMenu();
            drawerLayout.openDrawer(drawerContainer);
        });

        logo.setOnClickListener(v -> {
            loadNormalMenu();
            drawerLayout.openDrawer(drawerContainer);
        });

        btnDashboardMenu.setOnClickListener(v -> {
            loadDashboardMenu();
            drawerLayout.openDrawer(drawerContainer);
        });

        btnDashboardBack.setOnClickListener(v -> {
            loadFragment(new HomeFragment(), true);
            setBottomNavSelected(navHome);
        });
    }

    /* -------------------- DRAWER MENU -------------------- */
    private void setupDrawer() {
        loadNormalMenu();
    }

    private void loadNormalMenu() {
        drawerContainer.removeAllViews();
        View menuView = LayoutInflater.from(this)
                .inflate(R.layout.menu_drawer, drawerContainer, false);
        setupNormalMenuActions(menuView);
        drawerContainer.addView(menuView);
        isDashboardActive = false;
    }

    private void loadDashboardMenu() {
        drawerContainer.removeAllViews();
        View menuView = LayoutInflater.from(this)
                .inflate(R.layout.nav_header_dash, drawerContainer, false);
        setupDashboardMenuActions(menuView);
        drawerContainer.addView(menuView);
        isDashboardActive = true;
    }

    private void setupNormalMenuActions(View menuView) {
        menuView.findViewById(R.id.menuHome).setOnClickListener(v -> {
            loadFragment(new HomeFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menuProjects).setOnClickListener(v -> {
            loadFragment(new ProjectsFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menuReviews).setOnClickListener(v -> {
            loadFragment(new ReviewsFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menuContact).setOnClickListener(v -> {
            loadFragment(new ContactFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menuAbout).setOnClickListener(v -> {
            loadFragment(new AboutFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menuBlogs).setOnClickListener(v -> {
            loadFragment(new BlogsFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menuFaq).setOnClickListener(v -> {
            loadFragment(new FAQFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });

        menuView.findViewById(R.id.menuDashboard).setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                loadFragment(new DashboardFragment(), true);
                showDashboardHeader();
                loadDashboardMenu();
                setBottomNavForDashboard();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("open_dashboard", true);
                startActivity(intent);
                Toast.makeText(this, "Please login to access Dashboard", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(drawerContainer);
        });

        menuView.findViewById(R.id.btnBookNow).setOnClickListener(v -> {
            startActivity(new Intent(this, Contact_or_MessageActivity.class));
            drawerLayout.closeDrawer(drawerContainer);
        });

        // Login / Logout
        menuView.findViewById(R.id.btnLoginLogout).setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                // Logout
                session.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Login
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(drawerContainer);
        });
    }

    private void setupDashboardMenuActions(View menuView) {

        // Populate user data
        TextView tvName = menuView.findViewById(R.id.name);
        TextView tvEmail = menuView.findViewById(R.id.email);

        if (session.isLoggedIn()) {
            String name = session.getName();
            String email = session.getEmail();

            if (name != null && !name.isEmpty()) {
                tvName.setText(name);
            } else {
                tvName.setText("Guest User");
            }

            if (email != null && !email.isEmpty()) {
                tvEmail.setText(email);
            } else {
                tvEmail.setText("No email available");
            }
        } else {
            tvName.setText("Guest User");
            tvEmail.setText("Please login");
        }

        //-----------------------------------//
        menuView.findViewById(R.id.menu_dashboard).setOnClickListener(v -> drawerLayout.closeDrawer(drawerContainer));
        menuView.findViewById(R.id.menu_home).setOnClickListener(v -> {
            loadFragment(new HomeFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menu_projects).setOnClickListener(v -> {
            loadFragment(new ProjectsFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menu_my_flats).setOnClickListener(v -> {
            loadFragment(new MyFlatFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menu_auctions).setOnClickListener(v -> {
            loadFragment(new AuctionFragment(), true);
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menu_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            drawerLayout.closeDrawer(drawerContainer);
        });
        menuView.findViewById(R.id.menu_logout).setOnClickListener(v -> {
            session.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /* -------------------- FRAGMENT MANAGEMENT -------------------- */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        Log.d("MainActivity", "Loading fragment: " + fragment.getClass().getSimpleName());

        // Fragment লোড করার সময় loader show করবে
        showLoader();
        //resetHeaderColor();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
            Log.d("MainActivity", "Added to back stack: " + fragment.getClass().getSimpleName());
        }

        transaction.commit();

        currentFragment = fragment;

        if (fragment instanceof DashboardFragment) {
            showDashboardHeader();
            loadDashboardMenu();
            setBottomNavForDashboard();
        } else {
            showLogoHeader();
            loadNormalMenu();
            if (!(fragment instanceof HomeFragment)) {
                resetHeaderColor();
            }
            updateBottomNavigation(fragment);
        }

        Log.d("MainActivity", "Back stack entry count: " + getSupportFragmentManager().getBackStackEntryCount());
    }

    private void updateBottomNavigation(Fragment fragment) {
        Log.d("MainActivity", "Updating bottom nav for: " + fragment.getClass().getSimpleName());

        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);
        int primaryColor = ContextCompat.getColor(this, R.color.primaryDark);

        // Reset all
        navHome.setColorFilter(grayColor);
        navProjects.setColorFilter(grayColor);
        navContact.setColorFilter(grayColor);
        navAbout.setColorFilter(grayColor);

        // Set active based on fragment
        if (fragment instanceof HomeFragment) {
            navHome.setColorFilter(primaryColor);
        } else if (fragment instanceof ProjectsFragment || fragment instanceof SearchFragment) {
            navProjects.setColorFilter(primaryColor);
        } else if (fragment instanceof ContactFragment) {
            navContact.setColorFilter(primaryColor);
        } else if (fragment instanceof AboutFragment) {
            navAbout.setColorFilter(primaryColor);
        } else if (fragment instanceof ReviewsFragment) {
            // Reviews doesn't have a bottom nav item, keep current selection
        } else if (fragment instanceof BlogsFragment) {
            // Blogs doesn't have a bottom nav item, keep current selection
        } else if (fragment instanceof FAQFragment) {
            // FAQ doesn't have a bottom nav item, keep current selection
        }
    }

    private void setBottomNavForDashboard() {
        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);

        // Reset all for dashboard
        navHome.setColorFilter(grayColor);
        navProjects.setColorFilter(grayColor);
        navContact.setColorFilter(grayColor);
        navAbout.setColorFilter(grayColor);

        // Dashboard doesn't have a bottom nav item
    }

    /* -------------------- HEADER -------------------- */
    private void showDashboardHeader() {
        logoHeader.setVisibility(View.GONE);
        dashboardHeader.setVisibility(View.VISIBLE);
        isDashboardActive = true;
    }

    private void showLogoHeader() {
        dashboardHeader.setVisibility(View.GONE);
        logoHeader.setVisibility(View.VISIBLE);
        isDashboardActive = false;
    }

    /* -------------------- BOTTOM NAV -------------------- */
    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment(), true);
        });

        navProjects.setOnClickListener(v -> {
            loadFragment(new ProjectsFragment(), true);
        });

        fabMain.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                loadFragment(new DashboardFragment(), true);
                showDashboardHeader();
                loadDashboardMenu();
                setBottomNavForDashboard();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("open_dashboard", true);
                startActivity(intent);
                Toast.makeText(this, "Please login to access Dashboard", Toast.LENGTH_SHORT).show();
            }
        });

        navContact.setOnClickListener(v -> {
            loadFragment(new ContactFragment(), true);
        });

        navAbout.setOnClickListener(v -> {
            loadFragment(new AboutFragment(), true);
        });
    }

    private void setBottomNavSelected(ImageButton selected) {
        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);
        int primaryColor = ContextCompat.getColor(this, R.color.primaryDark);

        navHome.setColorFilter(grayColor);
        navProjects.setColorFilter(grayColor);
        navContact.setColorFilter(grayColor);
        navAbout.setColorFilter(grayColor);

        selected.setColorFilter(primaryColor);
    }

    /* -------------------- CONTACT METHODS -------------------- */
    private void openWhatsApp() {
        try {
            String url = "https://wa.me/8801958253300?text=" +
                    java.net.URLEncoder.encode("Hello, I'm interested in your properties.", "UTF-8");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "WhatsApp not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePhoneCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+8801958253300"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot make call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:info@thepremiumhomes.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Property Inquiry");
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void openSearchFragment(String searchText) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search_query", searchText);
        fragment.setArguments(bundle);

        loadFragment(fragment, true);
    }

    private void setupFABs() {
        FloatingActionButton fabWhatsapp = findViewById(R.id.fabWhatsapp);
        FloatingActionButton fabCall = findViewById(R.id.fabCall);
        FloatingActionButton fabEmail = findViewById(R.id.fabEmail);

        if (fabWhatsapp != null) {
            fabWhatsapp.setOnClickListener(v -> openWhatsApp());
        }
        if (fabCall != null) {
            fabCall.setOnClickListener(v -> makePhoneCall());
        }
        if (fabEmail != null) {
            fabEmail.setOnClickListener(v -> sendEmail());
        }
    }

    /* -------------------- LOADER METHODS -------------------- */
    public void showLoader() {
        Log.d("MainActivity", "showLoader called");
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            Log.d("MainActivity", "ProgressBar set to VISIBLE");
        } else {
            Log.e("MainActivity", "ProgressBar is null!");
        }
    }

    public void hideLoader() {
        Log.d("MainActivity", "hideLoader called");
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            Log.d("MainActivity", "ProgressBar set to GONE");
        }
    }

    //header back color
    // Add this method to MainActivity
    public void changeHeaderBackground(int colorResId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (logoHeader != null && logoHeader.getVisibility() == View.VISIBLE) {
                    // Change logo header background
                    logoHeader.setCardBackgroundColor(ContextCompat.getColor(MainActivity.this, colorResId));

                    // Also change text colors if needed
                    TextView title = findViewById(R.id.text_title);
                    TextView slogan = findViewById(R.id.text_slogan);
                    ImageView logo = findViewById(R.id.logo);

                    if (colorResId == R.color.white) {
                        // When background is white, use dark text
                        if (title != null) title.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                        if (slogan != null) slogan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                        if (logo != null) logo.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                        menuButton.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                    } else {
                        // When background is primary, use light text
                        if (title != null) title.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                        if (slogan != null) slogan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                        if (logo != null) logo.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white));
                        menuButton.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white));
                    }
                }
            }
        });
    }

    // Add this method to reset header when fragment changes
    private void resetHeaderColor() {
        if (logoHeader != null) {
            logoHeader.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
            TextView title = findViewById(R.id.text_title);
            TextView slogan = findViewById(R.id.text_slogan);
            ImageView logo = findViewById(R.id.logo);

            if (title != null) title.setTextColor(ContextCompat.getColor(this, R.color.primaryDark));
            if (slogan != null) slogan.setTextColor(ContextCompat.getColor(this, R.color.primaryDark));
            if (logo != null) logo.setColorFilter(ContextCompat.getColor(this, R.color.primaryDark));
            menuButton.setColorFilter(ContextCompat.getColor(this, R.color.primaryDark));
        }
    }



    /* -------------------- BACK PRESS -------------------- */
    private void setupBackPressedHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("MainActivity", "Back pressed, drawer open: " + drawerLayout.isDrawerOpen(drawerContainer));

                if (drawerLayout.isDrawerOpen(drawerContainer)) {
                    drawerLayout.closeDrawer(drawerContainer);
                    return;
                }

                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                Log.d("MainActivity", "Back stack count: " + backStackCount);

                if (backStackCount > 0) {
                    // Get current fragment before popping
                    Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    String currentName = current != null ? current.getClass().getSimpleName() : "null";
                    Log.d("MainActivity", "Current fragment before pop: " + currentName);

                    getSupportFragmentManager().popBackStack();

                    // Get the new current fragment after popping
                    if (drawerLayout.isDrawerOpen(drawerContainer)) {
                        drawerLayout.closeDrawer(drawerContainer);
                        return;
                    }

                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else if (currentFragment instanceof DashboardFragment) {
                        loadFragment(new HomeFragment(), false);
                    } else {
                        finish();
                    }


                } else if (currentFragment instanceof DashboardFragment) {
                    Log.d("MainActivity", "Navigating from Dashboard to Home");
                    loadFragment(new HomeFragment(), false);
                    showLogoHeader();
                    loadNormalMenu();
                    setBottomNavSelected(navHome);
                } else {
                    Log.d("MainActivity", "Finishing activity");
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {
            drawerLayout.closeDrawer(drawerContainer);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "Activity destroyed");
    }
}