package www.dpremiumhomes.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.CameraFeed;

public class CctvAdapter extends RecyclerView.Adapter<CctvAdapter.CctvViewHolder> {

    private List<CameraFeed> cameraFeeds;
    private Context context;
    private OnCameraClickListener listener;

    public interface OnCameraClickListener {
        void onCameraClick(CameraFeed cameraFeed);
        void onFullscreenClick(CameraFeed cameraFeed);
    }

    public CctvAdapter(List<CameraFeed> cameraFeeds) {
        this.cameraFeeds = cameraFeeds;
    }

    public void setOnCameraClickListener(OnCameraClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CctvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_cctv_camera, parent, false);
        return new CctvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CctvViewHolder holder, int position) {
        CameraFeed camera = cameraFeeds.get(position);

        // Set camera information
        holder.tvCameraName.setText(camera.getName());
        holder.tvLocation.setText(camera.getLocation());
        holder.tvPropertyName.setText(camera.getPropertyName());

        // Update LIVE badge visibility
        holder.tvLive.setVisibility(camera.isLive() ? View.VISIBLE : View.GONE);

        // Setup WebView for RTSP stream
        setupWebView(holder.webView, holder.loadingIndicator, holder.tvError, camera.getUrl());

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCameraClick(camera);
            }
        });

        holder.btnFullscreen.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFullscreenClick(camera);
            }
        });
    }

    private void setupWebView(WebView webView, ProgressBar loadingIndicator, TextView tvError, String streamUrl) {
        // Enable JavaScript (required for RTSP.me player)
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Set WebView clients
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        // Load the RTSP embed URL
        String html = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' src='" + streamUrl +
                "' frameborder='0' allowfullscreen></iframe></body></html>";

        webView.loadDataWithBaseURL("https://rtsp.me", html, "text/html", "UTF-8", null);
    }

    @Override
    public int getItemCount() {
        return cameraFeeds.size();
    }

    static class CctvViewHolder extends RecyclerView.ViewHolder {
        WebView webView;
        ProgressBar loadingIndicator;
        TextView tvError;
        TextView tvLive;
        TextView tvCameraName;
        TextView tvLocation;
        TextView tvPropertyName;
        View btnFullscreen;

        public CctvViewHolder(@NonNull View itemView) {
            super(itemView);

            webView = itemView.findViewById(R.id.webView);
            loadingIndicator = itemView.findViewById(R.id.loadingIndicator);
            tvError = itemView.findViewById(R.id.tvError);
            tvLive = itemView.findViewById(R.id.tvLive);
            tvCameraName = itemView.findViewById(R.id.tvCameraName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPropertyName = itemView.findViewById(R.id.tvPropertyName);
            btnFullscreen = itemView.findViewById(R.id.btnFullscreen);
        }
    }
}