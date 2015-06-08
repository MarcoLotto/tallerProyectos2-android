package com.example.marco.fiubados.model;

/**
 * Created by Marco on 23/05/2015.
 */
public class Comentary extends DatabaseObject {

    private User author;
    private String message;
    private String date;
    private String imageUrl;

    public Comentary(String id, User author, String message, String date, String imageUrl) {
        super(id);
        this.author = author;
        this.message = message;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
