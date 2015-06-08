package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.WallPostType;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;

public class SendWallNotificationHttpAsyncTask extends HttpAsyncTask {

    private static final String SEND_WALL_NOTIFICATION_RESULT_OK = "ok";
    private String containerId;
    private String title;
    private String message;
    private String date;

    public SendWallNotificationHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId,
                                             String title, String containerId, String message) {
        super(callingActivity, callbackScreen, serviceId);
        this.containerId = containerId;
        this.title = title;
        this.message = message;
        this.date = (Long.valueOf(new Date().getTime())).toString();
    }

    @Override
    protected void configureRequestFields() {
        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("userToken", ContextManager.getInstance().getUserToken());
            jObject.put("containerId", this.containerId); // Id de usuario
            jObject.put("title", this.title); // Título de la notificacion
            jObject.put("message", this.message); // Mensaje de la notificacion
            jObject.put("date", this.date); // Timestamp de la notificacion
            jObject.put("postType", WallPostType.NOTIFICATION.getType()); // Tipo de wallPost
            setRequestPostData(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
    }

    @Override
    protected void onResponseArrival() {
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            if(this.getResponseField("result").equals(SEND_WALL_NOTIFICATION_RESULT_OK)) {
                // Le indicamos al servicio que ya se agrego el comentario
                callbackScreen.onServiceCallback(new ArrayList<String>(), serviceId);
            }
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
