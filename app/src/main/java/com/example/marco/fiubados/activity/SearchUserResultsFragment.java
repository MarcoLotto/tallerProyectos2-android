package com.example.marco.fiubados.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marco.fiubados.R;

/**
 *
 */
public class SearchUserResultsFragment extends Fragment {

    /**
     *
     */
    // TODO: Rename and change types and number of parameters
    public static SearchUserResultsFragment newInstance() {
        SearchUserResultsFragment fragment = new SearchUserResultsFragment();
        return fragment;
    }

    public SearchUserResultsFragment() {
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
        return inflater.inflate(R.layout.fragment_search_user_results, container, false);
    }

}