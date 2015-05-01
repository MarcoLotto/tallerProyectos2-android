package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.Job;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Marco on 25/04/2015.
 */
public class JobsEditAndCreateHttpAsyncTask extends HttpAsyncTask{

    private TabScreen screen;
    private int serviceId;
    private Job job;

    public JobsEditAndCreateHttpAsyncTask(Activity callingActivity, TabScreen tabScreen, int serviceId, Job job) {
        super(callingActivity);
        this.screen = tabScreen;
        this.serviceId = serviceId;
        this.job = job;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestFieldAndForceAsGetParameter("userToken", ContextManager.getInstance().getUserToken());

        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject dateObject = new JSONObject();
            dateObject.put("init", this.job.getStartDate());
            dateObject.put("end", this.job.getEndDate());

            JSONObject jobObject = new JSONObject();
            jobObject.put("company", this.job.getCompany());
            jobObject.put("position", this.job.getPosition());
            jobObject.put("dateInterval", dateObject);

            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("job", jobObject);
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
                this.job.setDirty(false);

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
