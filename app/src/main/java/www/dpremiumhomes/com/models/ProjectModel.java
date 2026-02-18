package www.dpremiumhomes.com.models;

public class ProjectModel {
    public String id; // Add this
    public String imageUrl;
    public String name;
    public String location;

    public ProjectModel(String id, String imageUrl, String name, String location) { // Update constructor
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.location = location;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}