package com.example.marco.fiubados.activity.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.model.Group;

/**
 * Fragmento de la vista principal de un grupo para un usuario que no es miembro.
 */
public class GroupMainNotMemberFragment extends Fragment {

    public GroupMainNotMemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_main_not_member, container, false);

        Group group = ContextManager.getInstance().groupToView;

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

    private void onGroupJoinAction() {
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
