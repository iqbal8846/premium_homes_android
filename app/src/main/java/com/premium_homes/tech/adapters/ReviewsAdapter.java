package com.premium_homes.tech.adapters;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import com.premium_homes.tech.R;
import com.premium_homes.tech.models.ReviewModel;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.VH> {

    private final List<ReviewModel> list;
    private final LifecycleOwner lifecycleOwner;

    public ReviewsAdapter(LifecycleOwner lifecycleOwner, List<ReviewModel> list) {
        this.lifecycleOwner = lifecycleOwner;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_fragment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {

        ReviewModel m = list.get(position);

        h.tvQuote.setText(m.getTitle());
        h.tvName.setText(m.getName());
        h.tvRole.setText(m.getRole());
        h.tvDate.setText(m.getDate());

        h.progressBar.setVisibility(View.VISIBLE);

        lifecycleOwner.getLifecycle().addObserver(h.playerView);

        // Check if videoId is valid
        if (m.getVideoId() != null && !m.getVideoId().isEmpty()) {
            h.playerView.addYouTubePlayerListener(
                    new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            // Load video but don't autoplay to save data
                            youTubePlayer.cueVideo(m.getVideoId(), 0);
                        }

                        @Override
                        public void onStateChange(
                                @NonNull YouTubePlayer youTubePlayer,
                                @NonNull PlayerConstants.PlayerState state) {

                            if (state == PlayerConstants.PlayerState.PLAYING ||
                                    state == PlayerConstants.PlayerState.VIDEO_CUED) {
                                h.progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
            );
        } else {
            // Hide player and show error if no video
            h.playerView.setVisibility(View.GONE);
            h.progressBar.setVisibility(View.GONE);
            Toast.makeText(h.itemView.getContext(),
                    "Video not available for this review", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        holder.playerView.release();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        YouTubePlayerView playerView;
        ProgressBar progressBar;
        TextView tvQuote, tvName, tvRole, tvDate;

        VH(View v) {
            super(v);
            playerView = v.findViewById(R.id.youtubePlayerView);
            progressBar = v.findViewById(R.id.progressBar);
            tvQuote = v.findViewById(R.id.tv_review_quote);
            tvName = v.findViewById(R.id.tv_user_name);
            tvRole = v.findViewById(R.id.tv_user_title);
            tvDate = v.findViewById(R.id.tv_date);
        }
    }
}