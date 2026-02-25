package www.dpremiumhomes.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.FavoriteModel;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Context context;
    private List<FavoriteModel> list;
    private OnRemoveClick listener;

    public interface OnRemoveClick {
        void onRemove(String propertyId);
    }

    public FavoritesAdapter(Context context, List<FavoriteModel> list, OnRemoveClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, location, remove;
        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvPropertyTitle);
            price = itemView.findViewById(R.id.tvPrice);
            location = itemView.findViewById(R.id.tvLocation);
            remove = itemView.findViewById(R.id.btnRemove);
            image = itemView.findViewById(R.id.imgProperty);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavoriteModel model = list.get(position);

        holder.title.setText(model.title);
        holder.price.setText(model.price);
        holder.location.setText(model.location);
        String fullImageUrl = "https://premium-api.dvalleybd.com" + model.image;

        Glide.with(context)
                .load(fullImageUrl)
                .into(holder.image);

        holder.remove.setOnClickListener(v -> listener.onRemove(model.propertyId));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
