package com.example.marco.fiubados.model;

public class WallPost extends DatabaseObject{

    private String message;
    private String date;
    private WallPostType type;

    public WallPost (String id, String message, String date, WallPostType type) {
        super(id);
        this.message = message;
        this.date = date;
        this.type = type;
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

    public WallPostType getType() {
        return type;
    }

    public void setType(WallPostType type) {
        this.type = type;
    }
}
