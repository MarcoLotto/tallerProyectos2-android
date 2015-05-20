package com.example.marco.fiubados.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.ProfileActivity;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.model.User;

/**
 * Fragmento de la vista de Muro de MainActivity.
 */
public class WallTabFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WallTabFragment newInstance(int sectionNumber) {
        WallTabFragment fragment = new WallTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WallTabFragment() {
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
        inflater.inflate(R.menu.menu_wall_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Hace click en perfil de usuario
        if (id == R.id.action_user_profile) {
            return openProfileActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wall_tab, container, false);
        return rootView;
    }

    /*
     * Callback Screen Methods
     */



    private boolean openProfileActivity() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        // Le pasamos al activity de profile el usuario que esta actualmente en el muro
        User currentWallUser = null;//this.wallTabScreen.getUserOwnerOfTheWall();
        if(currentWallUser != null) {
            intent.putExtra(ProfileActivity.USER_ID_PARAMETER, currentWallUser.getId());
        }
        else{
            intent.putExtra(ProfileActivity.USER_ID_PARAMETER, ContextManager.getInstance().getMyUser().getId());
        }
        this.startActivity(intent);
        return true;
    }

}
