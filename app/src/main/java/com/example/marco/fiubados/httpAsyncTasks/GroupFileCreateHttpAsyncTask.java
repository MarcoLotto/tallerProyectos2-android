package com.example.marco.fiubados.httpAsyncTasks;


import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.File;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GroupFileCreateHttpAsyncTask extends HttpAsyncTask{

    private File file;

    public GroupFileCreateHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, File file) {
        super(callingActivity, callbackScreen, serviceId);
        this.file = file;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());

        // Armamos la data personalizada para el envio por POST
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userToken", ContextManager.getInstance().getUserToken());
            jsonObject.put("name", this.file.getName());
            jsonObject.put("url", this.file.getName());
            jsonObject.put("type", this.file.getName());

            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("uploadedDatum", jsonObject);

            this.setRequestPostData(jsonObject);
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
                this.dialog.setMessage("La creación no se pudo realizar");
                this.dialog.show();
            }
            // Le indicamos al servicio que nos llamo que terminó la creacion
            this.callbackScreen.onServiceCallback(new ArrayList<String>(), this.serviceId);

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

