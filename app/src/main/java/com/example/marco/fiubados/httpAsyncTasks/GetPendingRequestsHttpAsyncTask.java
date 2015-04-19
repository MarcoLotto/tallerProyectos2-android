package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request para el servicio de Login
 */
public class GetPendingRequestsHttpAsyncTask extends GetFriendsHttpAsyncTask {

    public GetPendingRequestsHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String searchName) {
        super(callingActivity, screen, serviceId, searchName);
    }

    @Override
    protected void onResponseArrival() {
        if (this.responseCode == HttpURLConnection.HTTP_OK) {
            List<User> users = new ArrayList<User>();

            String result = this.getResponseField("result");
            if (result.equals(this.GET_FRIEND_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("pendingFriendshipRequests");
                    this.fillUsers(users, containerField, User.FRIENDSHIP_STATUS_WAITING, true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
            this.screen.onServiceCallback(users, this.serviceId);
        } else if (this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            this.dialog.setMessage("Usted no esta autorizado para realizar esto");
            this.dialog.show();
        } else {
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }
}

