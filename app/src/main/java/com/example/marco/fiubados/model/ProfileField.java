package com.example.marco.fiubados.model;

/**
 * Created by Marco on 11/04/2015.
 */
public class ProfileField extends Field{

    private String displayName, name, value;

    public ProfileField(String name, String value, String displayName){
        super(name, value);
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
