package com.example.marco.fiubados.model;

import android.location.Location;

import com.example.marco.fiubados.commons.FormatTranslator;

import java.util.ArrayList;
import java.util.Date;
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

    // User and Profile info
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String biography;
    private String nationality;
    private String city;
    private List<Education> educationInfo = new ArrayList<>();
    private List<Job> jobs = new ArrayList<>();
    private String padron;
    private Academic academicInfo = new Academic("");

    private Location location;
    private String lastTimeUpdate;

    // Friendiship Info
    private String friendshipStatus;
    private String friendshipRequestId;  // REVIEW: Ya se que esto no va pero son las 5:30 de la mañana
    private String matchParameter;

    /*
     * Constructors
     */

    public User(String id) {
        super(id);
    }

    public User(String id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /*
     * Overrides
     */

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        return ((User) o).getId().equals(this.getId());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLastTimeUpdate() {
        return this.lastTimeUpdate;
    }

    public void setLastTimeUpdate(String strDate) {
        this.lastTimeUpdate = FormatTranslator.adaptDate(strDate);
    }

    /*
     * First Name getter and setter
     */

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /*
     * Last Name getter and setter
     */

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /*
     * Full Name getter
     */

    public String getFullName() {
        String fullName = this.getFirstName() + " " + this.getLastName();
        return fullName;
    }

    /*
     * Email getter and setter
     */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*
     * Profile Picture getter and setter
     */

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /*
     * Biography getter and setter
     */

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    /*
     * Nationality getter and setter
     */

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    /*
     * City getter and setter
     */

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /*
     * Education Info getter and setter
     */

    public List<Education> getEducationInfo() {
        return educationInfo;
    }

    public void setEducationInfo(List<Education> educationInfo) {
        this.educationInfo = educationInfo;
    }

    /*
     * Job Info getter and setter
     */

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    /*
     * Padron getter and setter
     */

    public String getPadron() {
        return padron;
    }

    public void setPadron(String padron) {
        this.padron = padron;
    }

    /*
     * Academic Info getter and setter
     */

    public Academic getAcademicInfo() {
        return academicInfo;
    }

    public void setAcademicInfo(Academic academicInfo) {
        this.academicInfo = academicInfo;
    }

    /*
     * Friendship Status getter and setter
     */

    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    /*
     * Friendship Request ID getter and setter
     */

    // REVIEW: Sacar de acá, esto no va
    public String getFriendshipRequestId() {
        return friendshipRequestId;
    }

    public void setFriendshipRequestId(String friendshipRequestId) {
        this.friendshipRequestId = friendshipRequestId;
    }

    /*
     * Match Parameter getter and setter
     */

    public String getMatchParameter() {
        return matchParameter;
    }

    public void setMatchParameter(String matchParameter) {
        this.matchParameter = matchParameter;
    }
}
