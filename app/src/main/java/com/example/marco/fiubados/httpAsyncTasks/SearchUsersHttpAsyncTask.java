package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;

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
 * Created by Marco on 21/04/2015.
 */
public class SearchUsersHttpAsyncTask extends GetFriendsHttpAsyncTask {


    public SearchUsersHttpAsyncTask(Activity callingActivity, CallbackScreen screen, int serviceId, String searchName) {
        super(callingActivity, screen, serviceId, searchName);
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("userToken", ContextManager.getInstance().getUserToken());
        this.addRequestField("query", this.searchName);
   }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK){
            List<User> users = new ArrayList<>();

            String result = this.getResponseField("result");
            if(result.equals(this.GET_FRIEND_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                   this.fillUsers(users, dataField, User.FRIENDSHIP_STATUS_UNKNOWN, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los usuarios que conseguimos
            this.callbackScreen.onServiceCallback(users, this.serviceId);
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
    protected void fillUsers(List<User> users, String containerField, String friendshipStatus, boolean isFriendshipRequest) throws JSONException {
        JSONObject jsonData = new JSONObject(containerField);
        try {
            this.fillUserData(users, jsonData.getString("usersByName"), false);
        }catch(Exception e){}
        try {
            this.fillUserData(users, jsonData.getString("usersByCity"), true);
        }catch(Exception e){}
        try {
            this.fillUserData(users, jsonData.getString("usersByNationality"), true);
        }catch(Exception e){}
        try {
            this.fillUserData(users, jsonData.getString("usersByCareer"), true);
        }catch(Exception e){}
    }

    private void fillUserData(List<User> users, String containerField, boolean controlsMatch) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String id = jsonObject.getString("userId");
            String email = jsonObject.getString("email");
            String name = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String picture = jsonObject.getString("picture");

            User user = new User(id, name, email);
            user.setLastName(lastName);
            user.setProfilePicture(picture);
            String friendship = jsonObject.getString("friendship");
            if(controlsMatch){
                String matchLabel = jsonObject.getString("match");
                user.setMatchParameter(matchLabel);
            }
            if(friendship.equals("pendingFriendshipRequest")){
                user.setFriendshipStatus(User.FRIENDSHIP_STATUS_WAITING);
            }
            else if(friendship.equals("friendshipRequestSent")){
                user.setFriendshipStatus(User.FRIENDSHIP_STATUS_REQUESTED);
            }
            else if(friendship.equals("noFriends")){
                user.setFriendshipStatus(User.FRIENDSHIP_STATUS_UNKNOWN);
            }
            else if(friendship.equals("friends")){
                user.setFriendshipStatus(User.FRIENDSHIP_STATUS_FRIEND);
            }
            try {
                user.setFriendshipRequestId(jsonObject.getString("friendshipRequestId"));
            }catch(Exception e){}

            if(!users.contains(user)) {
                users.add(user);
            }
        }
    }
}

