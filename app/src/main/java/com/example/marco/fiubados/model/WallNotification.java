package com.example.marco.fiubados.model;

public class WallNotification extends WallPost {

    private String imageUrl;

    public WallNotification(String id, String message, String date, String imageUrl) {
        super(id, message, date, WallPostType.NOTIFICATION);
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
