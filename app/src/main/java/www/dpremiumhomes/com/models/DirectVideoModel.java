package www.dpremiumhomes.com.models;

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