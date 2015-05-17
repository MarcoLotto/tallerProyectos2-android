package com.example.marco.fiubados.activity.group;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupMembersHttpAsyncTask;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupMembersActivityFragment extends Fragment implements CallbackScreen {
    private static final String LOG_TAG = GroupMembersActivityFragment.class.getSimpleName();
    private static final int GROUP_MEMBERS_SERVICE_ID = 0;
    public static final String GROUPS_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups";
    public static final String GROUP_MEMBERS_PARTIAL_URL = "/members";

    private List<User> members = new ArrayList<>();
    private ArrayAdapter<String> mGroupMembersAdapter;

    public GroupMembersActivityFragment() {
    }

    /*
     * Lifecycle Methods
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_members, container, false);

        mGroupMembersAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        android.R.layout.simple_list_item_1, // The name of the layout ID.
                        new ArrayList<String>());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_group_members);
        listView.setAdapter(mGroupMembersAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onFocus();
    }

    /*
     * Callback Screen Methods
     */

    @Override
    public void onFocus() {
        this.members.clear();
        String groupID = ContextManager.getInstance().groupToView.getId();
        GetGroupMembersHttpAsyncTask membersTask = new GetGroupMembersHttpAsyncTask(this.getActivity(), this, GROUP_MEMBERS_SERVICE_ID);
        String endpointURL = GROUPS_ENDPOINT_URL + "/" + groupID + GROUP_MEMBERS_PARTIAL_URL;
        Log.v(LOG_TAG, "Endpoint URL:" + endpointURL);
        membersTask.execute(endpointURL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if (serviceId == GROUP_MEMBERS_SERVICE_ID) {
            this.members = responseElements;
            this.addMembersToUIList();
        }
    }

    /*
     * Private Methods
     */

    private void addMembersToUIList() {
        mGroupMembersAdapter.clear();
        for (User user : this.members) {
            mGroupMembersAdapter.add(user.getFullName());
        }
    }
}
