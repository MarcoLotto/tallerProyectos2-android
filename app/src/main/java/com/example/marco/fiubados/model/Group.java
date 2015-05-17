package com.example.marco.fiubados.model;

import java.util.ArrayList;
import java.util.List;

public class Group extends DatabaseObject {

    private String name;
    private String description;
    private List<GroupDiscussion> discussions;
    private boolean isMember;
	
    public Group(String id) {
        super(id);
    }

    public Group(String id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
        this.discussions = new ArrayList<GroupDiscussion>();
    }

    public Group(String id, String name, String description, boolean isMember){
        super(id);
        this.name = name;
        this.description = description;
        this.isMember = isMember;
        this.discussions = new ArrayList<GroupDiscussion>();
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

    public void addDiscussion(GroupDiscussion discussion) {
        this.discussions.add(discussion);
    }
    public List<GroupDiscussion> getDiscussions(){
        return this.discussions;
    }
}
