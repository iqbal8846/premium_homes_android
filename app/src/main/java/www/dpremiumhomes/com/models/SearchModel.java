package www.dpremiumhomes.com.models;

import java.util.List;

public class SearchModel {
    private int id;
    private String name;
    private String location;
    private String types;
    private String image;
    private String tag;
    private String community;
    private String priceRange;
    private List<String> bathrooms;
    private List<String> flatSizes;
    private List<String> balconies;
    private List<String> filterLocations;
    private String fullLocation;

    public SearchModel(int id, String name, String location, String types, String image, String tag, String community, String priceRange, List<String> bathrooms, List<String> flatSizes, List<String> balconies, List<String> filterLocations, String fullLocation) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.types = types;
        this.image = image;
        this.tag = tag;
        this.community = community;
        this.priceRange = priceRange;
        this.bathrooms = bathrooms;
        this.flatSizes = flatSizes;
        this.balconies = balconies;
        this.filterLocations = filterLocations;
        this.fullLocation = fullLocation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTypes() { return types; }
    public void setTypes(String types) { this.types = types; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getCommunity() { return community; }
    public void setCommunity(String community) { this.community = community; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public List<String> getBathrooms() { return bathrooms; }
    public void setBathrooms(List<String> bathrooms) { this.bathrooms = bathrooms; }

    public List<String> getFlatSizes() { return flatSizes; }
    public void setFlatSizes(List<String> flatSizes) { this.flatSizes = flatSizes; }

    public List<String> getBalconies() { return balconies; }
    public void setBalconies(List<String> balconies) { this.balconies = balconies; }

    public List<String> getFilterLocations() { return filterLocations; }
    public void setFilterLocations(List<String> filterLocations) { this.filterLocations = filterLocations; }

    public String getFullLocation() { return fullLocation; }
    public void setFullLocation(String fullLocation) { this.fullLocation = fullLocation; }
}