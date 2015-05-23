package com.example.marco.fiubados.activity.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.marco.fiubados.R;

/**
 * Fragmento de la vista principal de un grupo para un usuario que no es miembro.
 */
public class GroupMainNotMemberFragment extends Fragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupMainNotMemberFragment.
     */
    public static GroupMainNotMemberFragment newInstance() {
        GroupMainNotMemberFragment fragment = new GroupMainNotMemberFragment();
        return fragment;
    }

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
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}
