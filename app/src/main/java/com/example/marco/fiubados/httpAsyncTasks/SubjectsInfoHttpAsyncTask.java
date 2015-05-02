package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.TabScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 02/05/2015.
 */
public class SubjectsInfoHttpAsyncTask extends HttpAsyncTask {

    private TabScreen screen;
    private int serviceId;
    private Map<String, String> subjects;

    public SubjectsInfoHttpAsyncTask(Activity callingActivity, TabScreen screen, int serviceId, Map<String, String> subjects) {
        super(callingActivity);
        this.screen = screen;
        this.serviceId = serviceId;
        this.subjects = subjects;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("subjects");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            this.subjects.clear();
            String subjectsData = this.getResponseField("subjects");
            try {
                JSONArray subjectsArray = new JSONArray(subjectsData);
                for (int i = 0; i < subjectsArray.length(); i++) {
                    JSONObject jsonObject = subjectsArray.getJSONObject(i);
                    String subjectCode = jsonObject.getString("code");
                    String subjectName = jsonObject.getString("name");
                    this.subjects.put(subjectCode, subjectName);
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            // Le indicamos a la pantalla que nos llamo que terminamos
            this.screen.onServiceCallback(new ArrayList(), this.serviceId);
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
