package com.example.marco.fiubados.model;

/**
 * Created by Marco on 11/04/2015.
 */
public class ProfileField {

    private String displayName, name, value;

    public ProfileField(String name, String value, String displayName){
        this.name = name;
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
