package www.dpremiumhomes.com;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.dpremiumhomes.com.models.BlogItem;

public class BlogDetailActivity extends AppCompatActivity {

    private LinearLayout loadingContainer, errorContainer, blogContainer;
    private TextView tvTitle, tvAuthor, tvDate, tvContent, tvCategory, tvComments, tvReadTime, errorText;
    private ImageView ivFeaturedImage, btnBack;
    private Button btnRetry;

    private int blogId;
    private RequestQueue requestQueue;
    private final String BLOGS_API_URL = "https://premium-api.dvalleybd.com/blogs.php?action=get-all-blogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        setStatusBarColor();

        // Get blog ID from intent
        blogId = getIntent().getIntExtra("id", -1);
        if (blogId == -1) {
            finish();
            return;
        }

        // Initialize all views
        initViews();

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());
        btnRetry.setOnClickListener(v -> loadBlogData());

        // Initialize Volley and load data
        requestQueue = Volley.newRequestQueue(this);
        loadBlogData();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(this, R.color.primary)
            );
        }
    }

    private void initViews() {
        loadingContainer = findViewById(R.id.loadingContainer);
        errorContainer = findViewById(R.id.errorContainer);
        blogContainer = findViewById(R.id.blogContainer);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDate = findViewById(R.id.tvDate);
        tvContent = findViewById(R.id.tvContent);
        tvCategory = findViewById(R.id.tvCategory);
        tvComments = findViewById(R.id.tvComments);
        tvReadTime = findViewById(R.id.tvReadTime);
        errorText = findViewById(R.id.errorText);

        ivFeaturedImage = findViewById(R.id.ivFeaturedImage);
        btnBack = findViewById(R.id.btnBack);
        btnRetry = findViewById(R.id.btnRetry);
    }

    private void loadBlogData() {
        showLoading();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                BLOGS_API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray blogsArray = response.getJSONArray("blogs");
                                BlogItem blog = findBlogById(blogsArray);

                                if (blog != null) {
                                    setBlogData(blog);
                                    showContent();
                                } else {
                                    showError("Blog not found");
                                }
                            } else {
                                showError(response.getString("message"));
                            }
                        } catch (Exception e) {
                            showError("Error loading data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showError("Network error");
                    }
                }
        );

        requestQueue.add(request);
    }

    private BlogItem findBlogById(JSONArray blogsArray) throws JSONException {
        for (int i = 0; i < blogsArray.length(); i++) {
            JSONObject blogJson = blogsArray.getJSONObject(i);
            int id = blogJson.getInt("id");

            if (id == blogId) {
                // Create BlogItem from JSON
                BlogItem blog = new BlogItem();
                blog.setId(id);
                blog.setTitle(blogJson.getString("title"));
                blog.setExcerpt(blogJson.getString("excerpt"));
                blog.setImage(blogJson.getString("image"));
                blog.setAuthor(blogJson.getString("author"));
                blog.setDate(blogJson.getString("date"));

                // Try to get comments and readTime if they exist
                try {
                    blog.setComments(blogJson.getInt("comments"));
                } catch (Exception e) {
                    blog.setComments(0); // Default to 0 if not found
                }

                try {
                    blog.setReadTime(blogJson.getString("readTime"));
                } catch (Exception e) {
                    blog.setReadTime("5 min read"); // Default
                }

                return blog;
            }
        }
        return null;
    }

    private void setBlogData(BlogItem blog) {
        // Set title
        tvTitle.setText(blog.getTitle());

        // Set author
        tvAuthor.setText(blog.getAuthor());

        // Set date
        tvDate.setText(blog.getDate());

        // Set content (using excerpt)
        String excerpt = blog.getExcerpt();
        tvContent.setText(Html.fromHtml(excerpt));

        // Set comments
        int comments = blog.getComments();
        tvComments.setText(comments + " Comments");

        // Set read time
        tvReadTime.setText(blog.getReadTime());

        // Set category (simple logic)
        String title = blog.getTitle().toLowerCase();
        if (title.contains("tedx") || title.contains("partner")) {
            tvCategory.setText("Partnership");
        } else if (title.contains("চবি") || title.contains("বিশ্ববিদ্যালয়")) {
            tvCategory.setText("শিক্ষা");
        } else if (title.contains("বরিশাল")) {
            tvCategory.setText("ইভেন্ট");
        } else if (title.contains("প্রকল্প") || title.contains("project")) {
            tvCategory.setText("নতুন প্রকল্প");
        } else {
            tvCategory.setText("Real Estate");
        }

        // Load image
        String imageUrl = blog.getImage();
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.about_cover)
                .into(ivFeaturedImage);
    }

    private void showLoading() {
        loadingContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        blogContainer.setVisibility(View.GONE);
    }

    private void showError(String message) {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        blogContainer.setVisibility(View.GONE);
        errorText.setText(message);
    }

    private void showContent() {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        blogContainer.setVisibility(View.VISIBLE);
    }
}