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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.EducationsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileDeleteHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.Academic;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Education;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;


public class AcademicProfileEditActivity extends ActionBarActivity implements TabScreen {

    private static final String EDIT_EDUCATIONS_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/educations/";
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 1;
    private ListView profileEditListView;
    private Education lastEducationFieldClicked;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_profile_edit);

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
        // Por cada servicio que se edite tiramos un tiro al servicio
        for(Education education : this.user.getEducationInfo()) {
            if(education.isDirty()) {
                if(education.isDeleted()){
                    ProfileDeleteHttpAsyncTask service = new ProfileDeleteHttpAsyncTask(this, this, EDIT_PROFILE_INFO_SERVICE_ID, education);
                    service.execute(this.EDIT_EDUCATIONS_PROFILE_ENDPOINT_URL + education.getId() + "/destroy");
                }
                else {
                    EducationsEditAndCreateHttpAsyncTask service = new EducationsEditAndCreateHttpAsyncTask(this, this, EDIT_PROFILE_INFO_SERVICE_ID, education);
                    service.execute(this.EDIT_EDUCATIONS_PROFILE_ENDPOINT_URL + education.getId() + "/edit");
                }
            }
        }
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            this.addProfileFieldsToUIList();
        }
        else if(serviceId == this.EDIT_PROFILE_INFO_SERVICE_ID){
            // Revisamos que nos hayan devuelto todos los tiros que mandamos
            if(this.areAllAcademicsAndEducationsUpdated()) {
                // Pudimos editar correctamente, volvemos a la pantalla de vista de perfil
                Toast toast = Toast.makeText(this.getApplicationContext(), "Edición exitosa", Toast.LENGTH_SHORT);
                toast.show();
                this.finish();
            }
        }
    }

    private boolean areAllAcademicsAndEducationsUpdated() {
        for(Academic academic : this.user.getAcademicInfo()){
            if(academic.isDirty())
                return false;
        }
        for(Education education : this.user.getEducationInfo()){
            if(education.isDirty())
                return false;
        }
        return true;
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
        if(this.user.getEducationInfo().size() > position) {
            this.lastEducationFieldClicked = this.user.getEducationInfo().get(position);

            // Abrimos el popup de modificación del parámetro
            Dialog editDialog = this.createEducationDialog();
            editDialog.show();
        }
    }

    private void addProfileFieldsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<DualField>();
        for (Education education : this.user.getEducationInfo()) {
            // Agrego a la lista de de info de educación a todos los items
            if(!education.isDeleted()) {
                String line1 = education.getDiploma() + " - " + education.getInstitute();
                String line2 = education.getStartDate() + " - " + education.getEndDate();
                finalListViewLines.add(new DualField(new Field("", line1), new Field("", line2)));
            }
        }
        this.profileEditListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }

    public Dialog createEducationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_job_dialog, null);

        // Como estamos reutilziando un layout de jobs, le cambiamos los nombres a los labels desde acá
        ((TextView) dialogView.findViewById(R.id.fieldNameCompany)).setText("Diploma");
        ((TextView) dialogView.findViewById(R.id.fieldNamePosition)).setText("Instituto");
        ((TextView) dialogView.findViewById(R.id.fieldNameStartDate)).setText("Fecha inicio");
        ((TextView) dialogView.findViewById(R.id.fieldNameEndDate)).setText("Fecha fin");

        // Cargamos los valores originales en los campos
        if(this.lastEducationFieldClicked != null) {
            ((EditText) dialogView.findViewById(R.id.fieldValueCompany)).setText(this.lastEducationFieldClicked.getDiploma());
            ((EditText) dialogView.findViewById(R.id.fieldValuePosition)).setText(this.lastEducationFieldClicked.getInstitute());
            ((EditText) dialogView.findViewById(R.id.fieldValueStartDate)).setText(this.lastEducationFieldClicked.getStartDate());
            ((EditText) dialogView.findViewById(R.id.fieldValueEndDate)).setText(this.lastEducationFieldClicked.getEndDate());
        }

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos
                        String diploma = ((EditText) dialogView.findViewById(R.id.fieldValueCompany)).getText().toString();
                        String institute = ((EditText) dialogView.findViewById(R.id.fieldValuePosition)).getText().toString();
                        String startDate = ((EditText) dialogView.findViewById(R.id.fieldValueStartDate)).getText().toString();
                        String endDate = ((EditText) dialogView.findViewById(R.id.fieldValueEndDate)).getText().toString();

                        // Validamos los campos
                        if (FieldsValidator.isTextFieldValid(diploma, 1) && FieldsValidator.isTextFieldValid(diploma, 1)
                                && FieldsValidator.isTextFieldValid(startDate, 1) && FieldsValidator.isTextFieldValid(endDate, 1)) {
                            Education education = new Education("", diploma, institute, startDate, endDate);
                            save(education);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, no puede haber campos vacíos", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Llamamos al servicio de eliminación
                        // TODO
                    }
                });
        return builder.create();
    }

    private void deleteEducation() {
        if(this.lastEducationFieldClicked != null){
            this.lastEducationFieldClicked.setDeleted(true);
            this.lastEducationFieldClicked.setDirty(true);
            this.addProfileFieldsToUIList();
        }
    }

    private void save(Education education) {
        if(this.lastEducationFieldClicked != null){
            // Guardamos cada uno de los campos del educations
            this.lastEducationFieldClicked.setDiploma(education.getDiploma());
            this.lastEducationFieldClicked.setInstitute(education.getInstitute());
            this.lastEducationFieldClicked.setStartDate(education.getStartDate());
            this.lastEducationFieldClicked.setEndDate(education.getEndDate());
            this.lastEducationFieldClicked.setDirty(true);
            this.addProfileFieldsToUIList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_academic_profile_edit, menu);
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
}
