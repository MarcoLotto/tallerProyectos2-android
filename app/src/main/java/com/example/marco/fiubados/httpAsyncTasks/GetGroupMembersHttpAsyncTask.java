package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.util.Log;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Obtiene los miembros de un grupo
 */
public class GetGroupMembersHttpAsyncTask extends HttpAsyncTask {
    private static final String LOG_TAG = GetGroupMembersHttpAsyncTask.class.getSimpleName();
    protected static final String GET_GROUP_MEMBERS_RESULT_OK = "ok";

    public GetGroupMembersHttpAsyncTask(Activity callingActivity, CallbackScreen screen, int serviceId) {
        super(callingActivity, screen, serviceId);
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }

    @Override
    protected void configureRequestFields() {
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
            List<User> members = new ArrayList<>();

            String result = this.getResponseField("result");
            if(result.equals(this.GET_GROUP_MEMBERS_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    JSONObject jGroup = (new JSONObject(dataField)).getJSONObject("group");
                    String containerField = jGroup.getString("members");
                    this.fillGroupMembers(members, containerField);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
            this.callbackScreen.onServiceCallback(members, this.serviceId);
        } else {
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    protected void fillGroupMembers(List<User> groupMembers, String containerField) throws JSONException {
        JSONArray jArray = new JSONArray(containerField);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObject = jArray.getJSONObject(i);

            String userId = jObject.getString("userId");
            String userFirstName = jObject.getString("firstName");
            String userLastName = jObject.getString("lastName");

            User user = new User(userId, userFirstName, userLastName);
            groupMembers.add(user);
        }
    }
}
