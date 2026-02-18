package www.dpremiumhomes.com.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.SearchModel;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Context context;
    private final List<SearchModel> list;
    private OnItemClickListener listener;

    // =====================
    // Constructor
    // =====================
    public SearchAdapter(Context context, List<SearchModel> list) {
        this.context = context;
        this.list = new ArrayList<>(list);
    }

    // =====================
    // ViewHolder Creation
    // =====================
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new ViewHolder(v);
    }

    // =====================
    // Bind Data
    // =====================
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        SearchModel s = list.get(position);

        h.title.setText(s.getName());
        h.location.setText(s.getLocation());

        // Build property details from extracted data
        String propertyDetails = buildPropertyDetails(s);
        h.bedrooms.setText(propertyDetails);

        h.footer.setText(s.getCommunity());

        // Price range display (if available in layout)
        if (h.priceRange != null && s.getPriceRange() != null && !s.getPriceRange().trim().isEmpty()) {
            h.priceRange.setText(s.getPriceRange());
            h.priceRange.setVisibility(View.VISIBLE);
        }

        // Tag visibility & styling
        if (s.getTag() == null || s.getTag().trim().isEmpty()) {
            h.tag.setVisibility(View.GONE);
        } else {
            h.tag.setText(s.getTag());
            h.tag.setVisibility(View.VISIBLE);

            if (s.getTag().equalsIgnoreCase("SOLD OUT!")) {
                h.tag.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.red)
                );
                h.tag.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else if (s.getTag().equalsIgnoreCase("ON SALE")) {
                h.tag.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.emerald_700)
                );
                h.tag.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                // default background for other tags
                h.tag.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.emerald_700)
                );
                h.tag.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        }

        // Image
        Glide.with(context)
                .load(s.getImage())
                .placeholder(R.drawable.premium_banner)
                .error(R.drawable.about_sample)
                .into(h.image);

        // Item click
        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(s);
            }
        });
    }

    // Helper method to build property details string from SearchModel
    private String buildPropertyDetails(SearchModel model) {
        List<String> details = new ArrayList<>();

        // Extract bedroom info from types field
        String types = model.getTypes();
        if (types != null) {
            String lowerTypes = types.toLowerCase();

            // Check for bedroom count
            if (lowerTypes.contains("studio")) {
                details.add("STUDIO");
            } else if (lowerTypes.contains("1 bedroom") || lowerTypes.contains("1 bed")) {
                details.add("1 BED");
            } else if (lowerTypes.contains("2 bedroom") || lowerTypes.contains("2 bed")) {
                details.add("2 BED");
            } else if (lowerTypes.contains("3 bedroom") || lowerTypes.contains("3 bed")) {
                details.add("3 BED");
            } else if (lowerTypes.contains("4 bedroom") || lowerTypes.contains("4 bed")) {
                details.add("4 BED");
            }
        }

        // Add bathroom count from filters
        if (model.getBathrooms() != null && !model.getBathrooms().isEmpty()) {
            details.add(model.getBathrooms().get(0) + " BATH");
        }

        // Add balcony count from filters
        if (model.getBalconies() != null && !model.getBalconies().isEmpty()) {
            details.add(model.getBalconies().get(0) + " BALCONY");
        }

        // Add flat size from filters if available
        if (model.getFlatSizes() != null && !model.getFlatSizes().isEmpty()) {
            String flatSize = model.getFlatSizes().get(0);
            if (!flatSize.isEmpty()) {
                details.add(flatSize + " SFT");
            }
        }

        // If no extracted details, show the original types field
        if (details.isEmpty() && types != null && !types.trim().isEmpty()) {
            return types;
        }

        return TextUtils.join(" • ", details);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // =====================
    // Update Adapter Data (USED FOR FILTERING)
    // =====================
    public void updateList(List<SearchModel> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    // =====================
    // Filter methods
    // =====================
    public void filterList(List<SearchModel> filteredList) {
        list.clear();
        list.addAll(filteredList);
        notifyDataSetChanged();
    }

    public List<SearchModel> getCurrentList() {
        return new ArrayList<>(list);
    }

    public void clearList() {
        list.clear();
        notifyDataSetChanged();
    }

    // =====================
    // Click Listener
    // =====================
    public interface OnItemClickListener {
        void onItemClick(SearchModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // =====================
    // ViewHolder
    // =====================
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, location, bedrooms, footer, tag, priceRange;

        ViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.image_building);
            title = v.findViewById(R.id.text_title);
            location = v.findViewById(R.id.text_location);
            bedrooms = v.findViewById(R.id.text_bedrooms);
            footer = v.findViewById(R.id.text_footer);
            tag = v.findViewById(R.id.tag_on_sale);

        }
    }
}