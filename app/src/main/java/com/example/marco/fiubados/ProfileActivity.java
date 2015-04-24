package com.example.marco.fiubados;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends ActionBarActivity implements TabScreen {
/*
    private final int PERSONAL_TAB_INDEX = 0;
    private final int JOBS_TAB_INDEX = 1;
    private final int ACADEMIC_TAB_INDEX = 2;

    // Ejemplo
    boolean isPersonalInfoTagActive = this.tabHost.getCurrentTab() == PERSONAL_TAB_INDEX;

*/
    // Parametros que recibe este activity via extra info
    public static final String USER_ID_PARAMETER = "userIdParameter";
    //public static final String SHOW_PROFILE_ENDPOINT_URL = "http://www.mocky.io/v2/552afe974787d0c5012fa58e";
    public static final String SHOW_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int SEARCH_JOB_INFO_SERVICE_ID = 1;
    private final int SEARCH_ACADEMIC_INFO_SERVICE_ID = 2;

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

        ProfileInfoHttpAsyncTask personalInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, userOwnerId);
        personalInfoService.execute(SHOW_PROFILE_ENDPOINT_URL);

        ProfileInfoHttpAsyncTask jobsInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_JOB_INFO_SERVICE_ID, userOwnerId);
        jobsInfoService.execute(SHOW_PROFILE_ENDPOINT_URL);

        ProfileInfoHttpAsyncTask academicInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_ACADEMIC_INFO_SERVICE_ID, userOwnerId);
        academicInfoService.execute(SHOW_PROFILE_ENDPOINT_URL);

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
            case 0:
                // Tab de informacion personal
                break;
            case 1:
                // Tab de empleos
                break;
            case 2:
                // Tab de informacion academica
                break;
        }
        this.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        // Si estoy viendo el perfil de mi usuario, permito editarlo
        menu.findItem(R.id.profileEditAction).setVisible(this.user.equals(ContextManager.getInstance().getMyUser()));

        // TODO Agregar el icono de +

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
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

    private boolean openJobEditActivity() {
        // TODO
        Intent intent = new Intent(this, ProfileEditActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_PARAMETER, this.user.getId());
        this.startActivity(intent);
        this.finish();
        return true;
    }

    private boolean openAcademicEditActivity() {
        // TODO
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
        List<String> finalListViewLines = new ArrayList<>();

        for (ProfileField field : this.fields) {
            // Agregamos a la lista de campos todos los fields encontrados
            finalListViewLines.add(field.getDisplayName() + ": " + field.getValue());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        this.personalFieldsListView.setAdapter(adapter);
    }
}
