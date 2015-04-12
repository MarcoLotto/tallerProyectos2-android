package com.example.marco.fiubados;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.httpAsyncTasks.ProfileEditHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;


public class ProfileEditActivity extends ActionBarActivity implements TabScreen {

    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 1;
    private ListView profileEditListView;
    private List<ProfileField> fields;
    private ProfileField lastFieldClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Nos guardamos la list view para mostrar los campos del perfil para su edición
        this.profileEditListView = (ListView) findViewById(R.id.profileEditListView);
        this.configureComponents();

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

    public Dialog createDialog(String fieldName, String fieldOriginalValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_edit_parameter, null);

        // Asignamos los valores iniciales
        TextView fieldNameTextView = (TextView) dialogView.findViewById(R.id.profileFieldNameTextView);
        fieldNameTextView.setText(fieldName);
        EditText fieldValueEditText = (EditText) dialogView.findViewById(R.id.profileValueEditText);
        fieldValueEditText.setText(fieldOriginalValue);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText paramValue = (EditText) dialogView.findViewById(R.id.profileValueEditText);
                        saveParameterValue(paramValue.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hace falta hacer ninguna acción
                    }
                });
        return builder.create();
    }

    private void saveParameterValue(String value) {
        if(this.lastFieldClicked != null){
            this.lastFieldClicked.setValue(value);
            this.addProfileFieldsToUIList();
        }
    }

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        this.profileEditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onParameterClickedOnList(position);
            }
        });
    }

    private void onParameterClickedOnList(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if(this.fields.size() > position) {
            this.lastFieldClicked = this.fields.get(position);
            // Abrimos el popup de modificación del parámetro
            Dialog editDialog = this.createDialog(this.lastFieldClicked.getName(), this.lastFieldClicked.getValue());
            editDialog.show();
        }
    }
}
