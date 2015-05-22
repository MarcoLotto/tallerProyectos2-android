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
 * Servicio para buscar grupos
 */
public class SearchGroupsHttpAsyncTask extends HttpAsyncTask {
    protected static final String GET_GROUPS_RESULT_OK = "ok";

    private String searchQuery;

    public SearchGroupsHttpAsyncTask(Activity callingActivity, CallbackScreen screen, int serviceId, String searchQuery) {
        super(callingActivity, screen, serviceId);
        this.searchQuery = searchQuery;
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }

    @Override
    protected void configureRequestFields() {
        addRequestField("userToken", ContextManager.getInstance().getUserToken());
        addRequestField("query", searchQuery);
    }

    @Override
    protected void configureResponseFields() {
        addResponseField("result");
        addResponseField("message");
        addResponseField("data");
    }

    @Override
    protected void onResponseArrival() {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            List<Group> groups = new ArrayList<>();

            String result = getResponseField("result");
            if (result.equals(GET_GROUPS_RESULT_OK)) {
                String dataField = getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("groupsByName");
                    fillGroups(groups, containerField);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
            callbackScreen.onServiceCallback(groups, serviceId);
        } else {
            dialog.setMessage("Error en la conexión con el servidor");
            dialog.show();
        }
    }

    protected void fillGroups(List<Group> groups, String containerField) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String groupId = jsonObject.getString("groupId");
            String groupName = jsonObject.getString("name");
            String groupDescription = jsonObject.getString("description");
            boolean groupIsMember = jsonObject.getBoolean("isMember");
            Group group = new Group(groupId, groupName, groupDescription, groupIsMember);
            groups.add(group);
        }
    }
}
