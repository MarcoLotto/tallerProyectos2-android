package com.example.marco.fiubados.model;

/**
 * Created by Marco on 24/04/2015.
 */
public class Job extends DatabaseObject {

    private String company;
    private String position;
    private String startDate;
    private String endDate;

    public Job(String id) {
        super(id);
    }

    public Job(String id, String company, String position, String startDate, String endDate) {
        super(id);
        this.company = company;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
