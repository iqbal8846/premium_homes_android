package com.premium_homes.tech.models;

public class AllProjectModel {
    private String id;              // <-- Add this
    private String imageUrl;
    private String title;
    private String location;
    private String bedrooms;
    private String footer;
    private String tag;

    public AllProjectModel(String id, String imageUrl, String title, String location,
                           String bedrooms, String footer, String tag) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.location = location;
        this.bedrooms = bedrooms;
        this.footer = footer;
        this.tag = tag != null ? tag.trim() : "";
    }

    // Getters
    public String getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getBedrooms() { return bedrooms; }
    public String getFooter() { return footer; }
    public String getTag() { return tag; }

    public boolean isSoldOut() {
        return tag.toUpperCase().contains("SOLD OUT");
    }

    public boolean hasTag() {
        return !tag.isEmpty();
    }
}