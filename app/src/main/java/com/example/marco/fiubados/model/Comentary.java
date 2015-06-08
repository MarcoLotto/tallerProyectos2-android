package com.example.marco.fiubados.model;

/**
 * Created by Marco on 23/05/2015.
 */
public class Comentary extends WallPost {

    private User author;
    private String imageUrl;

    public Comentary(String id, User author, String message, String date, String imageUrl) {
        super(id, message, date, WallPostType.COMMENT);
        this.author = author;
        this.imageUrl = imageUrl;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
