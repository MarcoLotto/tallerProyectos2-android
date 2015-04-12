package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.content.Intent;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.MainScreenActivity;
import com.example.marco.fiubados.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request para el servicio de Login
 */
public class LoginHttpAsyncTask extends HttpAsyncTask {
    private static final String LOGIN_RESULT_OK = "ok";
    String username, password;

    public LoginHttpAsyncTask(Activity callingActivity, String username, String password) {
        super(callingActivity);
        this.username = username;
        this.password = password;
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
                    User myUser = new User("", json.getString("userId"));
                    ContextManager.getInstance().setMyUser(myUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Pudimos logueanos correctamente, vamos a la pantalla de inicio
                Intent intent = new Intent(this.callingActivity, MainScreenActivity.class);
                this.callingActivity.startActivity(intent);
            }
        }
        else if(this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            this.dialog.setMessage("Error en los datos de usuario. Revise los datos ingresados");
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