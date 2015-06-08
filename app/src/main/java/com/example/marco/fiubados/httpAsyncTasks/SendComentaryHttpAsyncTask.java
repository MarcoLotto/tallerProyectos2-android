package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Marco on 24/05/2015.
 */
public class SendComentaryHttpAsyncTask extends HttpAsyncTask {

    private static final String SEND_COMENTARY_RESULT_OK = "ok";
    private String containerId;
    private String parentComentaryId;
    private String message;
    private String date;

    public SendComentaryHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId,
                                      String containerId, String parentComentaryId, String message) {
        super(callingActivity, callbackScreen, serviceId);
        this.containerId = containerId;
        this.parentComentaryId = parentComentaryId;
        this.message = message;
        this.date = (Long.valueOf(new Date().getTime())).toString();
    }

    @Override
    protected void configureRequestFields() {
        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("userToken", ContextManager.getInstance().getUserToken());
            jObject.put("containerId", this.containerId); // Id de usuario / discusión
            jObject.put("parentId", this.parentComentaryId); // Id del comentario padre (-1 si no tiene)
            jObject.put("message", this.message); // Mensaje del comentario
            jObject.put("date", this.date); // Timestamp del comentario
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
            if(this.getResponseField("result").equals(SEND_COMENTARY_RESULT_OK)) {
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
