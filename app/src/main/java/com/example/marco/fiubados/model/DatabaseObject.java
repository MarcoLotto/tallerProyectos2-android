package com.example.marco.fiubados.model;

/**
 * Created by Marco on 08/04/2015.
 *
 * Aquellos objetos que tienen un id en la base de datos
 */
public abstract class DatabaseObject {
    private String id;

    DatabaseObject(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
