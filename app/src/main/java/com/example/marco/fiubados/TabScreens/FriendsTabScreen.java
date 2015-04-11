package com.example.marco.fiubados.TabScreens;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.HttpAsyncTask;
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

    private final int SEARCH_USERS_SERVICE_ID = 0;

    private TabbedActivity tabOwnerActivity;
    private List<User> users;
    private ListView friendsListView;

    public FriendsTabScreen(TabbedActivity tabOwnerActivity, ListView friendsListView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.friendsListView = friendsListView;

        this.configureComponents();
    }

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        this.friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onUserClickedOnList(position);
            }
        });
    }

    private void onUserClickedOnList(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if(this.users.size() > position) {
            User userClicked = this.users.get(position);
            this.tabOwnerActivity.getWallTabScreen().setUserOwnerOfTheWall(userClicked);
            this.tabOwnerActivity.selectWallTabScreen();
        }
    }

    @Override
    public void onFocus() {
        // Vamos a hacer el pedido de amigos al web service
        SearchUsersHttpAsyncTask httpService = new SearchUsersHttpAsyncTask(this.tabOwnerActivity, this,
                SEARCH_USERS_SERVICE_ID, "TODO", ContextManager.getInstance().getMyUser().getId());
        httpService.execute("http://www.mocky.io/v2/55299ffa22258fe502a378b0");
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == SEARCH_USERS_SERVICE_ID){
            this.users = (List<User>) responseElements;
            this.addUsersToUserUIList();
        }
    }

    private void addUsersToUserUIList() {
        List<String> finalListViewLines = new ArrayList<String>();
        Iterator<User> it = this.users.iterator();
        while(it.hasNext()){
            // Agregamos a la lista de amigos a todos los usuarios
            User user = it.next();
            String appender = "";
            if(!user.getFriendshipStatus().isEmpty()){
                appender = " - " + user.getFriendshipStatus();
            }
            finalListViewLines.add(user.getName() + appender);
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, finalListViewLines);
        this.friendsListView.setAdapter(adapter);
    }
}
