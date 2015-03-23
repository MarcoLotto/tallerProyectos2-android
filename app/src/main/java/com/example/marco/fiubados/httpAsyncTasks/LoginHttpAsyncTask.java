package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.content.Intent;

import com.example.marco.fiubados.LoginOkActivity;

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
        this.addRequestField("username", this.username);
        this.addRequestField("password", this.password);
    }

    @Override
    protected void configureResponseFields() {
       this.addResponseField("loginStatus");
    }

    @Override
    protected void onResponseArrival() {
        String loginResult = this.getResponseField("loginStatus");
        if(loginResult.equals("OK")){
            // Vamos a la activity de login OK
            Intent intent = new Intent(this.callingActivity, LoginOkActivity.class);
            this.callingActivity.startActivity(intent);
        }
    }
}
