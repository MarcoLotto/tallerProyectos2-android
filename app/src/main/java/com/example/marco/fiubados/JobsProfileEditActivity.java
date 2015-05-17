package com.example.marco.fiubados;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.commons.DialogCallback;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.commons.FormDialogBuilder;
import com.example.marco.fiubados.httpAsyncTasks.JobsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileDeleteHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Job;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;

public class JobsProfileEditActivity extends AppCompatActivity implements CallbackScreen, DialogCallback {

    private static final String EDIT_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/jobs/";
    private static final String DELETE_JOB_SERVICE_ENDPOINT_URL = EDIT_PROFILE_ENDPOINT_URL;

    private static final int EDIT_JOB_DIALOG_ID = 0;

    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 1;
    private static final int DELETE_JOB_SERVICE_ID = 2;

    private ListView profileEditListView;
    private Job lastFieldClicked;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_profile_edit);

        // Nos guardamos la list view para mostrar los campos del perfil para su edición
        this.profileEditListView = (ListView) findViewById(R.id.profileEditListView);
        this.configureComponents();

        // Primero conseguimos los datos del perfil
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(ProfileActivity.USER_ID_PARAMETER);
        this.user = new User(userOwnerId);
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
        for(Job job : this.user.getJobs()) {
            if(job.isDirty()) {
                if(job.isDeleted()){
                    // Borramos el job
                    ProfileDeleteHttpAsyncTask service = new ProfileDeleteHttpAsyncTask(this, this, DELETE_JOB_SERVICE_ID, job);
                    service.execute(DELETE_JOB_SERVICE_ENDPOINT_URL + job.getId() + "/destroy");
                }
                else {
                    // Actualizamos el job
                    JobsEditAndCreateHttpAsyncTask service = new JobsEditAndCreateHttpAsyncTask(this, this, EDIT_PROFILE_INFO_SERVICE_ID, job);
                    service.execute(this.EDIT_PROFILE_ENDPOINT_URL + job.getId() + "/edit");
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
        else if(serviceId == this.EDIT_PROFILE_INFO_SERVICE_ID || serviceId == this.DELETE_JOB_SERVICE_ID){
            // Revisamos que nos hayan devuelto todos los tiros que mandamos
            if(this.areAllJobsUpdated()) {
                // Pudimos editar correctamente, volvemos a la pantalla de vista de perfil
                Toast toast = Toast.makeText(this.getApplicationContext(), "Edición exitosa", Toast.LENGTH_SHORT);
                toast.show();
                this.finish();
            }
        }
    }

    private boolean areAllJobsUpdated() {
        for(Job job : this.user.getJobs()){
            if(job.isDirty())
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
        if(this.user.getJobs().size() > position) {
            this.lastFieldClicked = this.user.getJobs().get(position);

           // Abrimos el popup de modificación del parámetro
            List<String> inputs = new ArrayList<>();
            inputs.add(this.lastFieldClicked.getCompany());
            inputs.add(this.lastFieldClicked.getPosition());
            inputs.add(this.lastFieldClicked.getStartDate());
            inputs.add(this.lastFieldClicked.getEndDate());
            FormDialogBuilder.showProfileInstitutionDialog(this, this, this.EDIT_JOB_DIALOG_ID, inputs, R.layout.layout_add_job_dialog, R.string.modify, R.string.delete);
        }
    }

    private void addProfileFieldsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        for (Job job : this.user.getJobs()) {
            if (!job.isDeleted()) {
                // Agrego a la lista de trabajos todos los trabajos
                String line1 = job.getCompany() + " - " + job.getPosition();
                String line2 = job.getStartDate() + " - ";
                if (job.getEndDate().isEmpty()) {
                    line2 += "Actualidad";
                } else {
                    line2 += job.getEndDate();
                }
                finalListViewLines.add(new DualField(new Field("", line1), new Field("", line2)));
            }
        }
        this.profileEditListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }

    private void onJobModify(List<String> outputs){
        if(outputs.size() < 4){
            return;
        }
        // Conseguimos todos los valores de los campos
        String company = outputs.get(0);
        String position = outputs.get(1);
        String startDate = outputs.get(2);
        String endDate = outputs.get(3);

        // Validamos los campos
        if (FieldsValidator.isTextFieldValid(company, 1) && FieldsValidator.isTextFieldValid(position, 1)
                && FieldsValidator.isDateValid(startDate) && (endDate.isEmpty() || FieldsValidator.isDateValid(endDate))) {
            Job job = new Job("", company, position, startDate, endDate);
            saveJob(job);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la fecha de fin", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void deleteJob() {
        if(this.lastFieldClicked != null){
            this.lastFieldClicked.setDeleted(true);
            this.lastFieldClicked.setDirty(true);
            this.addProfileFieldsToUIList();
        }
    }

    private void saveJob(Job job) {
        if(this.lastFieldClicked != null){
            // Guardamos cada uno de los campos del job
            this.lastFieldClicked.setCompany(job.getCompany());
            this.lastFieldClicked.setPosition(job.getPosition());
            this.lastFieldClicked.setStartDate(job.getStartDate());
            this.lastFieldClicked.setEndDate(job.getEndDate());
            this.lastFieldClicked.setDirty(true);
            this.addProfileFieldsToUIList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobs_profile_edit, menu);
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
    public void onDialogClose(int dialogId, List<String> outputs, boolean userAccepts) {
        if(dialogId == this.EDIT_JOB_DIALOG_ID){
            if(userAccepts){
                this.onJobModify(outputs);
            }
            else{
                this.deleteJob();
            }
        }
    }
}
