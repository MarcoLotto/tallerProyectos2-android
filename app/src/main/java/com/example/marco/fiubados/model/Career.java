package com.example.marco.fiubados.model;

/**
 * Created by Marco on 03/05/2015.
 */
public class Career extends DatabaseObject {

    private String name;

    public Career(String id) {
        super(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
