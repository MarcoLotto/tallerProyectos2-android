package com.example.marco.fiubados.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 24/04/2015.
 */
public class ProfileArray {

    private List<ProfileField> fields = new ArrayList<ProfileField>();

    public List<ProfileField> getFields() {
        return fields;
    }

    public void addFields(ProfileField field) {
        this.fields.add(field);
    }
}
