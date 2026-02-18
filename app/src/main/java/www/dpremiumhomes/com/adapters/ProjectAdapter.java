package www.dpremiumhomes.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.ProjectModel;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    // Define the interface
    public interface OnProjectClickListener {
        void onProjectClick(ProjectModel project);
    }

    private final Context context;
    private List<ProjectModel> list;
    private final OnProjectClickListener listener;

    // Constructor with listener
    public ProjectAdapter(Context context, List<ProjectModel> list, OnProjectClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    // Old constructor for backward compatibility (keep this if needed elsewhere)
    public ProjectAdapter(Context context, List<ProjectModel> list) {
        this(context, list, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectModel model = list.get(position);
        holder.txtName.setText(model.name);
        holder.txtLocation.setText(model.location);

        // Load image with Glide
        Glide.with(context)
                .load(model.imageUrl)
                .placeholder(R.drawable.sample_image)
                .into(holder.imgProject);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProjectClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProject;
        TextView txtName, txtLocation;

        ViewHolder(View itemView) {
            super(itemView);
            imgProject = itemView.findViewById(R.id.imgProject);
            txtName = itemView.findViewById(R.id.txtName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
        }
    }

    public void updateList(List<ProjectModel> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }
}