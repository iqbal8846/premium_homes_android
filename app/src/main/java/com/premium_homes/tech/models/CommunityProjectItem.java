package com.premium_homes.tech.models;

public class CommunityProjectItem {
    private final String id;
    private final String imageURL;
    private final String title;
    private final String location;
    private final String bedrooms;
    private final boolean isOnSale;
    private final String footerText;
    private final String community;
    // Add this field

    // Update constructor to include community
    public CommunityProjectItem(String id, String imageURL, String title, String location,
                                String bedrooms, boolean isOnSale, String footerText, String community) {
        this.id = id;
        this.imageURL = imageURL;
        this.title = title;
        this.location = location;
        this.bedrooms = bedrooms;
        this.isOnSale = isOnSale;
        this.footerText = footerText;
        this.community = community;
    }

    // Complete getters
    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public boolean isOnSale() {
        return isOnSale;
    }

    public String getFooterText() {
        return footerText;
    }

    // Add this getter method
    public String getCommunity() {
        return community;
    }
}