package com.premium_homes.tech.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.premium_homes.tech.R;
import com.premium_homes.tech.models.CameraFeed;

public class CctvAdapter extends RecyclerView.Adapter<CctvAdapter.ViewHolder> {

    private List<CameraFeed> cameraFeeds;
    private OnCameraClickListener listener;
    private int selectedCameraId = -1;

    public interface OnCameraClickListener {
        void onCameraClick(CameraFeed cameraFeed);
    }

    public CctvAdapter(List<CameraFeed> cameraFeeds) {
        this.cameraFeeds = cameraFeeds;
    }

    public void setOnCameraClickListener(OnCameraClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedCameraId(int cameraId) {
        this.selectedCameraId = cameraId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cctv_camera, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CameraFeed feed = cameraFeeds.get(position);

        // Set camera name only
        holder.tvCameraName.setText(feed.getName());

        // Show selection indicator if this camera is selected
        if (feed.getId() == selectedCameraId) {
            holder.selectionIndicator.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.selectionIndicator.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.7f);
        }

        // Setup WebView for preview
        setupWebView(holder.webView, holder.loadingIndicator, holder.tvError, feed.getStreamUrl());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCameraClick(feed);
            }
        });
    }

    private void setupWebView(WebView webView, ProgressBar loadingIndicator, TextView tvError, String streamUrl) {
        loadingIndicator.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);

        String html = "<!DOCTYPE html><html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<link href='https://vjs.zencdn.net/7.20.3/video-js.css' rel='stylesheet' />" +
                "<script src='https://vjs.zencdn.net/7.20.3/video.min.js'></script>" +
                "</head><body style='margin:0; padding:0; background:black;'>" +
                "<video id='my-video' class='video-js vjs-default-skin' preload='auto' " +
                "width='100%' height='100%' style='position:absolute; top:0; left:0;' autoplay muted>" +
                "<source src='" + streamUrl + "' type='application/x-mpegURL'></video>" +
                "<script>videojs('my-video').ready(function() { this.play(); });</script>" +
                "</body></html>";

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadingIndicator.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    @Override
    public int getItemCount() {
        return cameraFeeds.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        WebView webView;
        ProgressBar loadingIndicator;
        TextView tvLive, tvError, tvCameraName;
        View selectionIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webView);
            loadingIndicator = itemView.findViewById(R.id.loadingIndicator);
            tvLive = itemView.findViewById(R.id.tvLive);
            tvError = itemView.findViewById(R.id.tvError);
            tvCameraName = itemView.findViewById(R.id.tvCameraName);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);
        }
    }
}