package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.commons.AlertDialogBuilder;

import java.net.HttpURLConnection;

public class FriendshipResponseHttpAsynkTask extends HttpAsyncTask {

    String friendshipRequestId, friendshipRequestResponse;

    public FriendshipResponseHttpAsynkTask(Activity callingActivity, String friendshipRequestId, String friendshipRequestResponse) {
        super(callingActivity);
        this.friendshipRequestId = friendshipRequestId;
        this.friendshipRequestResponse = friendshipRequestResponse;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("friendshipRequestId", this.friendshipRequestId);
        this.addRequestField("friendshipRequestResponse", this.friendshipRequestResponse);
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
          // Se envió la respuesta bien...
        } else {
            AlertDialog alert = AlertDialogBuilder.generateAlert(this.callingActivity, "Error !", "Ha habido un error al procesar tu respuesta");
            alert.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }
}
