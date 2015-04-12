package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.User;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 08/04/2015.
 */
public class SendFriendRequestHttpAsyncTask extends HttpAsyncTask {
    String requestedUserId, myUserId;
    private TabScreen screen;
    private int serviceId;

    public SendFriendRequestHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String requestedUserId, String myUserId) {
        super(callingActivity);
        this.screen = screen;
        this.serviceId = serviceId;
        this.requestedUserId = requestedUserId;
        this.myUserId = myUserId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("myUserId", this.myUserId);
        this.addRequestField("requestedUserId", this.requestedUserId);
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("Result");
     }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            String resultValue = this.getResponseField("Result");
            if(!resultValue.equals("ok")) {
                this.dialog.setMessage("Su solicitud no pudo ser procesada");
                this.dialog.show();
            }
            // Le indicamos a la pantalla que nos llamo que terminamos y el resultado de esto
            List<String> results = new ArrayList<String>();
            results.add(resultValue);
            screen.onServiceCallback(results, this.serviceId);
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
