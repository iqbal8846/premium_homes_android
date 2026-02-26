package com.premium_homes.tech.models;

public class VideoReviewModel {
    private String youtubeId;
    private String title;

    public VideoReviewModel(String youtubeId, String title) {
        this.youtubeId = youtubeId;
        this.title = title;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public String getTitle() {
        return title;
    }
}