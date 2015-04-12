package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.ProfileField;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 12/04/2015.
 */
public class ProfileEditHttpAsyncTask extends HttpAsyncTask {
    private List<ProfileField> editedFields;
    private TabScreen screen;
    private int serviceId;

    public ProfileEditHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, List<ProfileField> editedFields) {
        super(callingActivity);
        this.editedFields = editedFields;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        Map<String, String> fieldsToSend = new HashMap<String, String>();
        Iterator<ProfileField> it = this.editedFields.iterator();
        while(it.hasNext()){
            ProfileField field = it.next();
            fieldsToSend.put(field.getName(), field.getValue());
        }
        this.addRequestField("profile", fieldsToSend);
    }

    @Override
    protected void configureResponseFields() {
        // TODO: Esto puede venir dividido en diferentes secciones, tener en cuenta
        this.addResponseField("result");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            if(!this.getResponseField("result").equals("ok")){
                this.dialog.setMessage("La edición no se pudo realizar, por favor verifique los datos ingresados");
                this.dialog.show();
            }
            else {
                // Le indicamos al servicio que nos llamo que terminó la edición
                screen.onServiceCallback(new ArrayList<String>(), this.serviceId);
            }
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

