package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 18/04/2015.
 */
public class SearchUsersHttpAsyncTask extends GetFriendsHttpAsyncTask {


    public SearchUsersHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String searchName) {
        super(callingActivity, screen, serviceId, searchName);
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
        //this.addRequestField("query", this.searchName);
        this.addRequestField("query", null);  // TODO: Reemplazar por la linea de arriba cuando se implemente
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            List<User> users = new ArrayList<User>();

            String result = this.getResponseField("result");
            if(result.equals(this.GET_FRIEND_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("users");
                    this.fillUsers(users, containerField, User.FRIENDSHIP_STATUS_UNKNOWN, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los usuarios que conseguimos
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
}
