package com.example.marco.fiubados.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.activity.group.GroupMainActivity;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.GroupEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento de la pestaña de grupos
 */
public class GroupsTabFragment extends Fragment implements CallbackScreen {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String GROUPS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups";
    private static final String CREATE_GROUP_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups";
    private static final int SEARCH_GROUPS_SERVICE_ID = 0;
    private static final int CREATE_GROUP_SERVICE_ID = 1;

    private List<Group> mGroups;
    private ArrayAdapter<String> mGroupsAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GroupsTabFragment newInstance(int sectionNumber) {
        GroupsTabFragment fragment = new GroupsTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupsTabFragment() {
        mGroups = new ArrayList<>();
    }

    /*
     * Lifecycle Methods
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_groups_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Hace click en busqueda de grupos
        if (id == R.id.action_search_groups) {
            // TODO
            return true;
        }

        // Hace click en nuevo grupo
        if (id == R.id.action_new_group) {
            openAddGroupDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups_tab, container, false);

        mGroupsAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        android.R.layout.simple_list_item_1, // The name of the layout ID.
                        new ArrayList<String>());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_user_groups);
        listView.setAdapter(mGroupsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onGroupItemClick(position);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFocus();
    }

    /*
     * Callback Screen Methods
     */

    @Override
    public void onFocus() {
        // Buscamos los grupos
        GetGroupsHttpAsyncTask groupsTask = new GetGroupsHttpAsyncTask(getActivity(), this, SEARCH_GROUPS_SERVICE_ID);
        groupsTask.execute(GROUPS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if (serviceId == SEARCH_GROUPS_SERVICE_ID || serviceId == CREATE_GROUP_SERVICE_ID) {
            if (responseElements != null) {
                mGroups.clear();
                mGroups = responseElements;
                addGroupsToGroupUIList();
            }
        }
    }

    private void addGroupsToGroupUIList() {
        mGroupsAdapter.clear();

        for (Group group : mGroups) {
            mGroupsAdapter.add(group.getName());
        }
    }

    private void onGroupItemClick(int position) {
        if (position < mGroups.size()) {
            ContextManager.getInstance().groupToView = mGroups.get(position);
            Intent intent = new Intent(getActivity(), GroupMainActivity.class);
            getActivity().startActivity(intent);
        }
    }

    private boolean openAddGroupDialog() {
        this.createAddGroupDialog(getActivity(), this);
        return true;
    }

    public void createAddGroupDialog(final Activity ownerActivity, final CallbackScreen ownerCallbackScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                            GroupEditAndCreateHttpAsyncTask service = new GroupEditAndCreateHttpAsyncTask(ownerActivity, ownerCallbackScreen, CREATE_GROUP_SERVICE_ID, group);
                            service.execute(CREATE_GROUP_SERVICE_ENDPOINT_URL);
                        } else {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la descripcion del grupo", Toast.LENGTH_LONG);
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

}
