package com.example.marco.fiubados.commons;

import java.util.List;

/**
 * Created by Marco on 10/05/2015.
 */
public interface DialogCallback{

    /**
     * Es invocado cuando un dialog se cierra. Se informa el id de dialog, las salidas, y un indicador
     * de si se apreto el boton de aceptar
      * @param dialogId
     * @param outputs
     * @param userAccepts
     */
    void onDialogClose(int dialogId, List<String> outputs, boolean userAccepts);
}
