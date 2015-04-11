package com.example.marco.fiubados;

/**
 * Created by Marco on 11/04/2015.
 */
public class ContextManager {

    private static ContextManager instance = null;
    private String userToken;
    private String myUser;

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
     * @return
     */
    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getMyUser() {
        return myUser;
    }

    public void setMyUser(String myUser) {
        this.myUser = myUser;
    }
}
