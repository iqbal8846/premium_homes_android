package www.dpremiumhomes.com;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv_live);

        initViews();
        setupData();
        setupRecyclerView();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.cctvRecycler);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupData() {
        cameraFeeds = new ArrayList<>();

        // Add camera feeds from your array
        cameraFeeds.add(new CameraFeed(1,
                "The Premium Garden View - Camera 1",
                "Property 15 - Flat 1A/1B",
                "The Premium Garden View",
                "https://rtsp.me/embed/zkYhn9Hs/",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(2,
                "The Premium Garden View - Camera 2",
                "Property 15 - Flat 1A/1B",
                "The Premium Garden View",
                "https://rtsp.me/embed/Ek9efBY6/",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(3,
                "The Premium Garden - Camera 1",
                "Property 8 - Flat 1B/4B",
                "The Premium Glory Garden",
                "https://rtsp.me/embed/T3yy42rf/",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(4,
                "The Premium Garden - Camera 2",
                "Property 8 - Flat 1B/4B",
                "The Premium Glory Garden",
                "https://rtsp.me/embed/b5D5454Q/",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(5,
                "The Harmony Residence - Camera 1",
                "Property 17 - Flat 1A",
                "The Harmony Residence",
                "https://rtsp.me/embed/4BtSR2sB/",
                "rtsp"));

        cameraFeeds.add(new CameraFeed(6,
                "The Harmony Residence - Camera 2",
                "Property 17 - Flat 1A",
                "The Harmony Residence",
                "https://rtsp.me/embed/EEKRdE98/",
                "rtsp"));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CctvAdapter(cameraFeeds);
        adapter.setOnCameraClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCameraClick(CameraFeed cameraFeed) {
        // Handle camera item click
        Toast.makeText(this, "Selected: " + cameraFeed.getName(), Toast.LENGTH_SHORT).show();
        // You could open a detailed view or play stream in fullscreen
    }

    @Override
    public void onFullscreenClick(CameraFeed cameraFeed) {
        // Handle fullscreen button click
        Toast.makeText(this, "Opening fullscreen: " + cameraFeed.getName(), Toast.LENGTH_SHORT).show();
        // Open fullscreen activity or dialog
        openFullscreenPlayer(cameraFeed);
    }

    private void openFullscreenPlayer(CameraFeed cameraFeed) {
        // Implement fullscreen player activity/dialog
        // This would be a separate activity with just the WebView
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up WebViews to prevent memory leaks
        if (adapter != null) {
            // You might want to add cleanup logic in adapter
        }
    }
}