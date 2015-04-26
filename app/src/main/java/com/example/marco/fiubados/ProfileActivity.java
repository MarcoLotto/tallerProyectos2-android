package com.example.marco.fiubados;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.JobsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.Academic;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Job;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;


public class ProfileActivity extends ActionBarActivity implements TabScreen {

    private final int PERSONAL_TAB_INDEX = 0;
    private final int JOBS_TAB_INDEX = 1;
    private final int ACADEMIC_TAB_INDEX = 2;

    // Parametros que recibe este activity via extra info
    public static final String USER_ID_PARAMETER = "userIdParameter";
    private static final String CREATE_JOB_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/jobs/create_job";
    public static final String SHOW_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private static final int CREATE_JOB_SERVICE_ID = 1;
    
    private List<ProfileField> fields = new ArrayList<>();
    private ListView personalFieldsListView;
    private ListView jobsFieldsListView;
    private ListView academicFieldsListView;
    private User user;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Nos guardamos la list view para mostrar los campos personales
        this.personalFieldsListView = (ListView) findViewById(R.id.profileFieldsListView);

        // Nos guardamos la list view para mostrar los campos del empleo
        this.jobsFieldsListView = (ListView) findViewById(R.id.jobsFieldsListView);

        // Nos guardamos la list view para mostrar los campos academicos
        this.academicFieldsListView = (ListView) findViewById(R.id.academicFieldsListView);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(USER_ID_PARAMETER);
        this.user = new User(userOwnerId, "");

        // La magia
        this.onFocus();

        // Configuramos los tabs
        this.configureTabHost();
    }

    private void configureTabHost() {
        this.tabHost = (TabHost) findViewById(R.id.profileTabHost);
        this.tabHost.setup();
        this.addTabSpectToTabHost(this.tabHost, "Personal", R.id.ProfileTabPersonal);
        this.addTabSpectToTabHost(this.tabHost, "Empleo", R.id.ProfileTabJobs);
        this.addTabSpectToTabHost(this.tabHost, "Académico", R.id.ProfileTabAcademic);

        this.tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                handleTabChange();
            }
        });
    }

    private void addTabSpectToTabHost(TabHost tabHost, String tabLabel, int tabId) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabLabel);
        tabSpec.setContent(tabId);
        tabSpec.setIndicator(tabLabel);
        tabHost.addTab(tabSpec);
    }

    /**
     * Maneja el cambio de pestañas
     */
    private void handleTabChange() {
        int currentTabIndex = this.tabHost.getCurrentTab();
        switch(currentTabIndex){
            case PERSONAL_TAB_INDEX:
                // Tab de informacion personal
                break;
            case JOBS_TAB_INDEX:
                // Tab de empleos
                break;
            case ACADEMIC_TAB_INDEX:
                // Tab de informacion academica
                break;
        }
        this.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Si estoy viendo el perfil de mi usuario, permito editarlo
        menu.findItem(R.id.profileEditAction).setVisible(this.user.equals(ContextManager.getInstance().getMyUser()));

        // Si estoy viendo el perfil de mi usuario y en los tabs de academico o jobs, permito editarlo
        menu.findItem(R.id.profileAddAction).setVisible(this.user.equals(ContextManager.getInstance().getMyUser()) &&
            (this.tabHost.getCurrentTab() != PERSONAL_TAB_INDEX));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profileEditAction:
                return this.openProfileEditActivity();
            case R.id.profileAddAction:
                return this.openProfileAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean openProfileAddDialog() {
        if(this.tabHost.getCurrentTab() == JOBS_TAB_INDEX){
            this.createAddJobDialog(this, this);
        }
        else if(this.tabHost.getCurrentTab() == ACADEMIC_TAB_INDEX){
            // TODO
        }
        return true;
    }

    private boolean openProfileEditActivity() {
        // Segun en que tab esté, abro un activity de edición diferente
        Intent intent;
        if(this.tabHost.getCurrentTab() == PERSONAL_TAB_INDEX) {
            intent = new Intent(this, ProfileEditActivity.class);
        }
        else if(this.tabHost.getCurrentTab() == JOBS_TAB_INDEX) {
            intent = new Intent(this, JobsProfileEditActivity.class);
        }
        else{
            // TODO
            intent = new Intent(this, ProfileEditActivity.class);
        }
        intent.putExtra(ProfileActivity.USER_ID_PARAMETER, this.user.getId());
        this.startActivity(intent);
        this.finish();
        return true;
    }

    @Override
    public void onFocus() {
        // Vamos a buscar la informacion del perfil
        ProfileInfoHttpAsyncTask personalInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, this.user);
        personalInfoService.execute(SHOW_PROFILE_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            this.addPersonalProfileFieldsToUIList();
            this.addJobsProfileFieldsToUIList();
            this.addAcademicProfileFieldsToUIList();
        }
        else if(serviceId == this.CREATE_JOB_SERVICE_ID){
            this.onFocus();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Creación exitosa", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void addAcademicProfileFieldsToUIList() {
        List<String> finalListViewLines = new ArrayList<>();
        for (Academic academic : this.user.getAcademicInfo()) {
            // TODO
            // Agregamos a la lista de campos todos los fields encontrados
            // finalListViewLines.add(academic.getDisplayName() + ": " + field.getValue());
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        this.academicFieldsListView.setAdapter(adapter);
    }

    private void addJobsProfileFieldsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<DualField>();
        for (Job job : this.user.getJobs()) {
            // Agrego a la lista de trabajos todos los trabajos
            String line1 = job.getCompany() + " - " + job.getPosition();
            String line2 = job.getStartDate() + " - ";
            if(job.getEndDate().isEmpty()){
                line2 += "Actualidad";
            }
            else{
                line2 += job.getEndDate();
            }
            finalListViewLines.add(new DualField(new Field("", line1), new Field("", line2)));
        }
        this.jobsFieldsListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }

    private void addPersonalProfileFieldsToUIList() {
        List<String> finalListViewLines = new ArrayList<>();
        // Agregamos a la lista de campos de personal de usuario todos los campos
        finalListViewLines.add("Nombre" + ": " + this.user.getName());
        finalListViewLines.add("Apellido" + ": " + this.user.getLastName());
        finalListViewLines.add("Padrón" + ": " + this.user.getPadron());
        finalListViewLines.add("Biografía" + ": " + this.user.getBiography());
        finalListViewLines.add("Nacionalidad" + ": " + this.user.getNationality());
        finalListViewLines.add("Ciudad" + ": " + this.user.getCity());

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        this.personalFieldsListView.setAdapter(adapter);
    }


    public void createAddJobDialog(final Activity ownerActivity, final TabScreen ownerTabScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_job_dialog, null);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos       
                        String company = ((EditText) dialogView.findViewById(R.id.fieldValueCompany)).getText().toString();
                        String position = ((EditText) dialogView.findViewById(R.id.fieldValuePosition)).getText().toString();
                        String startDate = ((EditText) dialogView.findViewById(R.id.fieldValueStartDate)).getText().toString();
                        String endDate = ((EditText) dialogView.findViewById(R.id.fieldValueEndDate)).getText().toString();

                        // Validamos los campos
                        if (FieldsValidator.isTextFieldValid(company, 1) && FieldsValidator.isTextFieldValid(position, 1)
                                && FieldsValidator.isTextFieldValid(startDate, 1)) {
                            Job job = new Job("", company, position, startDate, endDate);
                            JobsEditAndCreateHttpAsyncTask service = new JobsEditAndCreateHttpAsyncTask(ownerActivity, ownerTabScreen, CREATE_JOB_SERVICE_ID, job);
                            service.execute(CREATE_JOB_SERVICE_ENDPOINT_URL);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la fecha de fin", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hace falta hacer ninguna acción
                    }
                });
        builder.create().show();
    }
}
