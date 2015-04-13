package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.ProfileField;
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
 * Para procesar pedidos para pedir informacion del perfil
 */
public class ProfileInfoHttpAsyncTask extends HttpAsyncTask {
    private static final String SHOW_PROFILE_RESULT_OK = "ok";
    private String userId;
    private TabScreen screen;
    private int serviceId;

    public ProfileInfoHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, String userId) {
        super(callingActivity);
        this.userId = userId;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addUrlRequestField(this.userId);
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
            List<ProfileField> fields = new ArrayList<ProfileField>();

            String result = this.getResponseField("result");
            if(result.equals(this.SHOW_PROFILE_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String profileField = (new JSONObject(dataField)).getString("profile");
                    JSONObject jsonProfileField = new JSONObject(profileField);
                    fields.add(new ProfileField("Nombre", jsonProfileField.getString("firstName")));
                    fields.add(new ProfileField("Apellido", jsonProfileField.getString("lastName")));
                    fields.add(new ProfileField("Biografia", jsonProfileField.getString("biography")));
                    fields.add(new ProfileField("Nacionalidad", jsonProfileField.getString("nationality")));
                    fields.add(new ProfileField("Ciudad", jsonProfileField.getString("city")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los campos que conseguimos
            screen.onServiceCallback(fields, this.serviceId);
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
