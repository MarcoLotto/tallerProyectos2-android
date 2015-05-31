package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.TabScreens.WallTabScreen;
import com.example.marco.fiubados.model.Comentary;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 23/05/2015.
 */
public class GetComentariesHttpAsyncTask extends HttpAsyncTask {

    private static final String GET_COMENTARIES_RESULT_OK = "ok";
    private String containerId;

    public GetComentariesHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, String containerId) {
        super(callingActivity, callbackScreen, serviceId);
        this.containerId = containerId;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
        this.addRequestField("id", containerId);
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
        this.addResponseField("data");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            List<Comentary> comentaries = new ArrayList<>();

            String result = this.getResponseField("result");
            if(result.equals(this.GET_COMENTARIES_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("comentaries");
                    this.fillComentaries(comentaries, containerField);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
            this.callbackScreen.onServiceCallback(comentaries, this.serviceId);
        } else {
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    protected void fillComentaries(List<Comentary> comentaries, String containerField) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String id = jsonObject.getString("id");
            User author = new User(jsonObject.getString("authorId"), jsonObject.getString("authorFirstName"), jsonObject.getString("authorLastName"));
            String imageUrl = jsonObject.getString("image");
            author.setProfilePicture(imageUrl);
            if(jsonObject.has("friendship")){
                author.setFriendshipStatus(jsonObject.getString("friendship"));
            }
            String message = jsonObject.getString("message");
            Comentary comentary = new Comentary(id, author, message, imageUrl);
            comentaries.add(comentary);
        }
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }
}
