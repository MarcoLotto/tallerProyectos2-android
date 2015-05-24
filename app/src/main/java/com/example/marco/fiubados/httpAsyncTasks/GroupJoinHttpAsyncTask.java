package com.example.marco.fiubados.httpAsyncTasks;


import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GroupJoinHttpAsyncTask extends HttpAsyncTask{

    public GroupJoinHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId) {
        super(callingActivity, callbackScreen, serviceId);
    }

    @Override
    protected void configureRequestFields() {
        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("userToken", ContextManager.getInstance().getUserToken());
            setResquestPostData(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
        this.addResponseField("message");
    }

    @Override
    protected void onResponseArrival() {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Le indicamos al servicio que ya se unió el usuario
            callbackScreen.onServiceCallback(new ArrayList<String>(), serviceId);
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            dialog.setMessage("El grupo al que se quiere suscribir no existe");
            dialog.show();
        } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
            dialog.setMessage("Ya es miembro del grupo");
            dialog.show();
        } else {
            dialog.setMessage("Error en la conexión con el servidor");
            dialog.show();
        }

    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }
}
