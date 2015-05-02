package com.example.marco.fiubados;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.EducationsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.JobsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.Academic;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Education;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Job;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends AppCompatActivity implements TabScreen {

    // La posición en la lista de academico del item de materias aprobadas
    private static final int APPROVED_SUBJECTS_POSITION_IN_ACADEMICS_LIST = 2;

    private final int PERSONAL_TAB_INDEX = 0;
    private final int JOBS_TAB_INDEX = 1;
    private final int ACADEMIC_TAB_INDEX = 2;

    // Parametros que recibe este activity via extra info
    public static final String USER_ID_PARAMETER = "userIdParameter";
    private static final String CREATE_JOB_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/jobs/create_job";
    public static final String SHOW_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";
    private static final String CREATE_EDUCATION_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/educations/create_education";

    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private static final int CREATE_JOB_SERVICE_ID = 1;
    private static final int CREATE_EDUCATION_SERVICE_ID = 2;
    private static final int SUBJECTS_INFO_SERVICE_ID = 3;

    private List<ProfileField> fields = new ArrayList<>();
    private ListView personalFieldsListView;
    private ListView jobsFieldsListView;
    private ListView educationsFieldsListView;
    private User user;
    private User temporalUser;
    private TabHost tabHost;
    private ListView academicsFieldsListView;
    private List<String> fiubaAcademicsViewLines = new ArrayList<>();
    private List<String> approvedSubjects = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Nos guardamos la list view para mostrar los campos personales
        this.personalFieldsListView = (ListView) findViewById(R.id.profileFieldsListView);

        // Nos guardamos la list view para mostrar los campos del empleo
        this.jobsFieldsListView = (ListView) findViewById(R.id.jobsFieldsListView);

        // Nos guardamos la list view para mostrar los campos academicos y de educations
        this.educationsFieldsListView = (ListView) findViewById(R.id.educationsFieldsListView);
        this.academicsFieldsListView = (ListView) findViewById(R.id.academicFieldsListView);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(USER_ID_PARAMETER);
        this.user = new User(userOwnerId, "");

        // La magia
        this.onFocus();

        // Configuramos los tabs
        this.configureTabHost();

        // Configuramos el comportamiento de los componentes
        this.configureComponents();
    }

    private void configureComponents() {
        this.academicsFieldsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onAcademicParameterClickedOnList(position);
            }
        });
    }

    private void onAcademicParameterClickedOnList(int position) {
        // Abrimos el popup de modificación del parámetro
        Academic academic = this.user.getAcademicInfo();
        if (position == this.APPROVED_SUBJECTS_POSITION_IN_ACADEMICS_LIST){
            Dialog subjectsDialog = SubjectsFinder.createApprovedSubjectsDialog(this, this.approvedSubjects);
            subjectsDialog.show();
        }
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
            this.createAddEducationDialog(this, this);
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
            intent = new Intent(this, AcademicProfileEditActivity.class);
        }
        intent.putExtra(ProfileActivity.USER_ID_PARAMETER, this.user.getId());
        this.startActivity(intent);
        this.finish();
        return true;
    }

    @Override
    public void onFocus() {
        // Creamos un usuario temporal en donde se guardará la data
        this.temporalUser = new User(this.user.getId(), "");

        // Vamos a buscar la informacion del perfil
        ProfileInfoHttpAsyncTask personalInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, this.temporalUser);
        personalInfoService.execute(SHOW_PROFILE_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            this.user = temporalUser;
            this.addPersonalProfileFieldsToUIList();
            this.addJobsProfileFieldsToUIList();
            this.addEducationsProfileFieldsToUIList();
            this.prepareAcademicsProfileFieldsToShowUIList();
        }
        else if(serviceId == this.CREATE_JOB_SERVICE_ID || serviceId == this.CREATE_EDUCATION_SERVICE_ID){
            this.onFocus();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Creación exitosa", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(serviceId == this.SUBJECTS_INFO_SERVICE_ID){
            this.addAcademicsProfileFieldsToUIList();

            // Me guardo la info de materias aprobadas por si la quieren ver
            this.approvedSubjects.clear();
            this.approvedSubjects.addAll(responseElements);
        }
    }

    private void addEducationsProfileFieldsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        for (Education education : this.user.getEducationInfo()) {
            // Agrego a la lista de educación, todas las educaciones
            String line1 = education.getDiploma() + " - " + education.getInstitute();
            String line2 = education.getStartDate() + " - " + education.getEndDate();
            finalListViewLines.add(new DualField(new Field("", line1), new Field("", line2)));
        }
        this.educationsFieldsListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }


    private void prepareAcademicsProfileFieldsToShowUIList() {
        this.fiubaAcademicsViewLines.clear();
        this.fiubaAcademicsViewLines.add("Carrera" + ": " + this.user.getAcademicInfo().getCareer());
        this.fiubaAcademicsViewLines.add("Padrón" + ": " + this.user.getPadron());

        // Llamamos al helper para conseguir todos los datos de las materias
        SubjectsFinder subjectsFinder = new SubjectsFinder(this, this, this.SUBJECTS_INFO_SERVICE_ID, this.fiubaAcademicsViewLines);
    }

    private void addAcademicsProfileFieldsToUIList() {
        // Llenamos la inforamción de FIUBA
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.fiubaAcademicsViewLines);
        this.academicsFieldsListView.setAdapter(adapter);
    }

    private void addJobsProfileFieldsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
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
                                && FieldsValidator.isDateValid(startDate) && (endDate.isEmpty() || FieldsValidator.isDateValid(endDate))) {
                            Job job = new Job("", company, position, startDate, endDate);
                            JobsEditAndCreateHttpAsyncTask service = new JobsEditAndCreateHttpAsyncTask(ownerActivity, ownerTabScreen, CREATE_JOB_SERVICE_ID, job);
                            service.execute(CREATE_JOB_SERVICE_ENDPOINT_URL);
                        } else {
                            if (!FieldsValidator.isDateValid(startDate) || (endDate.isEmpty() || FieldsValidator.isDateValid(endDate))) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, recuerde que el formato de fecha es 'dd/mm/aaaa'", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la fecha de fin (o bien tener formato fecha válido)", Toast.LENGTH_LONG);
                                toast.show();
                            }
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

    public void createAddEducationDialog(final Activity ownerActivity, final TabScreen ownerTabScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_job_dialog, null);

        // Como estamos reutilizando el layout de jobs, le cambiamos los nombres a los campos
        ((TextView) dialogView.findViewById(R.id.fieldNameCompany)).setText("Diploma");
        ((TextView) dialogView.findViewById(R.id.fieldNamePosition)).setText("Instituto");
        ((TextView) dialogView.findViewById(R.id.fieldNameStartDate)).setText("Fecha inicio");
        ((TextView) dialogView.findViewById(R.id.fieldNameEndDate)).setText("Fecha fin");

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos
                        String diploma = ((EditText) dialogView.findViewById(R.id.fieldValueCompany)).getText().toString();
                        String institute = ((EditText) dialogView.findViewById(R.id.fieldValuePosition)).getText().toString();
                        String startDate = ((EditText) dialogView.findViewById(R.id.fieldValueStartDate)).getText().toString();
                        String endDate = ((EditText) dialogView.findViewById(R.id.fieldValueEndDate)).getText().toString();

                        // Validamos los campos
                        if (FieldsValidator.isTextFieldValid(diploma, 1) && FieldsValidator.isTextFieldValid(institute, 1)
                                && FieldsValidator.isDateValid(startDate) && FieldsValidator.isDateValid(endDate)) {
                            Education education = new Education("", diploma, institute, startDate, endDate);
                            EducationsEditAndCreateHttpAsyncTask service = new EducationsEditAndCreateHttpAsyncTask(ownerActivity, ownerTabScreen, CREATE_EDUCATION_SERVICE_ID, education);
                            service.execute(CREATE_EDUCATION_SERVICE_ENDPOINT_URL);
                        } else {
                            if (!FieldsValidator.isDateValid(startDate) || (endDate.isEmpty() || FieldsValidator.isDateValid(endDate))) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, recuerde que el formato de fecha es 'dd/mm/aaaa'", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, el único campo que puede estar vacío es la fecha de fin (o bien tener formato fecha válido)", Toast.LENGTH_LONG);
                                toast.show();
                            }
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
