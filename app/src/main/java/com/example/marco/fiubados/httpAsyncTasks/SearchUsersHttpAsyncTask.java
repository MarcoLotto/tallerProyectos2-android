package com.example.marco.fiubados.httpAsyncTasks;

/**
 * Created by Marco on 07/04/2015.
 */

import android.app.Activity;
import android.content.Intent;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.MainScreenActivity;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONArray;
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
public class SearchUsersHttpAsyncTask extends HttpAsyncTask {
    private static final String GET_FRIEND_RESULT_OK = "ok";
    private String searchName, myUserId;
    private TabScreen screen;
    private int serviceId;

    public SearchUsersHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String searchName, String myUserId) {
        super(callingActivity);
        this.searchName = searchName;
        this.myUserId = myUserId;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("searchName", this.searchName);
        this.addRequestField("myUserId", this.myUserId);
    }

    @Override
    protected void configureResponseFields() {
        // TODO: No va a venir un unico usuario esto hay que hacerlo para una lista!
        this.addResponseField("result");
        this.addResponseField("message");
        this.addResponseField("data");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            List<User> users = new ArrayList<User>();

            String result = this.getResponseField("result");
            if(result.equals(this.GET_FRIEND_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    JSONArray jObject = new JSONArray(dataField);
                    for (int i = 0; i < jObject.length(); i++) {
                        JSONObject jsonObject = jObject.getJSONObject(i);

                        String name = jsonObject.getString("name");
                        String userId = jsonObject.getString("userId");
                        String friendshipStatus = jsonObject.getString("friendshipStatus");
                        User user = new User(userId, name);
                        user.setFriendshipStatus(friendshipStatus);
                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
