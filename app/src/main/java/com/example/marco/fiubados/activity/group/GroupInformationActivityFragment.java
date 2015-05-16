package com.example.marco.fiubados.activity.group;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.model.Group;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupInformationActivityFragment extends Fragment {

    public GroupInformationActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_information, container, false);

        Group group = ContextManager.getInstance().groupToView;
        ((TextView) rootView.findViewById(R.id.group_information_name)).setText(group.getName());
        ((TextView) rootView.findViewById(R.id.group_information_description)).setText(group.getDescription());
        return rootView;
    }
}
