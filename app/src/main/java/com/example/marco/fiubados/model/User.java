package com.example.marco.fiubados.model;

/**
 * Created by Marco on 07/04/2015.
 */
public class User {

    private String name;
    private String friendshipStatus;

    public User(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
}
