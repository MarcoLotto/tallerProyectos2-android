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
    private String searchName;
    private TabScreen screen;
    private int serviceId;

    public SearchUsersHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String searchName) {
        super(callingActivity);
        this.searchName = searchName;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        //this.addRequestField("searchName", this.searchName);
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
    }

    @Override
    protected void configureResponseFields() {
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
                    String friendsField = (new JSONObject(dataField)).getString("friends");
                    JSONArray jObject = new JSONArray(friendsField);
                    for (int i = 0; i < jObject.length(); i++) {
                        JSONObject jsonObject = jObject.getJSONObject(i);

                        String name = jsonObject.getString("email");  // TODO: Despues va a ser el name real
                        String userId = jsonObject.getString("id");
                        User user = new User(userId, name);
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
        return GET_REQUEST_TYPE;
    }
}
