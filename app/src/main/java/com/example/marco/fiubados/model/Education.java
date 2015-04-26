package com.example.marco.fiubados.model;

/**
 * Created by Marco on 26/04/2015.
 */
public class Education extends DatabaseObject {

    private String diploma;
    private String institute;
    private String startDate;
    private String endDate;

    public Education(String id) {
        super(id);
    }

    public Education(String id, String diploma, String institute, String startDate, String endDate) {
        super(id);
        this.diploma = diploma;
        this.institute = institute;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
