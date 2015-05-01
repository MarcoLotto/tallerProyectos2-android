package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.DatabaseObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Marco on 26/04/2015.
 */
public class ProfileDeleteHttpAsyncTask extends HttpAsyncTask{

    private TabScreen screen;
    private int serviceId;
    private DatabaseObject object;

    public ProfileDeleteHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, DatabaseObject object) {
        super(callingActivity);
        this.screen = screen;
        this.serviceId = serviceId;
        this.object = object;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
        this.addResponseField("message");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK || this.responseCode == HttpURLConnection.HTTP_CREATED){
            if(!this.getResponseField("result").equals("ok")){
                this.dialog.setMessage("La edición no se pudo realizar, por favor verifique los datos ingresados");
                this.dialog.show();
            }
            else {
                // Cambiamos el estado del job que nos pasaron a no-dirty
                this.object.setDirty(false);

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
        return POST_REQUEST_TYPE;
    }
}
