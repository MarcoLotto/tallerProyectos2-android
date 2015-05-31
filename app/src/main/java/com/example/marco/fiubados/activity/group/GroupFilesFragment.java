package com.example.marco.fiubados.activity.group;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marco.fiubados.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupFilesFragment extends Fragment {

    public GroupFilesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_files, container, false);
    }
}
