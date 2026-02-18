package www.dpremiumhomes.com.adapters;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PartnersAdapter extends RecyclerView.Adapter<PartnersAdapter.VH> {

    private final int[] images;

    public PartnersAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());

        int size = (int) (140 * parent.getContext().getResources().getDisplayMetrics().density);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(12, 12, 12, 12);

        return new VH(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imageView;

        VH(@NonNull ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
}
