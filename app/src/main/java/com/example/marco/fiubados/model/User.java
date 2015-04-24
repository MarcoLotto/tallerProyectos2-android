package com.example.marco.fiubados.model;

import java.util.ArrayList;
import java.util.List;

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

    private String name, email;
    private String friendshipStatus;
    private String friendshipRequestId;  // REVIEW: Ya se que esto no va pero son las 5:30 de la mañana
    private String matchParameter;
    private String lastName;
    private String padron;
    private String biography;
    private String nationality;
    private String city;
    private List<Job> jobs = new ArrayList<Job>();
    private List<Academic> academicInfo = new ArrayList<Academic>();

    public User(String id, String name){
        super(id);
        this.name = name;
    }

    public User(String id, String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
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

    public void setMatchParameter(String matchParameter) {
        this.matchParameter = matchParameter;
    }

    public String getMatchParameter() {
        return matchParameter;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPadron(String padron) {
        this.padron = padron;
    }

    public String getPadron() {
        return padron;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBiography() {
        return biography;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Academic> getAcademicInfo() {
        return academicInfo;
    }

    public void setAcademicInfo(List<Academic> academicInfo) {
        this.academicInfo = academicInfo;
    }
}
