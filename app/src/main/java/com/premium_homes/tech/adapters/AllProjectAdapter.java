package com.premium_homes.tech.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.premium_homes.tech.R;
import com.premium_homes.tech.models.AllProjectModel;
import com.premium_homes.tech.PropertyViewActivity;  // Your detail activity

import java.util.List;

public class AllProjectAdapter extends RecyclerView.Adapter<AllProjectAdapter.AllProjectViewHolder> {

    private List<AllProjectModel> allProjectList;
    private Context context;

    public AllProjectAdapter(Context context, List<AllProjectModel> allProjectList) {
        this.context = context;
        this.allProjectList = allProjectList;
    }

    @NonNull
    @Override
    public AllProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new AllProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProjectViewHolder holder, int position) {
        AllProjectModel project = allProjectList.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return allProjectList.size();
    }

    class AllProjectViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBuilding;
        CardView tagCard;
        TextView tvTag;
        TextView textTitle;
        TextView textLocation;
        TextView textBedrooms;
        TextView textFooter;

        AllProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBuilding = itemView.findViewById(R.id.image_building);
            tagCard = itemView.findViewById(R.id.tagCard);        // Updated ID
            tvTag = itemView.findViewById(R.id.tag_on_sale);            // Updated ID
            textTitle = itemView.findViewById(R.id.text_title);
            textLocation = itemView.findViewById(R.id.text_location);
            textBedrooms = itemView.findViewById(R.id.text_bedrooms);
            textFooter = itemView.findViewById(R.id.text_footer);


        }

        void bind(AllProjectModel project) {
            Glide.with(itemView.getContext())
                    .load(project.getImageUrl())
                    .placeholder(R.drawable.sample_image)
                    .error(R.drawable.about_sample)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(imageBuilding);

            textTitle.setText(project.getTitle());
            textLocation.setText(project.getLocation());
            textBedrooms.setText(project.getBedrooms());
            textFooter.setText(project.getFooter());

            if (project.hasTag()) {
                tagCard.setVisibility(View.VISIBLE);
                tvTag.setText(project.getTag());

                if (project.isSoldOut()) {
                    tvTag.setBackgroundColor(
                            itemView.getContext().getResources().getColor(android.R.color.holo_red_dark)
                    );
                } else {
                    tvTag.setBackgroundColor(
                            itemView.getContext().getResources().getColor(R.color.primaryDark)
                    );
                }
            } else {
                tagCard.setVisibility(View.GONE);
            }
            // Set click listener on the entire card
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    AllProjectModel clickedProject = allProjectList.get(position);

                    Intent intent = new Intent(context, PropertyViewActivity.class);
                    intent.putExtra("propertyId", clickedProject.getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}