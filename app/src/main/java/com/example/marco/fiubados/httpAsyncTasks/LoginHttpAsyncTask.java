package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.content.Intent;

import com.example.marco.fiubados.MainScreenActivity;

import java.net.HttpURLConnection;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request REST para el servicio de Login
 */
public class LoginHttpAsyncTask extends HttpAsyncTask {
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
        this.addResponseField("email");
        this.addResponseField("user_token");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            // TODO: Tengo que guardarme el user token y password!
            // String userToken = this.getResponseField("user_token");
            // String email = this.getResponseField("email");

            // Pudimos logueanos correctamente, vamos a la pantalla de inicio
            Intent intent = new Intent(this.callingActivity, MainScreenActivity.class);
            this.callingActivity.startActivity(intent);
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
