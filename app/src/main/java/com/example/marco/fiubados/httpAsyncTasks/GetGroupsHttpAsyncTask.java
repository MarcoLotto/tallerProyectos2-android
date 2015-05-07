package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Trae los grupos de los cuales el usuario forma parte
 */
public class GetGroupsHttpAsyncTask extends HttpAsyncTask {

    protected static final String GET_GROUPS_RESULT_OK = "ok";

    public GetGroupsHttpAsyncTask(Activity callingActivity, CallbackScreen screen, int serviceId) {
        super(callingActivity, screen, serviceId);
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
            List<Group> groups = new ArrayList<>();

            String result = this.getResponseField("result");
            if(result.equals(this.GET_GROUPS_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("groups");
                    this.fillGroups(groups, containerField);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
            this.callbackScreen.onServiceCallback(groups, this.serviceId);
        } else {
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    protected void fillGroups(List<Group> groups, String containerField) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String groupName = jsonObject.getString("name");
            String groupDescription = jsonObject.getString("description");
            String groupId = jsonObject.getString("groupId");
            Group group = new Group(groupId, groupName, groupDescription);
            groups.add(group);
        }
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }
}
