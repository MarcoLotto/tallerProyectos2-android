package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.Career;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 03/05/2015.
 */
public class GetCareersHttpAsyncTask extends HttpAsyncTask {

    private final TabScreen screen;
    private final int serviceId;
    private final List<Career> careers;

    public GetCareersHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, List<Career> careers) {
        super(callingActivity);
        this.screen = screen;
        this.serviceId = serviceId;
        this.careers = careers;
    }

    @Override
    protected void configureRequestFields() {

    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("carreras");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            this.careers.clear();
            List<String> subjectCodes = new ArrayList<String>();
            String careersData = this.getResponseField("carreras");
            try {
                JSONArray careersArray = new JSONArray(careersData);
                for (int i = 0; i < careersArray.length(); i++) {
                    JSONObject careerObject = careersArray.getJSONObject(i);
                    String id = careerObject.getString("id");
                    String careerName = careerObject.getString("carrera");
                    Career career = new Career(id);
                    career.setName(careerName);
                    this.careers.add(career);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            // Le indicamos a la pantalla que nos llamo que terminamos
            this.screen.onServiceCallback(subjectCodes, this.serviceId);
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
        return GET_REQUEST_TYPE;
    }
}
