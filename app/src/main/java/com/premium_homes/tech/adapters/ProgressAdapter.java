package com.premium_homes.tech.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.premium_homes.tech.R;
import com.premium_homes.tech.CctvLiveActivity;
import com.premium_homes.tech.models.ProgressItem;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {

    private Context context;
    private List<ProgressItem> itemList;

    public ProgressAdapter(Context context, List<ProgressItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_progress_projects_live, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProgressItem item = itemList.get(position);

        holder.title.setText(item.title);
        holder.subTitle.setText(item.subTitle);
        holder.imageView.setImageResource(item.imageResId);

        holder.itemView.setOnClickListener(v -> {
            // 👉 Go to CCTV Page
            Intent intent = new Intent(context, CctvLiveActivity.class);
            intent.putExtra("title", item.title);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, subTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }
}
