package com.example.marco.fiubados;

import com.example.marco.fiubados.model.User;

/**
 * Created by Marco on 11/04/2015.
 */
public class ContextManager {

    public static final String WS_SERVER_URL = "https://fiuba-campus-movil-sprint2.herokuapp.com";
    //public static final String WS_SERVER_URL = "http://192.168.1.100:3000";

    private static ContextManager instance = null;
    private String userToken;
    private User myUser;
    private MainScreenActivity mainScreenActivity;

    private ContextManager(){
    }

    public static ContextManager getInstance(){
        if(instance == null){
            instance = new ContextManager();
        }
        return instance;
    }

    /**
     * Devuelve el id de login del usuario
     * @return id de login del usuario
     */
    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public User getMyUser() {
        return this.myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

    public MainScreenActivity getMainScreenActivity() {
        return mainScreenActivity;
    }

    public void setMainScreenActivity(MainScreenActivity mainScreenActivity) {
        this.mainScreenActivity = mainScreenActivity;
    }
}
