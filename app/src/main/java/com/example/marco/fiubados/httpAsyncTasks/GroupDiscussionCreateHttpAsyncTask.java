package com.example.marco.fiubados.httpAsyncTasks;


import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GroupDiscussionCreateHttpAsyncTask extends HttpAsyncTask{

    private GroupDiscussion discussion;

    public GroupDiscussionCreateHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, GroupDiscussion discussion) {
        super(callingActivity, callbackScreen, serviceId);
        this.discussion = discussion;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());

        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject dateObject = new JSONObject();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userToken", ContextManager.getInstance().getUserToken());
            jsonObject.put("subject", this.discussion.getName());
            jsonObject.put("description", this.discussion.getDescription());
            this.setResquestPostData(jsonObject);
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

