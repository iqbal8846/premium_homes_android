package www.dpremiumhomes.com.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

import www.dpremiumhomes.com.BlogDetailActivity;
import www.dpremiumhomes.com.CctvLiveActivity;
import www.dpremiumhomes.com.R;
import www.dpremiumhomes.com.models.BlogItem;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private Context context;
    private List<BlogItem> blogList;

    public BlogAdapter(Context context, List<BlogItem> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlogItem blog = blogList.get(position);

        // Set data to views
        holder.title.setText(blog.getTitle());
        holder.author.setText("By: " + blog.getAuthor());
        holder.date.setText(blog.getDate());

        // Load image
        if (blog.getImage() != null && !blog.getImage().isEmpty()) {
            Picasso.get()
                    .load(blog.getImage())
                    .placeholder(R.drawable.about_cover)
                    .error(R.drawable.about_cover)
                    .into(holder.image);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            // 👉 Go to CCTV Page
            Intent intent = new Intent(context, BlogDetailActivity.class);
            intent.putExtra("id", blog.getId());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public void updateList(List<BlogItem> newList) {
        blogList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, author, date;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.blogImage);
            title = itemView.findViewById(R.id.blogTitle);
            author = itemView.findViewById(R.id.blogAuthor);
            date = itemView.findViewById(R.id.blogDate);
        }
    }
}