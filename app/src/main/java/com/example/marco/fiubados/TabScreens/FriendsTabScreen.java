package com.example.marco.fiubados.TabScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
import com.example.marco.fiubados.httpAsyncTasks.GetFriendsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SearchUsersHttpAsyncTask;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 07/04/2015.
 *
 * Maneja la lógica interna del tab de listado de amigos
 */
public class FriendsTabScreen implements TabScreen{

    public static final String FRIENDS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends";
    private static final String SEARCH_USERS_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/search";
    private final int SEARCH_USERS_SERVICE_ID = 0;
    private static final int SEARCH_FRIENDS_SERVICE_ID = 1;

    private TabbedActivity tabOwnerActivity;
    private List<User> users;
    private ListView friendsListView;

    public FriendsTabScreen(TabbedActivity tabOwnerActivity, ListView friendsListView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.friendsListView = friendsListView;
        this.users = new ArrayList<User>();
        this.configureComponents();
    }

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        this.friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onUserClickedOnFriendsList(position);
            }
        });
    }

    private void onUserClickedOnFriendsList(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if(this.users.size() > position) {
            User userClicked = this.users.get(position);
            this.tabOwnerActivity.getWallTabScreen().setUserOwnerOfTheWall(userClicked);
            this.tabOwnerActivity.selectWallTabScreen();
        }
    }

    @Override
    public void onFocus() {
        this.users.clear();

        // Vamos a hacer el pedido de busqueda de usuarios
        SearchUsersHttpAsyncTask searchUsersHttpService = new SearchUsersHttpAsyncTask(this.tabOwnerActivity, this,
                SEARCH_USERS_SERVICE_ID, "TODO");
        searchUsersHttpService.execute(this.SEARCH_USERS_ENDPOINT_URL);

        // Vamos a hacer el pedido de amigos al web service
        GetFriendsHttpAsyncTask friendsHttpService = new GetFriendsHttpAsyncTask(this.tabOwnerActivity, this,
                this.SEARCH_FRIENDS_SERVICE_ID, "TODO");
        friendsHttpService.execute(this.FRIENDS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_USERS_SERVICE_ID){
            this.fillUserLists(responseElements);
        }
        if(serviceId == this.SEARCH_FRIENDS_SERVICE_ID){
            Iterator<User> it = this.users.iterator();
            while(it.hasNext()){
                User user = it.next();
                if(responseElements.contains(user)){
                    // Mi usuario y este usuario son amigos
                    user.setFriendshipStatus(User.FRIENDSHIP_STATUS_FRIEND);
                }
            }
            this.addUsersToUserUIList(this.users, this.friendsListView);
        }
    }

    private void fillUserLists(List responseElements) {
        Iterator<User> it = responseElements.iterator();
        while(it.hasNext()){
            this.users.add(it.next());
        }
    }

    private void addUsersToUserUIList(List<User> usersList, ListView usersListView) {
        List<String> finalListViewLines = new ArrayList<String>();
        Iterator<User> it = usersList.iterator();
        while(it.hasNext()){
            // Agregamos a la lista de amigos a todos los usuarios
            User user = it.next();
            String finalString = user.getName();
            if(user.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_FRIEND)){
                finalString += " - Amigo";
            }
            finalListViewLines.add(finalString);
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, finalListViewLines);
        usersListView.setAdapter(adapter);
    }
}
