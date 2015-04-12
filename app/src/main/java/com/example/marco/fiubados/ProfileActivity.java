package com.example.marco.fiubados;

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
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;

    private List<ProfileField> fields = new ArrayList<ProfileField>();
    private ListView profileFieldsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Nos guardamos la list view para mostrar los campos del perfil
        this.profileFieldsListView = (ListView) findViewById(R.id.profileFieldsListView);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(this.USER_ID_PARAMETER);
        ProfileInfoHttpAsyncTask profileInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, userOwnerId);
        profileInfoService.execute("http://www.mocky.io/v2/552966ac22258fdb02a3789a");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            finalListViewLines.add(field.getName() + ": " + field.getValue());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        this.profileFieldsListView.setAdapter(adapter);
    }
}