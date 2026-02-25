package www.dpremiumhomes.com;

import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import www.dpremiumhomes.com.adapters.CctvAdapter;
import www.dpremiumhomes.com.models.CameraFeed;

public class CctvLiveActivity extends AppCompatActivity implements CctvAdapter.OnCameraClickListener {

    private RecyclerView recyclerView;
    private CctvAdapter adapter;
    private List<CameraFeed> cameraFeeds;

    //Main display views
    private WebView mainWebView;
    private ProgressBar mainLoadingIndicator;
    private TextView mainTvError, mainTvLive;
    private TextView tvPropertyName, tvCameraName, tvLocation;
    private ImageView ivInfoButton; // Info button for when URL fails

    private CameraFeed selectedCamera;
    private Handler handler = new Handler();
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv_live);

        initViews();
        setupData();
        setupRecyclerView();

        // Select first camera by default
        if (!cameraFeeds.isEmpty()) {
            selectCamera(cameraFeeds.get(0));
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.cctvRecycler);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Main display views
        mainWebView = findViewById(R.id.mainWebView);
        mainLoadingIndicator = findViewById(R.id.mainLoadingIndicator);
        mainTvError = findViewById(R.id.mainTvError);
        mainTvLive = findViewById(R.id.mainTvLive);

        // Camera info views
        tvPropertyName = findViewById(R.id.tvPropertyName);
        tvCameraName = findViewById(R.id.tvCameraName);
        tvLocation = findViewById(R.id.tvLocation);

        // Info button (hidden by default)
        ivInfoButton = findViewById(R.id.ivInfoButton);
        ivInfoButton.setOnClickListener(v -> {
            Toast.makeText(this, "Camera information: " + selectedCamera.getName(), Toast.LENGTH_LONG).show();
            // You can show a dialog with more details here
        });

        setupWebView(mainWebView);
    }

    private void setupWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide loading when page is loaded
                if (webView == mainWebView) {
                    mainLoadingIndicator.setVisibility(ProgressBar.GONE);
                    mainTvLive.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (webView == mainWebView) {
                    // Show error and info button
                    mainLoadingIndicator.setVisibility(ProgressBar.GONE);
                    mainTvError.setVisibility(TextView.VISIBLE);
                    mainTvError.setText("Stream not available");
                    ivInfoButton.setVisibility(ImageView.VISIBLE);
                    mainTvLive.setVisibility(TextView.GONE);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    if (webView == mainWebView) {
                        mainLoadingIndicator.setVisibility(ProgressBar.GONE);
                        mainTvLive.setVisibility(TextView.VISIBLE);
                    }
                }
            }
        });
    }

    private void setupData() {
        cameraFeeds = new ArrayList<>();

        // Add camera feeds from your provided array
        cameraFeeds.add(new CameraFeed(1,
                "CAM 01 - Main Entrance",
                "Property 15 - Flat 1A/1B",
                "The Premium Green Valley",
                "https://thepremiumhomesltd.com/tphl/hls/The%20Premium%20Green%20Valley/cam01.m3u8",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(2,
                "CAM 02 - Garden View",
                "Property 15 - Flat 1A/1B",
                "The Premium Green Valley",
                "https://thepremiumhomesltd.com/tphl/hls/The%20Premium%20Green%20Valley/cam02.m3u8",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(3,
                "CAM 01 - Lobby",
                "Property 8 - Flat 1B/4B",
                "The Premium Glory Garden",
                "https://thepremiumhomesltd.com/tphl/hls/The%20Premium%20Glory%20Garden/cam01.m3u8",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(4,
                "CAM 02 - Parking",
                "Property 8 - Flat 1B/4B",
                "The Premium Glory Garden",
                "https://thepremiumhomesltd.com/tphl/hls/The%20Premium%20Glory%20Garden/cam02.m3u8",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(5,
                "CAM 01 - Entrance",
                "Property 17 - Flat 1A",
                "The Harmony Residence",
                "https://thepremiumhomesltd.com/tphl/hls/The%20Premium%20Harmony%20Residence/cam01.m3u8",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(6,
                "CAM 02 - Rooftop",
                "Property 17 - Flat 1A",
                "The Harmony Residence",
                "https://thepremiumhomesltd.com/tphl/hls/The%20Premium%20Harmony%20Residence/cam02.m3u8",
                "rtsp"));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CctvAdapter(cameraFeeds);
        adapter.setOnCameraClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void selectCamera(CameraFeed cameraFeed) {
        this.selectedCamera = cameraFeed;

        // Update info section
        tvPropertyName.setText(cameraFeed.getPropertyName());
        tvCameraName.setText(cameraFeed.getName());
        tvLocation.setText(cameraFeed.getLocation());

        // Reset UI state
        mainLoadingIndicator.setVisibility(ProgressBar.VISIBLE);
        mainTvError.setVisibility(TextView.GONE);
        mainTvLive.setVisibility(TextView.GONE);
        ivInfoButton.setVisibility(ImageView.GONE);

        // Cancel any existing timeout
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }

        // Set timeout for loading (10 seconds)
        timeoutRunnable = () -> {
            // If still loading after 10 seconds, show error
            if (mainLoadingIndicator.getVisibility() == ProgressBar.VISIBLE) {
                mainLoadingIndicator.setVisibility(ProgressBar.GONE);
                mainTvError.setVisibility(TextView.VISIBLE);
                mainTvError.setText("Stream timeout - Click info for details");
                ivInfoButton.setVisibility(ImageView.VISIBLE);
            }
        };
        handler.postDelayed(timeoutRunnable, 10000);

        // Load stream in main WebView
        loadStreamInMainWebView(cameraFeed.getStreamUrl());

        // Update adapter to show selection
        if (adapter != null) {
            adapter.setSelectedCameraId(cameraFeed.getId());
        }
    }

    private void loadStreamInMainWebView(String streamUrl) {
        // For HLS streams, use video.js or similar
        String html = "<!DOCTYPE html><html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<link href='https://vjs.zencdn.net/7.20.3/video-js.css' rel='stylesheet' />" +
                "<script src='https://vjs.zencdn.net/7.20.3/video.min.js'></script>" +
                "<style>body { margin:0; padding:0; background:black; } " +
                ".video-js { width:100%; height:100%; position:absolute; }</style>" +
                "</head><body>" +
                "<video id='my-video' class='video-js vjs-default-skin' controls preload='auto' " +
                "width='100%' height='100%' autoplay>" +
                "<source src='" + streamUrl + "' type='application/x-mpegURL'></video>" +
                "<script>videojs('my-video').ready(function() { this.play(); });</script>" +
                "</body></html>";

        mainWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    @Override
    public void onCameraClick(CameraFeed cameraFeed) {
        // Select and display this camera in main view
        selectCamera(cameraFeed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler
        if (handler != null && timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }
        // Clean up WebViews to prevent memory leaks
        if (mainWebView != null) {
            mainWebView.loadUrl("about:blank");
            mainWebView.destroy();
        }
    }
}