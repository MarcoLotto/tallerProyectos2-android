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
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.ProfileEditHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;


public class ProfileEditActivity extends ActionBarActivity implements TabScreen {

    private static final String EDIT_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 1;
    private ListView profileEditListView;
    private List<ProfileField> fields = new ArrayList<ProfileField>();
    private ProfileField lastFieldClicked;
    private User user;

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
        this.user = new User(userOwnerId, "");
        ProfileInfoHttpAsyncTask profileInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, this.user);
        profileInfoService.execute(ProfileActivity.SHOW_PROFILE_ENDPOINT_URL);

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
        profileEditService.execute(this.EDIT_PROFILE_ENDPOINT_URL);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            this.fields.clear();

            // TODO: Dependiendo de que se este editando, completar fields con diferente info
            this.fillFieldsListWithPersonalProfileData();

            this.addProfileFieldsToUIList();
        }
        else if(serviceId == this.EDIT_PROFILE_INFO_SERVICE_ID){
            // Pudimos editar correctamente, volvemos a la pantalla de vista de perfil
            Toast toast = Toast.makeText(this.getApplicationContext(), "Edición exitosa", Toast.LENGTH_SHORT);
            toast.show();
            this.finish();
        }
    }

    private void fillFieldsListWithPersonalProfileData() {
        this.fields.add(new ProfileField("firstName", this.user.getName(), "Nombre"));
        this.fields.add(new ProfileField("lastName", this.user.getLastName(), "Apellido"));
        this.fields.add(new ProfileField("biography", this.user.getBiography(), "Biografia"));
        this.fields.add(new ProfileField("nationality", this.user.getNationality(), "Nacionalidad"));
        this.fields.add(new ProfileField("city", this.user.getCity(), "Ciudad"));
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
        this.profileEditListView.setAdapter(adapter);
    }

    public Dialog createDialog(final String fieldName, String fieldOriginalValue, final boolean numeric, final int minFieldSize) {
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
                        String paramValue = ((EditText) dialogView.findViewById(R.id.profileValueEditText)).getText().toString();
                        // Validamos el campo y mandamos a guardar el valor
                        if (numeric && FieldsValidator.isNumericFieldValid(paramValue) || !numeric) {
                            if (FieldsValidator.isTextFieldValid(paramValue, minFieldSize)) {
                                saveParameterValue(paramValue);
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "El campo " + fieldName + " debe tener un mínimo de " + minFieldSize + " caracteres", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "El campo " + fieldName + " debe ser numérico", Toast.LENGTH_LONG);
                            toast.show();
                        }
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

            // Indicamos que campos deben ser validados
            boolean numeric = false;
            int minFieldSize = 0;
            if(this.lastFieldClicked.getName().equals("padron")){
                numeric = true;
                minFieldSize = 5;
            }
            else if(this.lastFieldClicked.getName().equals("firstName")){
                minFieldSize = 1;
            }
            else if(this.lastFieldClicked.getName().equals("lastName")){
                minFieldSize = 1;
            }
            // Abrimos el popup de modificación del parámetro
            Dialog editDialog = this.createDialog(this.lastFieldClicked.getDisplayName(), this.lastFieldClicked.getValue(), numeric, minFieldSize);
            editDialog.show();
        }
    }
}
