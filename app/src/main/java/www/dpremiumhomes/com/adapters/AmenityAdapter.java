package www.dpremiumhomes.com.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.AmenityItem;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder> {

    private List<AmenityItem> amenityList;

    public AmenityAdapter(List<AmenityItem> amenityList) {
        this.amenityList = amenityList;
    }

    @NonNull
    @Override
    public AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.amnity_item, parent, false);
        return new AmenityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityViewHolder holder, int position) {
        AmenityItem amenity = amenityList.get(position);

        holder.amenityName.setText(amenity.getName());

        // Load image using Picasso - note the API uses "img" field
        String imageUrl = amenity.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .fit()
                    .centerCrop()
                    .into(holder.amenityIcon);
        } else {
            // Set default icon if no image URL
            holder.amenityIcon.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return amenityList != null ? amenityList.size() : 0;
    }

    public void updateList(List<AmenityItem> newList) {
        amenityList = newList;
        notifyDataSetChanged();
    }

    static class AmenityViewHolder extends RecyclerView.ViewHolder {
        ImageView amenityIcon;
        TextView amenityName;

        AmenityViewHolder(@NonNull View itemView) {
            super(itemView);
            amenityIcon = itemView.findViewById(R.id.amenityIcon);
            amenityName = itemView.findViewById(R.id.amenityName);
        }
    }
}