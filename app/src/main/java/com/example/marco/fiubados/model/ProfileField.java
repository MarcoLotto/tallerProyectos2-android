package com.example.marco.fiubados.model;

/**
 * Created by Marco on 11/04/2015.
 */
public class ProfileField {

    private String name, value;

    public ProfileField(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
