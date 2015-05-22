package com.example.marco.fiubados;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.activity.group.GroupMainActivity;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.httpAsyncTasks.SearchGroupsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SearchUsersHttpAsyncTask;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SearchUsersActivity extends AppCompatActivity implements CallbackScreen {

    public static final String SEARCH_TYPE = "search_type";
    public static final int SEARCH_TYPE_USERS = 0;
    public static final int SEARCH_TYPE_GROUPS = 1;

    private static final int SEARCH_USERS_SERVICE_ID = 0;
    private static final int SEARCH_GROUPS_SERVICE_ID = 1;
    private static final String SEARCH_USERS_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/search";
    private static final String SEARCH_GROUPS_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups/search";


    private String queryText;
    private int searchType;
    private List<User> users = new ArrayList<>();;
    private List<Group> groups = new ArrayList<>();

    private ListView listView;

    /* ************************* *
     * ANDROID LIFECYCLE METHODS *
     * ************************* */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        listView = (ListView) findViewById(R.id.usersListView);

        // Recupero el query que debo mandar a buscar
        if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            queryText = getIntent().getStringExtra(SearchManager.QUERY);
            Bundle searchData = getIntent().getBundleExtra(SearchManager.APP_DATA);
            searchType = searchData.getInt(SEARCH_TYPE, SEARCH_TYPE_USERS);
        }
        configureComponents();
        onFocus();
    }

    /* *********************** *
     * CALLBACK SCREEN METHODS *
     * *********************** */

    @Override
    public void onFocus() {
        // Hacemos la llamada al servicio de busqueda
        switch (searchType) {
            case SEARCH_TYPE_USERS: {
                SearchUsersHttpAsyncTask service = new SearchUsersHttpAsyncTask(this, this, SEARCH_USERS_SERVICE_ID, queryText);
                service.execute(SEARCH_USERS_SERVICE_ENDPOINT_URL);
                break;
            }
            case SEARCH_TYPE_GROUPS: {
                SearchGroupsHttpAsyncTask service = new SearchGroupsHttpAsyncTask(this, this, SEARCH_GROUPS_SERVICE_ID, queryText);
                service.execute(SEARCH_GROUPS_SERVICE_ENDPOINT_URL);
                break;
            }
        }
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        switch (serviceId) {
            case SEARCH_USERS_SERVICE_ID: {
                users = responseElements;
                addUsersToListView();

                if (users.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No hay contactos para la búsqueda", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            }
            case SEARCH_GROUPS_SERVICE_ID: {
                groups = responseElements;
                addGroupsToListView();

                if (groups.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No hay grupos para la búsqueda", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            }
        }
    }

    /* *************** *
     * PRIVATE METHODS *
     * *************** */

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (searchType) {
                    case SEARCH_TYPE_USERS:
                        onUserItemClick(position);
                        break;
                    case SEARCH_TYPE_GROUPS:
                        onGroupItemClick(position);
                        break;
                }
            }
        });
    }

    private void addUsersToListView() {
        List<DualField> finalListViewLines = new ArrayList<>();
        Iterator<User> it = users.iterator();
        while(it.hasNext()){
            // Agregamos a la lista de amigos a todos los usuarios
            User user = it.next();
            finalListViewLines.add(new DualField(new Field("Nombre", user.getFullName()), new Field("SearchDesc", user.getMatchParameter())));
        }
        listView.setAdapter(new TwoLinesListAdapter(getApplicationContext(), finalListViewLines));
    }

    private void addGroupsToListView() {
        List<DualField> finalListViewLines = new ArrayList<>();
        Iterator<Group> it = groups.iterator();
        while (it.hasNext()) {
            // Agregamos a la lista de amigos a todos los usuarios
            Group group = it.next();
            finalListViewLines.add(new DualField(new Field("Nombre", group.getName()), new Field("SearchDesc", "")));
        }
        listView.setAdapter(new TwoLinesListAdapter(getApplicationContext(), finalListViewLines));
    }

    private void onUserItemClick(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if (position < users.size()) {
            User userClicked = users.get(position);
            MainScreenActivity mainScreenActivity = ContextManager.getInstance().getMainScreenActivity();
            mainScreenActivity.getWallTabScreen().setUserOwnerOfTheWall(userClicked);
            mainScreenActivity.selectWallTabScreen();
            finish();
        }
    }

    private void onGroupItemClick(int position) {
        // Se hizo click en un grupo
        if (position < groups.size()) {
            MainScreenActivity mainScreenActivity = ContextManager.getInstance().getMainScreenActivity();
            ContextManager.getInstance().groupToView = groups.get(position);
            Intent intent = new Intent(mainScreenActivity, GroupMainActivity.class);
            mainScreenActivity.startActivity(intent);
            finish();
        }
    }
}
