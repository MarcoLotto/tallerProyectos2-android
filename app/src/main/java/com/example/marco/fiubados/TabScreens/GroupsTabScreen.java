package com.example.marco.fiubados.TabScreens;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupsHttpAsyncTask;
import com.example.marco.fiubados.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja la logica de la pestaña de grupos
 */
public class GroupsTabScreen implements TabScreen {

    public static final String GROUPS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups";
    // public static final String GROUPS_SEARCH_ENDPOINT_URL = "http://www.mocky.io/v2/553e70fa2f711b4f27a5d287";
    private static final int SEARCH_GROUPS_SERVICE_ID = 0;

    private TabbedActivity tabOwnerActivity;
    private List<Group> groups;
    private ListView groupsListView;

    public GroupsTabScreen(TabbedActivity tabOwnerActivity, ListView groupsListView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.groupsListView = groupsListView;
        this.groups = new ArrayList<>();
    }

    @Override
    public void onFocus() {
        this.groups.clear();

        // Buscamos los grupos
        GetGroupsHttpAsyncTask friendsHttpAsyncTask = new GetGroupsHttpAsyncTask(this.tabOwnerActivity, this, SEARCH_GROUPS_SERVICE_ID);
        friendsHttpAsyncTask.execute(this.GROUPS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == SEARCH_GROUPS_SERVICE_ID){
            this.groups = responseElements;
            this.addGroupsToGroupUIList(this.groups, this.groupsListView);
        }
    }

    private void addGroupsToGroupUIList(List<Group> groupsList, ListView groupsListView) {
        List<String> finalListViewLines = new ArrayList<>();

        for (Group group : groupsList){
            finalListViewLines.add(group.getName() + " - " + group.getDescription());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this.tabOwnerActivity, android.R.layout.simple_list_item_1, finalListViewLines);
        groupsListView.setAdapter(adapter);
    }
}
