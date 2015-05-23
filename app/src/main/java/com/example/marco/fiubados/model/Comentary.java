package com.example.marco.fiubados.model;

/**
 * Created by Marco on 23/05/2015.
 */
public class Comentary extends DatabaseObject {

    private String author, message, imageUrl;

    public Comentary(String id, String author, String message, String imageUrl) {
        super(id);
        this.author = author;
        this.message = message;
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
