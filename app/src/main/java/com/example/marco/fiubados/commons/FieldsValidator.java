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
            Integer.parseInt(fieldValue);
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
}
