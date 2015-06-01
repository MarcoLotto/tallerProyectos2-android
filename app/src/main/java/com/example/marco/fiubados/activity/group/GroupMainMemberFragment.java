package com.example.marco.fiubados.activity.group;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.ComentaryFragment;
import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.KeyboardFragment;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupDiscussionsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.GroupDiscussionCreateHttpAsyncTask;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fragmento de la vista principal de un grupo para un usuario que es miembro.
 */
public class GroupMainMemberFragment extends Fragment implements CallbackScreen {

    private static final String GROUPS_SERVICE_URL = ContextManager.WS_SERVER_URL + "/api/groups/";
    private static final String CREATE_DISCUSSION_SERVICE_ENDPOINT_URL = "/discussions/";
    private static final String GET_DISCUSSIONS_ENDPOINT_URL = "/discussions/";
    private static final String GET_COMENTARIES_SERVICE_ENDPOINT = "";
    private static final String SEND_COMENTARY_SERVICE_ENDPOINT = "/comments";

    private static final int GET_DISCUSSIONS_SERVICE_ID = 0;
    private static final int CREATE_DISCUSSION_SERVICE_ID = 1;

    private ListView discussionsListView;
    private Group group;

    public GroupMainMemberFragment() {
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
        inflater.inflate(R.menu.menu_group_main_member, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new_discussion) {
            createAddDiscussionDialog(getActivity(), this);
            return true;
        }

        if (id == R.id.action_group_files) {
            startActivity(new Intent(getActivity(), GroupFilesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_main_member, container, false);

        discussionsListView = (ListView) rootView.findViewById(R.id.discussionsListView);
        group = ContextManager.getInstance().groupToView;

        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.text_view_group_description);
        descriptionTextView.setText(group.getDescription());
        int descriptionVisibility = group.getDescription().isEmpty() ? View.GONE : View.VISIBLE;
        descriptionTextView.setVisibility(descriptionVisibility);

        configureComponents();

        return rootView;
    }

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        this.discussionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onParameterClickedOnList(position);
            }
        });
    }

    private void onParameterClickedOnList(int position) {
        GroupDiscussion discussion = group.getDiscussions().get(position);
        Intent intent = new Intent(getActivity(), GroupDiscussionActivity.class);
        intent.putExtra(ComentaryFragment.EXTRA_PARAM_CONTAINER_ID, discussion.getId());
        String finalUrl = GROUPS_SERVICE_URL + group.getId() + CREATE_DISCUSSION_SERVICE_ENDPOINT_URL + discussion.getId() + GET_COMENTARIES_SERVICE_ENDPOINT;
        intent.putExtra(ComentaryFragment.EXTRA_PARAM_GET_COMENTARIES_URL, finalUrl);
        finalUrl = GROUPS_SERVICE_URL + group.getId() + CREATE_DISCUSSION_SERVICE_ENDPOINT_URL + discussion.getId() + SEND_COMENTARY_SERVICE_ENDPOINT;
        intent.putExtra(KeyboardFragment.EXTRA_PARAM_SEND_COMENTARY_URL, finalUrl);
        intent.putExtra(KeyboardFragment.EXTRA_PARAM_PARENT_COMENTARY_ID, "-1"); // Este parámetro se utilizará en el muro para comentarios anidados
        startActivity(intent);
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
        // Buscamos las discusiones del grupo
        GetGroupDiscussionsHttpAsyncTask service = new GetGroupDiscussionsHttpAsyncTask(getActivity(), this, GET_DISCUSSIONS_SERVICE_ID, group);
        String finalUrl = GROUPS_SERVICE_URL + group.getId() + GET_DISCUSSIONS_ENDPOINT_URL;
        service.execute(finalUrl);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == GET_DISCUSSIONS_SERVICE_ID){
            // Actalizamos las discusiones de la lista
            this.addDiscussionsToUIList();
        }
        else if(serviceId == CREATE_DISCUSSION_SERVICE_ID){
            this.onFocus();
        }
    }

    /*
     * Private Methods
     */

    private void addDiscussionsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        // Hacemos un reverse a la lista para que muestre las discusiones de la mas recienta a mas antigua
        List<GroupDiscussion> discussions = group.getDiscussions();
        Collections.reverse(discussions);
        for (GroupDiscussion discussion : discussions) {
            finalListViewLines.add(new DualField(new Field("Nombre", discussion.getName()), new Field("Autor", "Creado por " + discussion.getAuthor())));
        }
        this.discussionsListView.setAdapter(new TwoLinesListAdapter(getActivity().getApplicationContext(), finalListViewLines));
    }

    private void createAddDiscussionDialog(final Activity ownerActivity, final CallbackScreen ownerCallbackScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ownerActivity);
        // Get the layout inflater
        LayoutInflater inflater = ownerActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_discussion_dialog, null);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos
                        String name = ((EditText) dialogView.findViewById(R.id.fieldValueName)).getText().toString();

                        // Validamos los campos
                        if (FieldsValidator.isTextFieldValid(name, 1)) {
                            GroupDiscussion discussion = new GroupDiscussion("", name, "");
                            GroupDiscussionCreateHttpAsyncTask service = new GroupDiscussionCreateHttpAsyncTask(ownerActivity, ownerCallbackScreen, CREATE_DISCUSSION_SERVICE_ID, discussion);
                            String finalUrl = GROUPS_SERVICE_URL + group.getId() + CREATE_DISCUSSION_SERVICE_ENDPOINT_URL;
                            service.execute(finalUrl);
                        } else {
                            Toast toast = Toast.makeText(ownerActivity.getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la descripción", Toast.LENGTH_LONG);
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
