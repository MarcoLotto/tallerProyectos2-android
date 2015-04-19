package com.example.marco.fiubados.model;

/**
 * Created by Marco on 07/04/2015.
 *
 * Representa a un usuario del sistema, no necesariamente tiene que ser un amigo
 */
public class User extends DatabaseObject{

    public static final String FRIENDSHIP_STATUS_FRIEND = "F";    // Es amigo
    public static final String FRIENDSHIP_STATUS_WAITING = "W";   // Espera confirmacion
    public static final String FRIENDSHIP_STATUS_REQUESTED = "R"; // Fue preguntado para amigo
    public static final String FRIENDSHIP_STATUS_UNKNOWN = "U";   // No es amigo

    private String name;
    private String friendshipStatus;
    private String friendshipRequestId;  // REVIEW: Ya se que esto no va pero son las 5:30 de la mañana

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

    // REVIEW: Sacar de acá, esto no va
    public String getFriendshipRequestId() {
        return friendshipRequestId;
    }

    public void setFriendshipRequestId(String friendshipRequestId) {
        this.friendshipRequestId = friendshipRequestId;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        return ((User) o).getId().equals(this.getId());
    }
}
