package com.example.marco.fiubados.model;

public class Group extends DatabaseObject {

    private String name;
    private String description;
    private boolean isMember;

    public Group(String id) {
        super(id);
    }

    public Group(String id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Group(String id, String name, String description, boolean isMember){
        super(id);
        this.name = name;
        this.description = description;
        this.isMember = isMember;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setIsMember(boolean isMember) {
        this.isMember = isMember;
    }
}
