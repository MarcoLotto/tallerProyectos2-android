package com.example.marco.fiubados.model;

public enum WallPostType {
    COMMENT("comment"),
    NOTIFICATION("notification");

    private final String type;

    WallPostType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
