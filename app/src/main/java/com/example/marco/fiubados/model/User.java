package com.example.marco.fiubados.model;

/**
 * Created by Marco on 07/04/2015.
 *
 * Representa a un usuario del sistema, no necesariamente tiene que ser un amigo
 */
public class User extends DatabaseObject{

    private String name;
    private String friendshipStatus;

    public User(String id, String name){
        super(id);
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
