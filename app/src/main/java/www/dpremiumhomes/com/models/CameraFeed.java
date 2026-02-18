package www.dpremiumhomes.com.models;

public class CameraFeed {
    private int id;
    private String name;
    private String location;
    private String propertyName;
    private String url;
    private String type;
    private boolean isLive;

    public CameraFeed(int id, String name, String location, String propertyName, String url, String type) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.propertyName = propertyName;
        this.url = url;
        this.type = type;
        this.isLive = true; // Default to live
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getPropertyName() { return propertyName; }
    public String getUrl() { return url; }
    public String getType() { return type; }
    public boolean isLive() { return isLive; }

    public void setLive(boolean live) { isLive = live; }
}