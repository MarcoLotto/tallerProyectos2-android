package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.Comentary;
import com.example.marco.fiubados.model.User;
import com.example.marco.fiubados.model.WallNotification;
import com.example.marco.fiubados.model.WallPost;
import com.example.marco.fiubados.model.WallPostType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 23/05/2015.
 */
public class GetCommentsHttpAsyncTask extends HttpAsyncTask {

    private static final String GET_COMENTARIES_RESULT_OK = "ok";
    private String containerId;

    public GetCommentsHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, String containerId) {
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
            List<WallPost> posts = new ArrayList<>();

            String result = this.getResponseField("result");
            if(result.equals(GET_COMENTARIES_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("comentaries");
                    this.fillPosts(posts, containerField);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los comentarios que conseguimos
            this.callbackScreen.onServiceCallback(posts, this.serviceId);
        } else {
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    protected void fillPosts(List<WallPost> posts, String containerField) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String id = jsonObject.getString("id");
            String message = jsonObject.getString("message");
            String date = "12/06/2015";  // REVIEW: Sacar esto
            if(jsonObject.has("date")) {
                date = jsonObject.getString("date");
            }
            String imageUrl = jsonObject.getString("image");
            String postType = WallPostType.COMMENT.getType();
            if(jsonObject.has("postType")){
                postType = jsonObject.getString("postType");
            }
            if (WallPostType.COMMENT.getType().equals(postType)){
                User author = new User(jsonObject.getString("authorId"), jsonObject.getString("authorFirstName"), jsonObject.getString("authorLastName"));
                author.setProfilePicture(imageUrl);
                if(jsonObject.has("friendship")){
                    author.setFriendshipStatus(jsonObject.getString("friendship"));
                }

                Comentary comentary = new Comentary(id, author, message, date, imageUrl);
                posts.add(comentary);
            } else {
                WallNotification notification = new WallNotification(id, message, date, imageUrl);
                posts.add(notification);
            }

        }
    }

    @Override
    protected String getRequestMethod() {
        return GET_REQUEST_TYPE;
    }
}
