package com.example.marco.fiubados.activity.group;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.util.Iterator;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupMainActivityFragment extends Fragment implements CallbackScreen {
    private static final int GET_DISCUSSIONS_SERVICE_ID = 0;
    private static final String GET_DISCUSSIONS_ENDPOINT_URL = "http://www.mocky.io/v2/555902e73c2e8f020b9e764f";

    private ListView discussionsListView;
    private Group group;

    public GroupMainActivityFragment() {
    }

    /*
     * Lifecycle Methods
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_main, container, false);

        this.discussionsListView = (ListView) rootView.findViewById(R.id.discussionsListView);
        this.group = ContextManager.getInstance().groupToView;

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
        // Buscamos las discuciones del grupo
        GetGroupDiscussionsHttpAsyncTask service = new GetGroupDiscussionsHttpAsyncTask(getActivity(), this, this.GET_DISCUSSIONS_SERVICE_ID, this.group);
        service.execute(this.GET_DISCUSSIONS_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.GET_DISCUSSIONS_SERVICE_ID){
            // Actalizamos las discusiones de la lista
            this.addDiscussionsToUIList();
        }
    }

    /*
     * Private Methods
     */

    private void addDiscussionsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        Iterator<GroupDiscussion> it = this.group.getDiscussions().iterator();
        while(it.hasNext()){
            GroupDiscussion discussion = it.next();
            finalListViewLines.add(new DualField(new Field("Nombre", discussion.getName()), new Field("Autor", "Creado por " + discussion.getAuthor())));
        }
        this.discussionsListView.setAdapter(new TwoLinesListAdapter(getActivity().getApplicationContext(), finalListViewLines));
    }
}
