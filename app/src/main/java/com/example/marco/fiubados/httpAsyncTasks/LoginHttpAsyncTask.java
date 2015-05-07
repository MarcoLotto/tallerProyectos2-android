package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request para el servicio de Login
 */
public class LoginHttpAsyncTask extends HttpAsyncTask {

    private final String LOG_TAG = LoginHttpAsyncTask.class.getSimpleName();
    private final String LOGIN_RESULT_OK = "ok";
    private final String LOGIN_RESULT_UNAPPROVED = "unapprovedUser";
    private final String LOGIN_RESULT_INVALID_CREDENTIALS = "invalidCredentials";

    String username, password;

    public LoginHttpAsyncTask(Activity callingActivity, CallbackScreen screen, int serviceId,
                              String username, String password) {
        super(callingActivity, screen, serviceId);
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
        String result = this.getResponseField("result");
        Log.v(LOG_TAG, "RESULT STRING: " + result);
        if (result.equals(LOGIN_RESULT_OK)) {
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
            this.callbackScreen.onServiceCallback(new ArrayList<String>(), this.serviceId);
        } else if (result.equals(LOGIN_RESULT_UNAPPROVED)) {
            Toast.makeText(
                    this.callingActivity.getApplicationContext(),
                    "El registro no ha sido aprobado por un administrador. Ten paciencia.",
                    Toast.LENGTH_LONG)
                    .show();
        } else if (result.equals(LOGIN_RESULT_INVALID_CREDENTIALS)) {
            Toast.makeText(
                    this.callingActivity.getApplicationContext(),
                    "Error en los datos de usuario. Revise los datos ingresados.",
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(
                    this.callingActivity.getApplicationContext(),
                    "Error en la conexión con el servidor.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected String getRequestMethod() {
        // Cambiando este parámetro se determina por que método se enviará el request
        return POST_REQUEST_TYPE;
    }
}
