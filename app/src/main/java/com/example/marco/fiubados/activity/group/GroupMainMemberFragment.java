package com.example.marco.fiubados.activity.group;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.ComentaryFragment;
import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupDiscussionsHttpAsyncTask;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento de la vista principal de un grupo para un usuario que es miembro.
 */
public class GroupMainMemberFragment extends Fragment implements CallbackScreen {
    private static final int GET_DISCUSSIONS_SERVICE_ID = 0;
    private static final String GET_DISCUSSIONS_ENDPOINT_URL = "http://www.mocky.io/v2/555902e73c2e8f020b9e764f";
    private static final String GET_COMENTARIES_SERVICE_ENDPOINT = "http://www.mocky.io/v2/5560945358b174ad047eedd3";

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
            // Realizar las acciones correspondientes a nueva discusión
            return true;
        }

        if (id == R.id.action_group_files) {
            // Realizar las acciones correspondientes a ver los archivos del grupo
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
        Intent intent = new Intent(getActivity(), GroupDiscussionActivity.class);
        intent.putExtra(ComentaryFragment.EXTRA_PARAM_CONTAINER_ID, group.getDiscussions().get(position).getId());
        intent.putExtra(ComentaryFragment.EXTRA_PARAM_GET_COMENTARIES_URL, GET_COMENTARIES_SERVICE_ENDPOINT);
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
        service.execute(GET_DISCUSSIONS_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == GET_DISCUSSIONS_SERVICE_ID){
            // Actalizamos las discusiones de la lista
            this.addDiscussionsToUIList();
        }
    }

    /*
     * Private Methods
     */

    private void addDiscussionsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        for (GroupDiscussion discussion : group.getDiscussions()) {
            finalListViewLines.add(new DualField(new Field("Nombre", discussion.getName()), new Field("Autor", "Creado por " + discussion.getAuthor())));
        }
        this.discussionsListView.setAdapter(new TwoLinesListAdapter(getActivity().getApplicationContext(), finalListViewLines));
    }
}
