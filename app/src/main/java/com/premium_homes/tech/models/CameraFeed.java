package com.premium_homes.tech.models;

public class CameraFeed {
    private int id;
    private String name;
    private String location;
    private String propertyName;
    private String streamUrl;
    private String streamType;

    // Constructor
    public CameraFeed(int id, String name, String location, String propertyName,
                      String streamUrl, String streamType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.propertyName = propertyName;
        this.streamUrl = streamUrl;
        this.streamType = streamType;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getStreamType() {
        return streamType;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }
}