package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.commons.AlertDialogBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class FriendshipResponseHttpAsynkTask extends HttpAsyncTask {

    String friendshipRequestId, friendshipRequestResponse;
    TabScreen screen;
    int serviceId;

    public FriendshipResponseHttpAsynkTask(Activity callingActivity, TabScreen screen, int serviceId, String friendshipRequestId, String friendshipRequestResponse) {
        super(callingActivity);
        this.friendshipRequestId = friendshipRequestId;
        this.friendshipRequestResponse = friendshipRequestResponse;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("friendshipRequestId", this.friendshipRequestId);
        this.addRequestField("friendshipRequestResponse", this.friendshipRequestResponse);
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
        this.addResponseField("message");
    }

    @Override
    protected void onResponseArrival() {
        if (this.responseCode == HttpURLConnection.HTTP_OK) {
            // Volvemos a hacer foco sobre el tab para que se actualice la data
            this.screen.onFocus();

            // Le indicamos al que me llamó que ya terminamos y nos trajo todo bien
            this.screen.onServiceCallback(new ArrayList<String>(), this.serviceId);
        } else {
            AlertDialog alert = AlertDialogBuilder.generateAlert(this.callingActivity, "Error", "Ha habido un error al procesar tu respuesta");
            alert.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }
}
