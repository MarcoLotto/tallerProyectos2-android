package com.example.marco.fiubados.TabScreens;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.activity.group.GroupMainActivity;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SendWallNotificationHttpAsyncTask;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja la logica de la pestaña de grupos
 */
public class GroupsTabScreen implements CallbackScreen {

    public static final String GROUPS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups";
    private static final String NOTIFICATIONS_SERVICE_URL = ContextManager.WS_SERVER_URL + "/api/users/";
    private static final String SEND_NOTIFICATION_ENDPOINT = "/wall/notifications";
    // public static final String GROUPS_SEARCH_ENDPOINT_URL = "http://www.mocky.io/v2/553e70fa2f711b4f27a5d287";

    private static final int SEARCH_GROUPS_SERVICE_ID = 0;
    private static final int CREATE_GROUP_SERVICE_ID = 1;
    private static final int SEND_WALL_NOTIFICATION_SERVICE_ID = 2;

    private static final String NEW_GROUP_MESSAGE = " creó un nuevo grupo";


    private TabbedActivity tabOwnerActivity;
    private List<Group> groups;
    private ListView groupsListView;

    public GroupsTabScreen(TabbedActivity tabOwnerActivity, ListView groupsListView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.groupsListView = groupsListView;
        this.groups = new ArrayList<>();
        this.configureClicks();
    }

    private void configureClicks() {
        this.groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onGroupItemClick(position);
            }
        });
    }

    @Override
    public void onFocus() {
        // Buscamos los grupos
        GetGroupsHttpAsyncTask groupsTask = new GetGroupsHttpAsyncTask(this.tabOwnerActivity, this, SEARCH_GROUPS_SERVICE_ID);
        groupsTask.execute(GROUPS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if (serviceId == SEARCH_GROUPS_SERVICE_ID || serviceId == CREATE_GROUP_SERVICE_ID) {
            this.groups = responseElements;
            this.addGroupsToGroupUIList();
        }

        // Si es de creacion de grupo, publicamos una notificacion
        if (serviceId == CREATE_GROUP_SERVICE_ID){
            User myUser = ContextManager.getInstance().getMyUser();

            SendWallNotificationHttpAsyncTask service = new SendWallNotificationHttpAsyncTask(this.tabOwnerActivity,
                    this, SEND_WALL_NOTIFICATION_SERVICE_ID, myUser.getFirstName() + NEW_GROUP_MESSAGE,
                    myUser.getId(), myUser.getFirstName() + NEW_GROUP_MESSAGE);
            service.execute(NOTIFICATIONS_SERVICE_URL + myUser.getId() + SEND_NOTIFICATION_ENDPOINT);
        }
    }

    private void addGroupsToGroupUIList() {
        List<String> groupStrings = new ArrayList<>();

        for (Group group : this.groups){
            groupStrings.add(group.getName());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, groupStrings);
        this.groupsListView.setAdapter(adapter);
    }

    private void onGroupItemClick(int position) {
        if (position < this.groups.size()) {
            ContextManager.getInstance().groupToView = this.groups.get(position);
            Intent intent = new Intent(this.tabOwnerActivity, GroupMainActivity.class);
            this.tabOwnerActivity.startActivity(intent);
        }
    }

}
