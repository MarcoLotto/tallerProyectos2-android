package com.example.marco.fiubados;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.AcademicEditHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.EducationsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.GetCareersHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileDeleteHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.Academic;
import com.example.marco.fiubados.model.Career;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Education;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;

public class AcademicProfileEditActivity extends AppCompatActivity implements TabScreen {

    private static final String EDIT_EDUCATIONS_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/educations/";
    private static final int CAREER_POSITION_IN_ACADEMICS_LIST = 0;
    private static final String EDIT_ACADEMICS_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/academic_info/edit_career";
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 0;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 1;
    private static final int GET_CAREERS_SERVICE_ID = 2;
    private ListView educationsEditListView;
    private ListView academicEditListView;
    private Education lastEducationFieldClicked;
    private User user;
    private List<Career> availableCareers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_profile_edit);

        // Nos guardamos la list view para mostrar los campos del perfil para su edición
        this.educationsEditListView = (ListView) findViewById(R.id.educationsFieldsListView);
        this.academicEditListView = (ListView) findViewById(R.id.academicFieldsListView);
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
        if(this.user.getAcademicInfo().isDirty()){
            AcademicEditHttpAsyncTask service = new AcademicEditHttpAsyncTask(this, this, EDIT_PROFILE_INFO_SERVICE_ID, this.user.getAcademicInfo());
            service.execute(this.EDIT_ACADEMICS_PROFILE_ENDPOINT_URL);
        }
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            this.addEducationProfileFieldsToUIList();
            this.addAcademicsProfileFieldsToUIList();
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
        else if(serviceId == this.GET_CAREERS_SERVICE_ID){
            // Ya tenemos los nombres de carreras, abrimos el popup de edición
            this.onAcademicGetCareersResponse();
        }
    }

    private boolean areAllAcademicsAndEducationsUpdated() {
        if( this.user.getAcademicInfo().isDirty()){
            return false;
        }
        for(Education education : this.user.getEducationInfo()){
            if(education.isDirty())
                return false;
        }
        return true;
    }

    private void configureComponents() {
        this.educationsEditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onEducationParameterClickedOnList(position);
            }
        });
        this.academicEditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onAcademicParameterClickedOnList(position);
            }
        });
    }

    private void onEducationParameterClickedOnList(int position) {
        // Se hizo click en un usuario, preparo al muro y lo invoco
        if(this.user.getEducationInfo().size() > position) {
            this.lastEducationFieldClicked = this.user.getEducationInfo().get(position);

            // Abrimos el popup de modificación del parámetro
            Dialog editDialog = this.createEducationDialog();
            editDialog.show();
        }
    }

    private void onAcademicParameterClickedOnList(int position) {
        // Abrimos el popup de modificación del parámetro
        Academic academic = this.user.getAcademicInfo();
        if (position == this.CAREER_POSITION_IN_ACADEMICS_LIST){
            // Llamamos al servicio de obtención de carreras
            GetCareersHttpAsyncTask service = new GetCareersHttpAsyncTask(this, this, this.GET_CAREERS_SERVICE_ID, this.availableCareers);
            service.execute("http://www.mocky.io/v2/5546ac29f1598ae801776374"); // TODO: Cambiar por el servicio real cuando lo deploye en heroku
        }
    }

    private void onAcademicGetCareersResponse() {
        List<String> careersName = new ArrayList<>();
        for(Career career : this.availableCareers){
            careersName.add(career.getName());
        }
        Dialog editDialog = this.createAcademicComboParameterDialog(this.CAREER_POSITION_IN_ACADEMICS_LIST, "Carrera", careersName);
        editDialog.show();
    }

    public Dialog createAcademicTextParameterDialog(final int positionInList, final String fieldName, String fieldOriginalValue, final boolean numeric, final int minFieldSize) {
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
                                saveAcademicParameterValue(positionInList, paramValue);
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

    public Dialog createAcademicComboParameterDialog(final int positionInList, final String fieldName, List<String> possibleValues) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_combo_edit_parameter, null);

        // Asignamos el titulo del campo
        TextView fieldNameTextView = (TextView) dialogView.findViewById(R.id.profileFieldNameTextView);
        fieldNameTextView.setText(fieldName);

        // Asignamos las opciones posibles
        Spinner fieldValueSpinner = (Spinner) dialogView.findViewById(R.id.profileValueSpinner);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, possibleValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldValueSpinner.setAdapter(adapter);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos el valor de spinner y lo actualizamos
                        String paramValue = ((Spinner) dialogView.findViewById(R.id.profileValueSpinner)).getSelectedItem().toString();
                        saveAcademicParameterValue(positionInList, paramValue);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hace falta hacer ninguna acción
                    }
                });
        return builder.create();
    }

    private void saveAcademicParameterValue(int positionInList, String paramValue) {
        if(positionInList == this.CAREER_POSITION_IN_ACADEMICS_LIST){
            this.user.getAcademicInfo().setCareer(paramValue);
            this.user.getAcademicInfo().setDirty(true);
            this.addEducationProfileFieldsToUIList();
            this.addAcademicsProfileFieldsToUIList();
        }
    }

    private void addEducationProfileFieldsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        for (Education education : this.user.getEducationInfo()) {
            // Agrego a la lista de de info de educación a todos los items
            if(!education.isDeleted()) {
                String line1 = education.getDiploma() + " - " + education.getInstitute();
                String line2 = education.getStartDate() + " - " + education.getEndDate();
                finalListViewLines.add(new DualField(new Field("", line1), new Field("", line2)));
            }
        }
        this.educationsEditListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }

    private void addAcademicsProfileFieldsToUIList() {
        List<String> finalListViewLines = new ArrayList<>();
        finalListViewLines.add("Carrera" + ": " + this.user.getAcademicInfo().getCareer());
        finalListViewLines.add("Padrón" + ": " + this.user.getPadron());

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalListViewLines);
        this.academicEditListView.setAdapter(adapter);
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
                                && FieldsValidator.isDateValid(startDate) && FieldsValidator.isDateValid(endDate)) {
                            Education education = new Education("", diploma, institute, startDate, endDate);
                            save(education);
                        } else {
                            if (!FieldsValidator.isDateValid(startDate) || FieldsValidator.isDateValid(endDate)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, recuerde que el formato de fecha es 'dd/mm/aaaa'", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error en los campos ingresados, recuerde que no puede haber campos vacíos y el formato de fecha es 'dd/mm/aaaa'", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEducation();
                    }
                });
        return builder.create();
    }

    private void deleteEducation() {
        if(this.lastEducationFieldClicked != null){
            this.lastEducationFieldClicked.setDeleted(true);
            this.lastEducationFieldClicked.setDirty(true);
            this.addEducationProfileFieldsToUIList();
            this.addAcademicsProfileFieldsToUIList();
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
            this.addEducationProfileFieldsToUIList();
            this.addAcademicsProfileFieldsToUIList();
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
