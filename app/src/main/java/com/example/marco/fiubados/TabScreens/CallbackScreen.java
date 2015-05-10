package com.example.marco.fiubados.TabScreens;

import java.util.List;

/**
 * Created by Marco on 07/04/2015.
 */
public interface CallbackScreen {
    /**
     * Debe llamarse cuando la aplicación toma foco
     */
    void onFocus();

    /**
     * Sera llamado cuando algun servicio termine con su tarea y retorne la respuesta del servidor
     * Devuelve una lista con los elementos conseguidos y un identificador de servicio
     */
    void onServiceCallback(List responseElements, int serviceId);
}
