package com.example.marco.fiubados.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.GetFriendsHttpAsyncTask;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class FriendsTabFragment extends Fragment implements CallbackScreen {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String FRIENDS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends";
    private static final int SEARCH_FRIENDS_SERVICE_ID = 0;

    private List<User> mFriends;
    private ArrayAdapter<String> mFriendsAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FriendsTabFragment newInstance(int sectionNumber) {
        FriendsTabFragment fragment = new FriendsTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsTabFragment() {
        mFriends = new ArrayList<>();
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
        inflater.inflate(R.menu.menu_friends_fragment, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search_users));
        searchView.setQueryHint(getString(R.string.search_users_hint));
        Bundle searchData = new Bundle();
        searchData.putString("type_search", "users");
        searchView.setAppSearchData(searchData);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Hace click en busqueda de usuarios
        if (id == R.id.action_search_users) {
            return getActivity().onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_tab, container, false);

        mFriendsAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        android.R.layout.simple_list_item_1, // The name of the layout ID.
                        new ArrayList<String>());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_friends);
        listView.setAdapter(mFriendsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onFriendItemClick(position);
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
        // Vamos a hacer el pedido de amigos al web service
        GetFriendsHttpAsyncTask friendsHttpService = new GetFriendsHttpAsyncTask(getActivity(), this, SEARCH_FRIENDS_SERVICE_ID, "TODO");
        friendsHttpService.execute(FRIENDS_SEARCH_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == SEARCH_FRIENDS_SERVICE_ID) {
            if (responseElements != null) {
                mFriends.clear();
                mFriends = responseElements;
                Iterator<User> it = mFriends.iterator();
                while (it.hasNext()) {
                    User user = it.next();
                    if (responseElements.contains(user)) {
                        // Mi usuario y este usuario son amigos
                        user.setFriendshipStatus(User.FRIENDSHIP_STATUS_FRIEND);
                    }
                }
                addUsersToUserUIList();
            }
        }
    }

    /*
     * Private Methods
     */

    private void addUsersToUserUIList() {
        mFriendsAdapter.clear();

        for (User user : mFriends) {
            mFriendsAdapter.add(user.getFullName());
        }
    }

    private void onFriendItemClick(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if(position < mFriends.size()) {
            User userClicked = mFriends.get(position);
            // FIXME
            //this.tabOwnerActivity.getWallTabScreen().setUserOwnerOfTheWall(userClicked);
            //this.tabOwnerActivity.selectWallTabScreen();
        }
    }

}
