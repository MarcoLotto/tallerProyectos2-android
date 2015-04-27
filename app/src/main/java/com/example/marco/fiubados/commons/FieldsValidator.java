package com.example.marco.fiubados.commons;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 19/04/2015.
 */
public class FieldsValidator {

    /**
     * Valida que el valor de un campo tenga un largo mínimo
     */
    public static boolean isTextFieldValid(String fieldValue, int minCharacters){
        return fieldValue.length() >= minCharacters;
    }

    /**
     * Valida que el valor de un campo tenga un largo mínimo y que tenga todos los symbolos requeridos
     */
    public static boolean isTextFieldValid(String fieldValue, int minCharacters, List<String> neededSymbols){
        Iterator<String> it = neededSymbols.iterator();
        while(it.hasNext()){
            if(!fieldValue.contains(it.next())){
                return false;
            }
        }
        return isTextFieldValid(fieldValue, minCharacters);
    }

    /**
     * Determina si el valor de un campo corresponde con valores numericos
     */
    public static boolean isNumericFieldValid(String fieldValue){
        try {
            Long.parseLong(fieldValue);
        } catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }


    /**
     * Determina si el valor de un campo corresponde con valores numericos y con un tamaño mínimo
     */
    public static boolean isNumericFieldValid(String fieldValue, int minSize){
        return isNumericFieldValid(fieldValue) && isTextFieldValid(fieldValue, minSize);
    }

    public static boolean isDateValid(String date){
        if(date.length() > 10)
            return false;
        int index = date.indexOf("/");
        if(index == -1)
            return false;
        String days = date.substring(0, index);
        String aux = date.substring(index + 1);
        index = aux.indexOf("/");
        if(index == -1)
            return false;
        String month = aux.substring(0, index);
        String year = aux.substring(index + 1);

        try {
            long parsedDay = Long.parseLong(days);
            if (parsedDay > 31 || parsedDay < 1)
                return false;
            long parsedMonth = Long.parseLong(month);
            if (parsedMonth > 12 || parsedMonth < 1)
                return false;
            long parsedYear = Long.parseLong(year);
            if(parsedYear < 1000)
                return false;
        }catch(Exception e){
            return false;
        }
        return true;
    }
}
