package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.MainScreenActivity;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request para el servicio de Login
 */
public class LoginHttpAsyncTask extends HttpAsyncTask {
    private static final String LOGIN_RESULT_OK = "ok";
    String username, password;
    TabScreen screen;
    int serviceId;

    public LoginHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String username, String password) {
        super(callingActivity);
        this.username = username;
        this.password = password;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("email", this.username);
        this.addRequestField("password", this.password);
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
            String result = this.getResponseField("result");
            if(result.equals(this.LOGIN_RESULT_OK)) {
                String data = this.getResponseField("data");
                // Vamos a buscar los atributos en el tag de data
                try {
                    JSONObject json = new JSONObject(data);
                    ContextManager.getInstance().setUserToken(json.getString("userToken"));
                    User myUser = new User(json.getString("userId"), this.username);
                    ContextManager.getInstance().setMyUser(myUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Pudimos logueanos correctamente, vamos a la pantalla de inicio
                this.screen.onServiceCallback(new ArrayList<String>(), this.serviceId);
            }
        }
        else if(this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            Toast toast = Toast.makeText(this.callingActivity.getApplicationContext(), "Error en los datos de usuario. Revise los datos ingresados", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(this.callingActivity.getApplicationContext(), "Error en la conexión con el servidor", Toast.LENGTH_SHORT);
            toast.show();
          }
    }

    @Override
    protected String getRequestMethod() {
        // Cambiando este parámetro se determina por que método se enviará el request
        return POST_REQUEST_TYPE;
    }
}
