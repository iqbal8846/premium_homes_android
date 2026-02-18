package www.dpremiumhomes.com.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.ReviewModel;

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

        h.tvQuote.setText(m.title);
        h.tvName.setText(m.name);
        h.tvRole.setText(m.role);
        h.tvDate.setText(m.date);

        h.progressBar.setVisibility(View.VISIBLE);

        lifecycleOwner.getLifecycle().addObserver(h.playerView);

        h.playerView.addYouTubePlayerListener(
                new AbstractYouTubePlayerListener() {

                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        // 🔥 AUTOPLAY
                        youTubePlayer.loadVideo(m.videoId, 0);
                    }

                    @Override
                    public void onStateChange(
                            @NonNull YouTubePlayer youTubePlayer,
                            @NonNull PlayerConstants.PlayerState state) {

                        if (state == PlayerConstants.PlayerState.PLAYING) {
                            h.progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
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
