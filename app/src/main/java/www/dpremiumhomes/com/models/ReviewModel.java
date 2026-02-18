package www.dpremiumhomes.com.models;

public class ReviewModel {

    public String videoId;
    public String title;
    public String name;
    public String role;
    public String date;

    public ReviewModel(String videoId, String title, String name, String role, String date) {
        this.videoId = videoId;
        this.title = title;
        this.name = name;
        this.role = role;
        this.date = date;
    }
}
