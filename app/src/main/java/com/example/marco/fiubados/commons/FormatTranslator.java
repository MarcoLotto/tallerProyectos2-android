package com.example.marco.fiubados.commons;

/**
 * Created by Marco on 27/04/2015.
 */
public class FormatTranslator {

    public static String adaptDate(String date){
        if(FieldsValidator.isDateValid(date))
            return date;

        if(date.length() < 10)
            return date;

        int index = date.indexOf("-");
        if(index == -1)
            return date;

        String year = date.substring(0, index);
        String aux = date.substring(index + 1);
        index = aux.indexOf("-");
        if(index == -1)
            return date;

        String month = aux.substring(0, index);
        aux = aux.substring(index + 1);
        index = aux.indexOf("T");
        String days = aux.substring(0, index);

        return days + "/" + month + "/" + year;
    }
}
