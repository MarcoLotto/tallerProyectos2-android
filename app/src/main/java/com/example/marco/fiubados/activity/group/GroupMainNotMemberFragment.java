package com.example.marco.fiubados.activity.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.GroupJoinHttpAsyncTask;
import com.example.marco.fiubados.model.Group;

import java.util.List;

/**
 * Fragmento de la vista principal de un grupo para un usuario que no es miembro.
 */
public class GroupMainNotMemberFragment extends Fragment implements CallbackScreen {
    private static final String LOG_TAG = GroupMainNotMemberFragment.class.getSimpleName();
    private static final int GROUP_JOIN_SERVICE_ID = 0;
    private static final String GROUPS_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/groups/";
    private static final String GROUP_JOIN_PARTIAL_URL = "/join";

    Group group;

    public GroupMainNotMemberFragment() {
        // Required empty public constructor
    }

    /*
     * Lifecycle Methods
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_main_not_member, container, false);

        group = ContextManager.getInstance().groupToView;

        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.text_view_group_description);
        descriptionTextView.setText(group.getDescription());
        int descriptionVisibility = group.getDescription().isEmpty() ? View.GONE : View.VISIBLE;
        descriptionTextView.setVisibility(descriptionVisibility);

        final Button button = (Button) rootView.findViewById(R.id.button_group_join);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                onGroupJoinAction();
            }
        });

        return rootView;
    }

    /*
     * Callback Screen Methods
     */

    @Override
    public void onFocus() {
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if (serviceId == GROUP_JOIN_SERVICE_ID) {
            onSuccessGroupJoinCallback();
        }
    }

    /*
     * Private Methods
     */

    private void onGroupJoinAction() {
        onGroupJoinService();
        //onSuccessGroupJoinCallback();
    }

    private void onGroupJoinService() {
        // Envío la peticion para unirme al grupo
        GroupJoinHttpAsyncTask joinTask = new GroupJoinHttpAsyncTask(getActivity(), this, GROUP_JOIN_SERVICE_ID);
        String endpointURL = GROUPS_ENDPOINT_URL + group.getId() + GROUP_JOIN_PARTIAL_URL;
        Log.v(LOG_TAG, "Endpoint URL:" + endpointURL);
        joinTask.execute(endpointURL);
    }

    private void onSuccessGroupJoinCallback() {
        // Create new fragment and transaction
        Fragment newFragment = new GroupMainMemberFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.group_main_container, newFragment);

        // Commit the transaction
        transaction.commit();
    }
}
