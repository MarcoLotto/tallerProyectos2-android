package com.example.marco.fiubados.TabScreens;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.GetFriendsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SearchFriendsHttpAsyncTask;
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
    private static final int SEARCH_FRIENDS_SERVICE_ID = 0;

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

        // Vamos a hacer el pedido de amigos al web service
        GetFriendsHttpAsyncTask friendsHttpService = new GetFriendsHttpAsyncTask(this.tabOwnerActivity, this,
                this.SEARCH_FRIENDS_SERVICE_ID, "TODO");
        friendsHttpService.execute(this.FRIENDS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_FRIENDS_SERVICE_ID){
            this.users = responseElements;
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
            finalListViewLines.add(user.getName() + " " + user.getLastName());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, finalListViewLines);
        usersListView.setAdapter(adapter);
    }
}
