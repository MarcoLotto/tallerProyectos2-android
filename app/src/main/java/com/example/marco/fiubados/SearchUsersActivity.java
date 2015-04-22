package com.example.marco.fiubados;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.httpAsyncTasks.SearchUsersHttpAsyncTask;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SearchUsersActivity extends ActionBarActivity implements TabScreen {

    private static final int SEARCH_USERS_SERVICE_ID = 0;
    private static final String SEARCH_USERS_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/search";
    private String queryText;
    private List<User> users;
    private ListView usersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        this.users = new ArrayList<User>();
        this.usersListView = (ListView) this.findViewById(R.id.usersListView);

        // Recupero el query que debo mandar a buscar
        if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            this.queryText = this.getIntent().getStringExtra(SearchManager.QUERY);
        }
        this.configureComponents();
        this.onFocus();
    }

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        this.usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onUserClickedOnUserList(position);
            }
        });
    }

    private void onUserClickedOnUserList(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if(this.users.size() > position) {
            User userClicked = this.users.get(position);
            MainScreenActivity mainScreenActivity = ContextManager.getInstance().getMainScreenActivity();
            mainScreenActivity.getWallTabScreen().setUserOwnerOfTheWall(userClicked);
            mainScreenActivity.selectWallTabScreen();
            this.finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocus() {
        // Hacemos la llamada al servicio de busqueda
        SearchUsersHttpAsyncTask service = new SearchUsersHttpAsyncTask(this, this, this.SEARCH_USERS_SERVICE_ID, this.queryText);
        service.execute(this.SEARCH_USERS_SERVICE_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_USERS_SERVICE_ID){
            this.users = responseElements;
            Iterator<User> it = this.users.iterator();
            while(it.hasNext()){
                User user = it.next();
                if(responseElements.contains(user)){
                    // Mi usuario y este usuario son amigos
                    user.setFriendshipStatus(User.FRIENDSHIP_STATUS_FRIEND);
                }
            }
            this.addUsersToUserUIList(this.users, this.usersListView);
        }
    }

    private void addUsersToUserUIList(List<User> usersList, ListView usersListView) {
        List<DualField> finalListViewLines = new ArrayList<DualField>();
        Iterator<User> it = usersList.iterator();
        while(it.hasNext()){
            // Agregamos a la lista de amigos a todos los usuarios
            User user = it.next();
            String finalString = user.getName();
            finalListViewLines.add(new DualField(new Field("Nombre", user.getName()), new Field("SearchDesc", "TODO")));
        }
        usersListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }
}
