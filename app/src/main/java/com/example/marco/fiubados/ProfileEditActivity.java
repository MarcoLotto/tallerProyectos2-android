package com.example.marco.fiubados;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.httpAsyncTasks.ProfileEditHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.ProfileField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;


public class ProfileEditActivity extends ActionBarActivity implements TabScreen {

    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 1;
    private ListView profileEditListView;
    private List<ProfileField> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Nos guardamos la list view para mostrar los campos del perfil para su edición
        this.profileEditListView = (ListView) findViewById(R.id.profileEditListView);

        // Primero conseguimos los datos del perfil
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(ProfileActivity.USER_ID_PARAMETER);
        ProfileInfoHttpAsyncTask profileInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, userOwnerId);
        profileInfoService.execute("http://www.mocky.io/v2/552966ac22258fdb02a3789a");

        // Manejamos el on click del boton de guardar perfil
        Button saveEditButton = (Button) findViewById(R.id.saveProfileButton);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        // Llamamos al servicio de edición de perfil
        ProfileEditHttpAsyncTask profileEditService = new ProfileEditHttpAsyncTask(this, this, EDIT_PROFILE_INFO_SERVICE_ID, this.fields);
        profileEditService.execute("http://www.mocky.io/v2/552a755422258feb02a378c4");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
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
        else if(serviceId == this.EDIT_PROFILE_INFO_SERVICE_ID){
            // Pudimos editar correctamente, volvemos a la pantalla de vista de perfil
            Toast toast = Toast.makeText(this.getApplicationContext(), "Edición exitosa", Toast.LENGTH_SHORT);
            toast.show();
            this.finish();
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
        this.profileEditListView.setAdapter(adapter);
    }
}
