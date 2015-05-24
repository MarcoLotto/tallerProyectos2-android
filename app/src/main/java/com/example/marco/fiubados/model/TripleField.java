package com.example.marco.fiubados.model;

/**
 * Created by Marco on 23/05/2015.
 */
public class TripleField extends DualField {

    private Field field3;

    public TripleField(){
        super();
    }

    public TripleField(Field field1, Field field2, Field field3){
        super(field1, field2);
        this.field3 = field3;
    }

    public Field getField3() {
        return field3;
    }

    public void setField3(Field field3) {
        this.field3 = field3;
    }
}
