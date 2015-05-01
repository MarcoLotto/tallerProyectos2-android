package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;

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

    public SendFriendRequestHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String requestedUserId) {
        super(callingActivity);
        this.screen = screen;
        this.serviceId = serviceId;
        this.requestedUserId = requestedUserId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
        this.addRequestField("userToInviteId", this.requestedUserId);
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
     }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            String resultValue = this.getResponseField("result");
            if(!resultValue.equals("ok")) {
                this.dialog.setMessage("Su solicitud no pudo ser procesada");
                this.dialog.show();
            }
            // Le indicamos a la pantalla que nos llamo que terminamos y el resultado de esto
            List<String> results = new ArrayList<>();
            results.add(resultValue);
            screen.onServiceCallback(results, this.serviceId);
        }
        else if(this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            this.dialog.setMessage("Usted no esta autorizado para realizar esto");
            this.dialog.show();
        }
        else if(this.responseCode == HttpURLConnection.HTTP_CONFLICT){
            Toast toast = Toast.makeText(this.callingActivity.getApplicationContext(), "Solicitud anterior pendiente", Toast.LENGTH_SHORT);
            toast.show();
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
