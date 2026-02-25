package www.dpremiumhomes.com.models;

public class ReviewModel {
    private String videoId;
    private String title;
    private String name;
    private String role;
    private String date;

    public ReviewModel(String videoId, String title, String name, String role, String date) {
        this.videoId = videoId;
        this.title = title;
        this.name = name;
        this.role = role;
        this.date = date;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getDate() {
        return date;
    }
}