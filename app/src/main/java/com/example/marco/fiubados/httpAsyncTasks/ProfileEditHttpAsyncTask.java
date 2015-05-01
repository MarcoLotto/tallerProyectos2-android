package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.ProfileField;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 12/04/2015.
 */
public class ProfileEditHttpAsyncTask extends HttpAsyncTask {
    private List<ProfileField> editedFields;
    private TabScreen screen;
    private int serviceId;

    public ProfileEditHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, List<ProfileField> editedFields) {
        super(callingActivity);
        this.editedFields = editedFields;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        // Terminamos de armar la ultima parte de la url dependiendo del usuario
        this.addUrlRequestField(ContextManager.getInstance().getMyUser().getId());
        this.addUrlRequestField("edit");
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());

        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject profileObject = new JSONObject();
            Iterator<ProfileField> it = this.editedFields.iterator();
            while(it.hasNext()){
                ProfileField field = it.next();
                profileObject.put(field.getName(), field.getValue());
            }
            JSONObject dataObject = new JSONObject();
            dataObject.put("profile", profileObject);
            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("data", dataObject);
            this.setResquestPostData(mainJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK || this.responseCode == HttpURLConnection.HTTP_CREATED){
            if(!this.getResponseField("result").equals("ok")){
                this.dialog.setMessage("La edición no se pudo realizar, por favor verifique los datos ingresados");
                this.dialog.show();
            }
            else {
                // Le indicamos al servicio que nos llamo que terminó la edición
                screen.onServiceCallback(new ArrayList<String>(), this.serviceId);
            }
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

