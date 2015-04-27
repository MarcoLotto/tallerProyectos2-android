package com.example.marco.fiubados.httpAsyncTasks;


import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.Group;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GroupEditAndCreateHttpAsyncTask extends HttpAsyncTask{

    private TabScreen screen;
    private int serviceId;
    private Group group;

    public GroupEditAndCreateHttpAsyncTask(Activity callingActivity, TabScreen tabScreen, int serviceId, Group group) {
        super(callingActivity);
        this.screen = tabScreen;
        this.serviceId = serviceId;
        this.group = group;
    }



    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());

        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject dateObject = new JSONObject();

            JSONObject jobObject = new JSONObject();
            jobObject.put("name", this.group.getName());
            jobObject.put("description", this.group.getDescription());

            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("group", jobObject);
            this.setResquestPostData(mainJsonObject);
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
        if(this.responseCode == HttpURLConnection.HTTP_CREATED){
            if(!this.getResponseField("result").equals("ok")){
                this.dialog.setMessage("La edición no se pudo realizar, por favor verifique los datos ingresados");
                this.dialog.show();
            }
                // Le indicamos al servicio que nos llamo que terminó la creacion
                screen.onServiceCallback(new ArrayList<String>(), this.serviceId);
                screen.onFocus();

        } else if(this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            this.dialog.setMessage("Usted no esta autorizado para realizar esto");
            this.dialog.show();
        } else{
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }
}
