package com.example.marco.fiubados.httpAsyncTasks;

/**
 * Created by Marco on 07/04/2015.
 */

import android.app.Activity;
import android.location.Location;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Marco on 21/03/2015.
 *
 * Para procesar el request para el servicio de Login
 */
public class GetFriendsHttpAsyncTask extends HttpAsyncTask {
    protected static final String GET_FRIEND_RESULT_OK = "ok";
    protected String searchName;

    public GetFriendsHttpAsyncTask(Activity callingActivity, CallbackScreen screen, int serviceId, String searchName) {
        super(callingActivity, screen, serviceId);
        this.searchName = searchName;
    }

    @Override
    protected String getRequestMethod() {
        // Cambiando este parámetro se determina por que método se enviará el request
        return GET_REQUEST_TYPE;
    }

    @Override
    protected void configureRequestFields() {
        //this.addRequestField("searchName", this.searchName);
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
            List<User> users = new ArrayList<>();

            String result = this.getResponseField("result");
            if(result.equals(GET_FRIEND_RESULT_OK)) {
                String dataField = this.getResponseField("data");
                try {
                    String containerField = (new JSONObject(dataField)).getString("friends");
                    this.fillUsers(users, containerField, User.FRIENDSHIP_STATUS_FRIEND, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Le devolvemos a la pantalla que nos llamó todos los amigos que conseguimos
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

    protected void fillUsers(List<User> users, String containerField, String friendshipStatus, boolean isFriendshipRequest) throws JSONException {
        JSONArray jObject = new JSONArray(containerField);
        for (int i = 0; i < jObject.length(); i++) {
            JSONObject jsonObject = jObject.getJSONObject(i);

            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String userId = jsonObject.getString("userId");
            String email = jsonObject.getString("email");
            String picture = jsonObject.getString("picture");
            Location location = new Location("");
            String lastTimeUpdate = "";
            if ( jsonObject.has("longitude") && jsonObject.has("latitude") && !jsonObject.get("longitude").equals("") && !jsonObject.get("latitude").equals("") ) {

                Double longitude = jsonObject.getDouble("longitude");
                Double latitude = jsonObject.getDouble("latitude");
                location.setLongitude(longitude);
                location.setLatitude(latitude);
                lastTimeUpdate = jsonObject.getString("lastUpdateTime");
            }

            User user = new User(userId, firstName, lastName);
            user.setEmail(email);
            user.setProfilePicture(picture);
            user.setFriendshipStatus(friendshipStatus);
            if(isFriendshipRequest){
                user.setFriendshipRequestId(jsonObject.getString("friendshipRequestId"));
            }
            user.setLocation( location );
            user.setLastTimeUpdate( lastTimeUpdate );

            users.add(user);
        }
    }
}
