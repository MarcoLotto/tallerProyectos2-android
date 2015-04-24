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
public class GetFriendsHttpAsyncTask extends HttpAsyncTask {
    protected static final String GET_FRIEND_RESULT_OK = "ok";
    protected String searchName;
    protected TabScreen screen;
    protected int serviceId;

    public GetFriendsHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String searchName) {
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
                    String containerField = (new JSONObject(dataField)).getString("friends");
                    this.fillUsers(users, containerField, User.FRIENDSHIP_STATUS_FRIEND, false);

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

    protected void fillUsers(List<User> users, String containerField, String friendshipStatus, boolean isFriendshipRequest) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String userId = jsonObject.getString("userId");
            String email = jsonObject.getString("email");
            User user = new User(userId, firstName + " " + lastName, email);
            user.setFriendshipStatus(friendshipStatus);
            if(isFriendshipRequest){
                user.setFriendshipRequestId(jsonObject.getString("friendshipRequestId"));
            }
            users.add(user);
        }
    }

    @Override
    protected String getRequestMethod() {
        // Cambiando este parámetro se determina por que método se enviará el request
        return GET_REQUEST_TYPE;
    }
}
