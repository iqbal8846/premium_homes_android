package www.dpremiumhomes.com.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.DirectVideoModel;

public class DirectVideoAdapter extends RecyclerView.Adapter<DirectVideoAdapter.ViewHolder> {

    private Context context;
    LifecycleOwner lifecycleOwner;
    private ArrayList<DirectVideoModel> list;
    //TextView tvTitle;

    public DirectVideoAdapter(Context context,LifecycleOwner lifecycleOwner, ArrayList<DirectVideoModel> list) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.video_review_item, parent, false);
        return new ViewHolder(view);
    }
/**
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DirectVideoModel video = list.get(position);

        //----Loading thumbnail to ImageView----
        String videoId = video.getVideo_ID();
        String maxRes = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        String hq = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
        String mq = "https://img.youtube.com/vi/" + videoId + "/mqdefault.jpg";
        String def = "https://img.youtube.com/vi/" + videoId + "/default.jpg";

        Glide.with(context)
                .load(maxRes)
                .error(
                        Glide.with(context)
                                .load(hq)
                                .error(
                                        Glide.with(context)
                                                .load(mq)
                                                .error(def)
                                )
                )
                .placeholder(R.drawable.premium_banner)
                .into(holder.ivThumbnail);


        holder.itemView.setOnClickListener(v -> {
            showVideoDialog(video.getVideo_ID(), video.getTitle());
        });
    }

    **/

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    DirectVideoModel video = list.get(position);

    // ✅ Text from API
    //holder.tvTitle.setText(video.getName());
    //holder.tvSubTitle.setText(video.getProject());

    // ✅ Extracted YouTube ID from full URL
    String videoId = video.getVideoId();

    // ✅ Thumbnail
    String maxRes = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
    String hq = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    String mq = "https://img.youtube.com/vi/" + videoId + "/mqdefault.jpg";
    String def = "https://img.youtube.com/vi/" + videoId + "/default.jpg";

    Glide.with(context)
            .load(maxRes)
            .error(
                    Glide.with(context)
                            .load(hq)
                            .error(
                                    Glide.with(context)
                                            .load(mq)
                                            .error(def)
                            )
            )
            .placeholder(R.drawable.premium_banner)
            .into(holder.ivThumbnail);

    holder.itemView.setOnClickListener(v ->
            showVideoDialog(videoId, video.getName())
    );
}

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail, ivPlay;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.videoThumbnail);
            ivPlay = itemView.findViewById(R.id.playButton);
            //tvTitle = itemView.findViewById(R.id.videoTitle);
        }
    }


    private void showVideoDialog(String videoId, String title) {

        // SHOW loader immediately
        ((MainActivity) context).showLoader();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_youtube_player, null);

        YouTubePlayerView youTubePlayerView =
                view.findViewById(R.id.youtubePlayerView);

        //tvTitle = view.findViewById(R.id.tvDialogTitle);
        ImageView btnClose = view.findViewById(R.id.btnClose);

        if (title != null && !title.isEmpty()) {
            //tvTitle.setText(title);
        }

        // IMPORTANT: attach lifecycle
        lifecycleOwner.getLifecycle().addObserver(youTubePlayerView);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        youTubePlayerView.addYouTubePlayerListener(
                new AbstractYouTubePlayerListener() {

                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0);
                    }

                    @Override
                    public void onStateChange(
                            @NonNull YouTubePlayer youTubePlayer,
                            @NonNull PlayerConstants.PlayerState state) {

                        // Hide loader ONLY when video is actually playing
                        if (state == PlayerConstants.PlayerState.PLAYING) {
                            ((MainActivity) context).hideLoader();
                        }
                    }
                }
        );

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnDismissListener(d -> {
            youTubePlayerView.release();
            ((MainActivity) context).hideLoader(); // safety
        });

        dialog.show();
    }

}