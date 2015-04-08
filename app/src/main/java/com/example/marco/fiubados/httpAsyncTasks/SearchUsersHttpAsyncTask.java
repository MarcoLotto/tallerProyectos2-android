package com.example.marco.fiubados.httpAsyncTasks;

/**
 * Created by Marco on 07/04/2015.
 */

import android.app.Activity;
import android.content.Intent;

import com.example.marco.fiubados.MainScreenActivity;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.User;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request para el servicio de Login
 */
public class SearchUsersHttpAsyncTask extends HttpAsyncTask {
    private String userPartialName, myUserId;
    private TabScreen screen;
    private int serviceId;

    public SearchUsersHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId) {
        super(callingActivity);
        this.userPartialName = userPartialName;
        this.myUserId = myUserId;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userPartialName", this.userPartialName);
        this.addRequestField("myUserId", this.myUserId);
    }

    @Override
    protected void configureResponseFields() {
        // TODO: No va a venir un unico usuario esto hay que hacerlo para una lista!
        this.addResponseField("userName");
        this.addResponseField("userFriendshipStatus");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            List<User> users = new ArrayList<User>();

            // TODO: Hacer esto para cada usuario que traigamos y agregarlos todos a la lista de users
            User user = new User(this.getResponseField("userName"));
            String userFriendShipStatus = this.getResponseField("userFriendshipStatus");
            user.setFriendshipStatus(userFriendShipStatus);
            users.add(user);

            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
            screen.onServiceCallback(users, this.serviceId);
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
        // Cambiando este parámetro se determina por que método se enviará el request
        return POST_REQUEST_TYPE;
    }
}
