package com.example.marco.fiubados;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.FriendsTabScreen;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
import com.example.marco.fiubados.httpAsyncTasks.GetFriendsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.GetPendingRequestsHttpAsyncTask;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class NotificationsActivity extends ActionBarActivity implements TabScreen {

    private static final int GET_PENDING_FRIEND_REQUESTS_USERS_SERVICE_ID = 0;
    private static final String PENDING_FRIEND_REQUESTS_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends/pending_friendship_requests";
    public static final String FRIENDSHIP_CONFIRMATION_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends/respond_friendship_request";
    public static final String FRIENDSHIP_RESPONSE_STATUS_ACCEPT = "accept";
    public static final String FRINDSHIP_RESPONSE_STATUS_REJECT = "reject";

    private List<User> pendingFriends = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ListView notificationsListView = (ListView) findViewById(R.id.notificationsListView);
        // Configuramos el handler del onClick del friendsListView
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onUserClickedOnPendingFriendsList(position);
            }
        });
        this.onFocus();
    }

    private void onUserClickedOnPendingFriendsList(int position) {
        // Se hizo click en un request de amigo, abro el dialog de confirmación
        if(this.pendingFriends.size() > position) {
            User userClicked = this.pendingFriends.get(position);
            this.createDialog(this.pendingFriends.get(position)).show();
        }
    }

    public Dialog createDialog(User possibleFriend) {
        final User finalPossibleFriend = possibleFriend;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
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
                .setNegativeButton(R.string.notAccept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        respondFriendshipRequest(finalPossibleFriend, false);
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
        FriendshipResponseHttpAsynkTask service = new FriendshipResponseHttpAsynkTask(this, this,
                possibleFriend.getFriendshipRequestId(), status);
        service.execute(this.FRIENDSHIP_CONFIRMATION_ENDPOINT_URL);
    }

    @Override
    public void onFocus() {
       this.pendingFriends.clear();

        // Vamos a hacer el pedido de amigos al web service
        GetPendingRequestsHttpAsyncTask httpService = new GetPendingRequestsHttpAsyncTask(this, this, this.GET_PENDING_FRIEND_REQUESTS_USERS_SERVICE_ID, "TODO");
        httpService.execute(this.PENDING_FRIEND_REQUESTS_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == GET_PENDING_FRIEND_REQUESTS_USERS_SERVICE_ID){
            this.fillUserLists(responseElements);
            ListView notificationsListView = (ListView) findViewById(R.id.notificationsListView);
            this.addUsersToUserUIList(this.pendingFriends, notificationsListView);
        }
    }

    private void fillUserLists(List responseElements) {
        Iterator<User> it = responseElements.iterator();
        while(it.hasNext()){
            User user = it.next();
            switch(user.getFriendshipStatus()){
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
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        usersListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
