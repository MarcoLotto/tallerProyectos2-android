package com.example.marco.fiubados;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.FriendsTabScreen;
import com.example.marco.fiubados.TabScreens.GroupsTabScreen;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.TabScreens.WallTabScreen;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.GroupEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.JobsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.Job;
import com.example.marco.fiubados.model.User;

/**
 * Activity contenedora de todos los tabs
 */
public class MainScreenActivity extends TabbedActivity {

    private final int NEWS_TAB_INDEX = 0;
    private final int WALL_TAB_INDEX = 1;
    private final int GROUPS_TAB_INDEX = 2;
    private final int FRIENDS_TAB_INDEX = 3;

    private TabHost tabHost;
    private FriendsTabScreen friendsTabScreen;
    private WallTabScreen wallTabScreen;
    private GroupsTabScreen groupsTabScreen;
    private SearchView searchView;

    private static final String CREATE_GROUP_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups";
    private static final int CREATE_GROUP_SERVICE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ContextManager.getInstance().setMainScreenActivity(this);

        // Configuramos los tabs
        this.configureTabHost();

    }

    private void configureTabHost() {
        this.tabHost = (TabHost) findViewById(R.id.tabHost);
        this.tabHost.setup();
        this.addTabSpectToTabHost(this.tabHost, "Inicio", getResources().getDrawable(R.drawable.ic_action_news_holo_light), R.id.TabInicio);  // INICIO
        this.addTabSpectToTabHost(this.tabHost, "Muro", getResources().getDrawable(R.drawable.ic_action_chat_holo_light), R.id.TabMuro);      // MURO
        this.addTabSpectToTabHost(this.tabHost, "Grupos", getResources().getDrawable(R.drawable.ic_action_group_holo_light), R.id.TabGrupos);  // GRUPOS
        this.addTabSpectToTabHost(this.tabHost, "Amigos", getResources().getDrawable(R.drawable.ic_action_person_holo_light), R.id.TabAmigos);  // AMIGOS

        // Inicializamos los controladores de los tabs
        Button addFriendButton = (Button) findViewById(R.id.addFriendButton);
        Button sendFriendRequestButton = (Button) findViewById(R.id.sendFriendRequestButton);
        TextView wallTitleTextView = (TextView) findViewById(R.id.wallTitleTextView);
        this.wallTabScreen = new WallTabScreen(this, addFriendButton, sendFriendRequestButton, wallTitleTextView);
        ListView friendsListView = (ListView) findViewById(R.id.friendsListView);
        this.friendsTabScreen = new FriendsTabScreen(this, friendsListView);
        ListView groupsListView = (ListView) findViewById(R.id.groupsListView);
        this.groupsTabScreen = new GroupsTabScreen(this, groupsListView);

        this.tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                handleTabChange();
            }
        });
    }

    /**
     * Maneja el cambio de pestañas
     */
    private void handleTabChange() {
        int currentTabIndex = this.tabHost.getCurrentTab();
        switch(currentTabIndex){
            case 0:
                this.wallTabScreen.setUserOwnerOfTheWall(ContextManager.getInstance().getMyUser());
                // Tab de inicio
                break;
            case 1:
                // Tab de muro
                this.wallTabScreen.onFocus();
                break;
            case 2:
                // Tab de grupos
                this.wallTabScreen.setUserOwnerOfTheWall(ContextManager.getInstance().getMyUser());
                this.groupsTabScreen.onFocus();
                break;
            case 3:
                this.wallTabScreen.setUserOwnerOfTheWall(ContextManager.getInstance().getMyUser());
                // Tab de amigos
                this.friendsTabScreen.onFocus();
                break;
        };
        this.invalidateOptionsMenu();
    }

    private void addTabSpectToTabHost(TabHost tabHost, String tabLabel, Drawable icon, int tabId) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabLabel);
        tabSpec.setContent(tabId);
        tabSpec.setIndicator("", icon);
        tabHost.addTab(tabSpec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        this.searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        this.searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public TabScreen getNewsTabScreen() {
        return null;  // TODO
    }

    @Override
    public TabScreen getGroupsTabScreen() {
        return this.groupsTabScreen;
    }

    @Override
    public FriendsTabScreen getFriendsTabScreen() {
        return this.friendsTabScreen;
    }

    @Override
    public WallTabScreen getWallTabScreen() {
        return this.wallTabScreen;
    }

    @Override
    public void selectNewsTabScreen() {
        this.tabHost.setCurrentTab(NEWS_TAB_INDEX);
    }

    @Override
    public void selectGroupsTabScreen() {
        this.tabHost.setCurrentTab(GROUPS_TAB_INDEX);
    }

    @Override
    public void selectFriendsTabScreen() {
        this.tabHost.setCurrentTab(FRIENDS_TAB_INDEX);
    }

    @Override
    public void selectWallTabScreen() {
        this.tabHost.setCurrentTab(WALL_TAB_INDEX);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.notificationAction:
                 return this.openNotificationsActivity();
            case R.id.profileAction:
                return this.openProfileActivity();
            case R.id.action_search:
                return this.onSearchRequested();
            case R.id.addAction:
                return this.openAddGroupActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean openNotificationsActivity() {
        Intent intent = new Intent(this, NotificationsActivity.class);
        this.startActivity(intent);
        return true;
    }

    private boolean openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        // Le pasamos al activity de profile el usuario que esta actualmente en el muro
        User currentWallUser = this.wallTabScreen.getUserOwnerOfTheWall();
        if(currentWallUser != null) {
            intent.putExtra(ProfileActivity.USER_ID_PARAMETER, currentWallUser.getId());
        }
        else{
            intent.putExtra(ProfileActivity.USER_ID_PARAMETER, ContextManager.getInstance().getMyUser().getId());
        }
        this.startActivity(intent);
        return true;
    }

    private boolean openAddGroupActivity() {
        //Intent intent = new Intent(this, GroupCreateActivity.class);
        //this.startActivity(intent);
        this.createAddGroupDialog(this, this.groupsTabScreen);
        return true;
    }

    public void createAddGroupDialog(final Activity ownerActivity, final TabScreen ownerTabScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_group_dialog, null);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos
                        String groupName = ((EditText) dialogView.findViewById(R.id.fieldValueName)).getText().toString();
                        String groupDescription = ((EditText) dialogView.findViewById(R.id.fieldValueDescription)).getText().toString();

                        // Validamos los campos
                        if (FieldsValidator.isTextFieldValid(groupName, 1)) {
                            Group group = new Group("", groupName, groupDescription);
                            GroupEditAndCreateHttpAsyncTask service = new GroupEditAndCreateHttpAsyncTask(ownerActivity, ownerTabScreen, CREATE_GROUP_SERVICE_ID, group);
                            service.execute(CREATE_GROUP_SERVICE_ENDPOINT_URL);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la descripcion del grupo", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hace falta hacer ninguna acción
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Solo mostramos el boton de perfil si estamos en la pantalla de muro
        boolean isWallTagActive = this.tabHost.getCurrentTab() == WALL_TAB_INDEX;
        menu.findItem(R.id.profileAction).setVisible(isWallTagActive);

        // El icono de notificaciones lo mostramos siempre
        menu.findItem(R.id.notificationAction).setVisible(true);

        // El icono de busquedas
        menu.findItem(R.id.action_search).setVisible(true);

        // Mostramos el boton de agregar grupo si estamos en la pantalla de grupos
        boolean isGroupTagActive = this.tabHost.getCurrentTab() == GROUPS_TAB_INDEX;
        menu.findItem(R.id.addAction).setVisible(isGroupTagActive);

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.handleTabChange();
    }
}
