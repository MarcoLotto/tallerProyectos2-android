package com.example.marco.fiubados.TabScreens;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.httpAsyncTasks.HttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SearchUsersHttpAsyncTask;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 07/04/2015.
 */
public class FriendsTabScreen implements TabScreen{

    private final int SEARCH_USERS_SERVICE_ID = 0;

    private Activity tabOwnerActivity;
    private List<User> users;
    private ListView friendsListView;

    public FriendsTabScreen(Activity tabOwnerActivity, ListView friendsListView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.friendsListView = friendsListView;
    }

    @Override
    public void onFocus() {
        // Vamos a hacer el pedido de amigos al web service
        SearchUsersHttpAsyncTask httpService = new SearchUsersHttpAsyncTask(this.tabOwnerActivity, this, SEARCH_USERS_SERVICE_ID);
        httpService.execute("http://www.mocky.io/v2/55244275cb8408900ad8888d");
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
            finalListViewLines.add(user.getName());  // REVIEW: Se puede agregar el estatus de amistad
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, finalListViewLines);
        this.friendsListView.setAdapter(adapter);
    }
}
