package com.example.marco.fiubados.model;

import java.util.HashMap;
import java.util.Map;

public class MultipleField {

    private Map<String, Field> fields = new HashMap<>();

    public MultipleField(Field... fields){

        for (Field field : fields){
            this.fields.put(field.getName(), field);
        }

    }

    public Field getField(String fieldName){
        return this.fields.get(fieldName);
    }
}
