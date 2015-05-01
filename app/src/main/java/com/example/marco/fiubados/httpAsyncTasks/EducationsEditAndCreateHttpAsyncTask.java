package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.Education;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Marco on 26/04/2015.
 */
public class EducationsEditAndCreateHttpAsyncTask extends HttpAsyncTask {
    private TabScreen screen;
    private int serviceId;
    private Education education;

    public EducationsEditAndCreateHttpAsyncTask(Activity callingActivity, TabScreen tabScreen, int serviceId, Education education) {
        super(callingActivity);
        this.screen = tabScreen;
        this.serviceId = serviceId;
        this.education = education;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());

        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject dateObject = new JSONObject();
            dateObject.put("init", this.education.getStartDate());
            dateObject.put("end", this.education.getEndDate());

            JSONObject instituteObject = new JSONObject();
            instituteObject.put("name", this.education.getInstitute());

            JSONObject jobObject = new JSONObject();
            jobObject.put("diploma", this.education.getDiploma());
            jobObject.put("institute", instituteObject);
            jobObject.put("dateInterval", dateObject);

            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("education", jobObject);
            this.setResquestPostData(mainJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
        this.addResponseField("message");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK || this.responseCode == HttpURLConnection.HTTP_CREATED){
            if(!this.getResponseField("result").equals("ok")){
                this.dialog.setMessage("La edición no se pudo realizar, por favor verifique los datos ingresados");
                this.dialog.show();
            }
            else {
                // Cambiamos el estado del job que nos pasaron a no-dirty
                this.education.setDirty(false);

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
        return POST_REQUEST_TYPE;
    }
}
