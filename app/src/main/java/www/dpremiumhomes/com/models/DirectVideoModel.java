package www.dpremiumhomes.com.models;


/**
public class DirectVideoModel {
    private String title;
    private String video_ID;

    public DirectVideoModel(String title, String video_ID) {
        this.title = title;
        this.video_ID = video_ID;
    }

    public String getTitle() {
        return title;
    }

    public String getVideo_ID() {
        return video_ID;
    }

}
 **/

public class DirectVideoModel {

    private String name;
    private String role;
    private String project;
    private String review;
    private String date;
    private int rating;
    private String videoUrl;

    public DirectVideoModel(String name, String role, String project,
                            String review, String date, int rating, String videoUrl) {
        this.name = name;
        this.role = role;
        this.project = project;
        this.review = review;
        this.date = date;
        this.rating = rating;
        this.videoUrl = videoUrl;
    }

    public String getName() { return name; }

    public String getRole() { return role; }

    public String getProject() { return project; }

    public String getReview() { return review; }

    public String getDate() { return date; }

    public int getRating() { return rating; }

    public String getVideoUrl() { return videoUrl; }


    // 🔥 Extract YouTube Video ID from URL
    public String getVideoId() {
        try {
            if (videoUrl.contains("embed/")) {
                String id = videoUrl.substring(videoUrl.indexOf("embed/") + 6);
                int end = id.indexOf("?");
                return end != -1 ? id.substring(0, end) : id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}