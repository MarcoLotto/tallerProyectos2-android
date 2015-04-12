package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar pedidos para pedir informacion del perfil
 */
public class ProfileInfoHttpAsyncTask extends HttpAsyncTask {
    private String userId;
    private TabScreen screen;
    private int serviceId;

    public ProfileInfoHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String userId) {
        super(callingActivity);
        this.userId = userId;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userId", this.userId);
    }

    @Override
    protected void configureResponseFields() {
        // TODO: Esto puede venir dividido en diferentes secciones, tener en cuenta
        this.addResponseField("Name");
        this.addResponseField("Birthday");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            List<ProfileField> fields = new ArrayList<ProfileField>();

            // Esto se prodía hacer genérico, pero bue, kb. Agregar cada campo que cambie en el backend
            ProfileField field = new ProfileField("Name", this.getResponseField("Name"));
            fields.add(field);
            field = new ProfileField("Birthday", this.getResponseField("Birthday"));
            fields.add(field);

            // Le devolvemos a la pantalla que nos llamó todos los campos que conseguimos
            screen.onServiceCallback(fields, this.serviceId);
        }
        else if(this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            this.dialog.setMessage("Usted no esta autorizado para realizar esto");
            this.dialog.show();
        }
        else{
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        // Cambiando este parámetro se determina por que método se enviará el request
        return POST_REQUEST_TYPE;
    }
}
