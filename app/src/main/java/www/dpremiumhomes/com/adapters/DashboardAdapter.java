package www.dpremiumhomes.com.adapters;

import android.annotation.SuppressLint;
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
import www.dpremiumhomes.com.models.CommunityProjectItem;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    // Click interface
    public interface OnDashboardClickListener {
        void onDashboardClick(CommunityProjectItem item);
    }

    private final List<CommunityProjectItem> mData;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final OnDashboardClickListener listener;

    // Constructor
    public DashboardAdapter(Context context,
                            List<CommunityProjectItem> data,
                            OnDashboardClickListener listener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.listener = listener;
    }

    // Optional constructor
    public DashboardAdapter(Context context, List<CommunityProjectItem> data) {
        this(context, data, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_project_lists, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityProjectItem item = mData.get(position);

        // Image
        Glide.with(mContext)
                .load(item.getImageURL())
                .placeholder(R.drawable.sample_image)
                .into(holder.imageBuilding);

        // Sale tag
        if (item.isOnSale()) {
            holder.tagOnSale.setVisibility(View.VISIBLE);
            holder.tagOnSale.setText("ON SALE");
            holder.tagOnSale.setBackground(
                    mContext.getResources().getDrawable(R.drawable.button_green_gradient)
            );
        } else {
            holder.tagOnSale.setVisibility(View.VISIBLE);
            holder.tagOnSale.setText("SOLD OUT");
            holder.tagOnSale.setBackground(
                    mContext.getResources().getDrawable(R.color.red)
            );
        }

        // Text data
        holder.textTitle.setText(item.getTitle());
        holder.textLocation.setText(item.getLocation());
        holder.textBedrooms.setText(item.getBedrooms());
        holder.textFooter.setText(item.getFooterText());

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDashboardClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    // Update data
    public void updateList(List<CommunityProjectItem> newList) {
        mData.clear();
        mData.addAll(newList);
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageBuilding;
        TextView tagOnSale;
        TextView textTitle;
        TextView textLocation;
        TextView textBedrooms;
        TextView textFooter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageBuilding = itemView.findViewById(R.id.image_building);
            tagOnSale = itemView.findViewById(R.id.tag_on_sale);
            textTitle = itemView.findViewById(R.id.text_title);
            textLocation = itemView.findViewById(R.id.text_location);
            textBedrooms = itemView.findViewById(R.id.text_bedrooms);
            textFooter = itemView.findViewById(R.id.text_footer);
        }
    }
}
