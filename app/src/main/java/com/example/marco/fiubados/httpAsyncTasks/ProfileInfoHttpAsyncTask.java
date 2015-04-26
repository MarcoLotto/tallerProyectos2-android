package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.model.Academic;
import com.example.marco.fiubados.model.Education;
import com.example.marco.fiubados.model.Job;
import com.example.marco.fiubados.model.ProfileArray;
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
 * Para procesar pedidos de informacion del perfil
 */
public class ProfileInfoHttpAsyncTask extends HttpAsyncTask {
    private static final String SHOW_PROFILE_RESULT_OK = "ok";
    private User user;
    private TabScreen screen;
    private int serviceId;

    public ProfileInfoHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, User user) {
        super(callingActivity);
        this.user = user;
        this.screen = screen;
        this.serviceId = serviceId;
    }

    @Override
    protected void configureRequestFields() {
        this.addUrlRequestField(this.user.getId());
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
            String result = this.getResponseField("result");
            if(result.equals(this.SHOW_PROFILE_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String profileField = (new JSONObject(dataField)).getString("profile");
                    this.processPersonalProfileData(profileField, this.user);
                    this.processJobsProfileData(profileField, this.user.getJobs());
                    this.processEducationsProfileData(profileField, this.user.getEducationInfo());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Guardamos la info en el user que nos pasaron por parámetro, no por callback, pero igual avisamos que terminamos
            screen.onServiceCallback(new ArrayList<String>(), this.serviceId);
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

    private void processEducationsProfileData(String profileField, List<Education> educationInfo) throws JSONException {
        JSONObject jsonProfileField = new JSONObject(profileField);
        JSONArray jobsArray = jsonProfileField.getJSONArray("educations");
        for(int i=0; i < jobsArray.length(); i++){
            JSONObject jobData = (JSONObject) jobsArray.get(i);
            Education education = new Education(jobData.getString("id"));
            education.setDiploma(jobData.getString("diploma"));

            JSONObject instituteData = new JSONObject(jobData.getString("institute"));
            education.setInstitute(instituteData.getString("name"));

            JSONObject dateIntervalData = new JSONObject(jobData.getString("dateInterval"));
            education.setStartDate(dateIntervalData.getString("init"));
            try {
                education.setEndDate(dateIntervalData.getString("end"));
            }catch(Exception e){}
            educationInfo.add(education);
        }
    }

    private void processJobsProfileData(String profileField, List<Job> jobs) throws JSONException {
        JSONObject jsonProfileField = new JSONObject(profileField);
        JSONArray jobsArray = jsonProfileField.getJSONArray("jobs");
        for(int i=0; i < jobsArray.length(); i++){
            JSONObject jobData = (JSONObject) jobsArray.get(i);
            Job job = new Job(jobData.getString("id"));
            job.setCompany(jobData.getString("company"));
            job.setPosition(jobData.getString("position"));
            JSONObject dateIntervalData = new JSONObject(jobData.getString("dateInterval"));
            job.setStartDate(dateIntervalData.getString("init"));
            try {
                job.setEndDate(dateIntervalData.getString("end"));
            }catch(Exception e){}
            jobs.add(job);
        }
    }

    private void processPersonalProfileData(String profileField, User user) throws JSONException {
        JSONObject jsonProfileField = new JSONObject(profileField);
        user.setName(jsonProfileField.getString("firstName"));
        user.setLastName(jsonProfileField.getString("lastName"));
        user.setPadron(jsonProfileField.getString("padron"));
        user.setBiography(jsonProfileField.getString("biography"));
        user.setNationality(jsonProfileField.getString("nationality"));
        user.setCity(jsonProfileField.getString("city"));
    }

    @Override
    protected String getRequestMethod() {
        // Cambiando este parámetro se determina por que método se enviará el request
        return GET_REQUEST_TYPE;
    }
}
