package com.example.marco.fiubados.model;

/**
 * Created by Marco on 24/04/2015.
 */
public class Academic extends DatabaseObject {

    private String career;

    Academic(String id) {
        super(id);
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }
}
