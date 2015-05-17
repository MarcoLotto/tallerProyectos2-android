package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 17/05/2015.
 */
public class GetGroupDiscussionsHttpAsyncTask extends HttpAsyncTask {

    private static final String RESULT_OK = "ok";
    private Group group;

    public GetGroupDiscussionsHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, Group group) {
        super(callingActivity, callbackScreen, serviceId);
        this.group = group;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
        this.addRequestField("groupId", this.group.getId());
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
        this.addResponseField("data");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            String result = this.getResponseField("result");
            if(result.equals(this.RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("groupDiscussions");
                    this.fillGroupDiscussions(this.group, containerField);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le indicamos a la pantalla que nos llamó que terminamos con el pedido
            this.callbackScreen.onServiceCallback(new ArrayList<String>(), this.serviceId);
        } else {
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    protected void fillGroupDiscussions(Group group, String containerField) throws JSONException {
        group.getDiscussions().clear();
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String discussionName = jsonObject.getString("name");
            String discussionDescription = jsonObject.getString("description");
            String discussionId = jsonObject.getString("id");
            String discussionAuthor = jsonObject.getString("author");
            String discussionCreationDate = jsonObject.getString("creationDate");
            GroupDiscussion discussion = new GroupDiscussion(discussionId, discussionName, discussionDescription);
            discussion.setAuthor(discussionAuthor);
            discussion.setCreationDate(discussionCreationDate);
            group.addDiscussion(discussion);
        }
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }
}
