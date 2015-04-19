package com.example.marco.fiubados;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProfileActivity extends ActionBarActivity implements TabScreen {

    // Parametros que recibe este activity via extra info
    public static final String USER_ID_PARAMETER = "userIdParameter";
    //public static final String SHOW_PROFILE_ENDPOINT_URL = "http://www.mocky.io/v2/552afe974787d0c5012fa58e";
    public static final String SHOW_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;

    private List<ProfileField> fields = new ArrayList<ProfileField>();
    private ListView profileFieldsListView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Nos guardamos la list view para mostrar los campos del perfil
        this.profileFieldsListView = (ListView) findViewById(R.id.profileFieldsListView);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(this.USER_ID_PARAMETER);
        this.user = new User(userOwnerId, "");
        ProfileInfoHttpAsyncTask profileInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, userOwnerId);
        profileInfoService.execute(this.SHOW_PROFILE_ENDPOINT_URL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        // Si estoy viendo el perfil de mi usuario, permito editarlo
        if(this.user.equals(ContextManager.getInstance().getMyUser())) {
            menu.findItem(R.id.profileEditAction).setVisible(true);
        }
        else{
            menu.findItem(R.id.profileEditAction).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.notificationAction:
                // TODO: this.notificationScreen.onFocus();
                return true;
            case R.id.profileEditAction:
                return this.openProfileEditActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean openProfileEditActivity() {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_PARAMETER, this.user.getId());
        this.startActivity(intent);
        this.finish();
        return true;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            this.fields = responseElements;
            this.addProfileFieldsToUIList();
        }
    }

    private void addProfileFieldsToUIList() {
        List<String> finalListViewLines = new ArrayList<String>();
        Iterator<ProfileField> it = this.fields.iterator();
        while(it.hasNext()){
            // Agregamos a la lista de amigos a todos los usuarios
            ProfileField field = it.next();
            finalListViewLines.add(field.getDisplayName() + ": " + field.getValue());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        this.profileFieldsListView.setAdapter(adapter);
    }
}
