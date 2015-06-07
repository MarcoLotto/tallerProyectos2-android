package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.File;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;
import com.example.marco.fiubados.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by tom
 */
public class GetGroupFilesHttpAsyncTask extends HttpAsyncTask {

    private static final String RESULT_OK = "ok";
    private Group group;

    public GetGroupFilesHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, Group group) {
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
            if(result.equals(RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("groupUploadedData");
                    this.fillGroupFiles(this.group, containerField);
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

    protected void fillGroupFiles(Group group, String containerField) throws JSONException {
        group.getFiles().clear();
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String fId = jsonObject.getString("id");
            String fName = jsonObject.getString("name");
            String fUrl = jsonObject.getString("url");
            //String fFileType = jsonObject.getString("fileType");
            String userId =  jsonObject.getString("authorId");
            String fileAuthor = jsonObject.getString("authorFirstName") + " " + jsonObject.getString("authorLastName");
            String discussionCreationDate = jsonObject.getString("creationDate");
            File file = new File(fId,fName, fUrl, fileAuthor );
            file.setCreationDate(discussionCreationDate);
            group.addFile(file);
        }
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }
}
