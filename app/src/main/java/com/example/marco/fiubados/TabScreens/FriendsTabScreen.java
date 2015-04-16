package com.example.marco.fiubados.TabScreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
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

    private static final String FRIENDS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends";
    private static final String FRIENDSHIP_CONFIRMATION_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends/respond_friendship_request";
    private static final String FRIENDSHIP_RESPONSE_STATUS_ACCEPT = "accept";
    private static final String FRINDSHIP_RESPONSE_STATUS_REJECT = "reject";
    private final int SEARCH_USERS_SERVICE_ID = 0;

    private TabbedActivity tabOwnerActivity;
    private List<User> users, pendingFriends;
    private ListView friendsListView, pendingFriendsListView;

    public FriendsTabScreen(TabbedActivity tabOwnerActivity, ListView friendsListView, ListView pendingFriendsListView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.friendsListView = friendsListView;
        this.pendingFriendsListView = pendingFriendsListView;
        this.users = new ArrayList<User>();
        this.pendingFriends = new ArrayList<User>();

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
        // Configuramos el handler del onClick del friendsListView
        this.pendingFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onUserClickedOnPendingFriendsList(position);
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

    private void onUserClickedOnPendingFriendsList(int position) {
        // Se hizo click en un request de amigo, abro el dialog de confirmación
        if(this.pendingFriends.size() > position) {
            User userClicked = this.pendingFriends.get(position);
            this.createDialog(this.pendingFriends.get(position)).show();
        }
    }

    @Override
    public void onFocus() {
        this.users.clear();
        this.pendingFriends.clear();

        // Vamos a hacer el pedido de amigos al web service
        SearchUsersHttpAsyncTask httpService = new SearchUsersHttpAsyncTask(this.tabOwnerActivity, this,
                SEARCH_USERS_SERVICE_ID, "TODO");
        httpService.execute(this.FRIENDS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == SEARCH_USERS_SERVICE_ID){
            this.fillUserLists(responseElements);
            this.addUsersToUserUIList(this.users, this.friendsListView);
            this.addUsersToUserUIList(this.pendingFriends, this.pendingFriendsListView);
        }
    }

    private void fillUserLists(List responseElements) {
        Iterator<User> it = responseElements.iterator();
        while(it.hasNext()){
            User user = it.next();
            switch(user.getFriendshipStatus()){
                case User.FRIENDSHIP_STATUS_FRIEND:
                    this.users.add(user);
                    break;
                case User.FRIENDSHIP_STATUS_REQUESTED:
                    // TODO
                    break;
                case User.FRIENDSHIP_STATUS_UNKNOWN:
                    this.users.add(user);
                    break;
                case User.FRIENDSHIP_STATUS_WAITING:
                    this.pendingFriends.add(user);
                    break;
            }
        }
    }

    private void addUsersToUserUIList(List<User> usersList, ListView usersListView) {
        List<String> finalListViewLines = new ArrayList<String>();
        Iterator<User> it = usersList.iterator();
        while(it.hasNext()){
            // Agregamos a la lista de amigos a todos los usuarios
            User user = it.next();
            finalListViewLines.add(user.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, finalListViewLines);
        usersListView.setAdapter(adapter);
    }

    public Dialog createDialog(User possibleFriend) {
        final User finalPossibleFriend = possibleFriend;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.tabOwnerActivity);
        // Get the layout inflater
        LayoutInflater inflater = this.tabOwnerActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_question_popup, null);

        // Asignamos los valores iniciales
        TextView fieldNameTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
        fieldNameTextView.setText("¿Desea agregar a " + possibleFriend.getName() + " como amigo?");
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        respondFriendshipRequest(finalPossibleFriend, true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        respondFriendshipRequest(finalPossibleFriend, true);
                    }
                });
        return builder.create();
    }

    private void respondFriendshipRequest(User possibleFriend, boolean accepted) {
        String status = this.FRIENDSHIP_RESPONSE_STATUS_ACCEPT;
        if(!accepted){
            status = this.FRINDSHIP_RESPONSE_STATUS_REJECT;
        }
        // Hacemos el llamado al servicio de confirmación
        FriendshipResponseHttpAsynkTask service = new FriendshipResponseHttpAsynkTask(this.tabOwnerActivity, this,
                possibleFriend.getFriendshipRequestId(), status);
        service.execute(this.FRIENDSHIP_CONFIRMATION_ENDPOINT_URL);
    }
}
