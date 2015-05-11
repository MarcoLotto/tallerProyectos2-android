package com.example.marco.fiubados;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.GetSubjectsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SubjectsInfoHttpAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 02/05/2015.
 * Consigue las materias que el alumno tiene aprobadas y el factor de aprobación total de la carrera
 */
public class SubjectsFinder implements CallbackScreen {
    private static final int CAREER_SUBJECTS_SERVICE_ID = 0;
    private static final int APPROVED_SUBJECTS_SERVICE_ID = 1;
    private static final int SUBJECTS_INFO_SERVICE_ID = 2;

    private Activity callingActivity;
    private CallbackScreen callingScreen;
    private Map<String, String> subjectsInfo;
    private List<String> careerSubjects, approvedSubjects, fiubaAcademicToShowLines;
    private boolean careerSubjectsRetrived, approvedSubjectsRetrived, subjectsInfoRetrived;
    private int serviceId;

    SubjectsFinder(Activity callingActivity, CallbackScreen callingScreen, int serviceId,  List<String> fiubaAcademicToShowLines){
        this.callingActivity = callingActivity;
        this.callingScreen = callingScreen;
        this.subjectsInfo = new HashMap<String, String>();
        this.fiubaAcademicToShowLines = fiubaAcademicToShowLines;
        this.serviceId = serviceId;
        this.onFocus();
    }

    @Override
    public void onFocus() {
        this.careerSubjectsRetrived= false;
        this.approvedSubjectsRetrived = false;
        this.subjectsInfoRetrived = false;
        GetSubjectsHttpAsyncTask careerSubjectsService = new GetSubjectsHttpAsyncTask(this.callingActivity, this, this.CAREER_SUBJECTS_SERVICE_ID);
        careerSubjectsService.execute("http://www.mocky.io/v2/554541ff5c5bf0aa00ef8808");  // REVIEW
        GetSubjectsHttpAsyncTask approvedSubjectsService = new GetSubjectsHttpAsyncTask(this.callingActivity, this, this.APPROVED_SUBJECTS_SERVICE_ID);
        approvedSubjectsService.execute("http://www.mocky.io/v2/554542a95c5bf0b800ef8809"); // REVIEW
        SubjectsInfoHttpAsyncTask subjectsInfoService = new SubjectsInfoHttpAsyncTask(this.callingActivity, this, this.SUBJECTS_INFO_SERVICE_ID, this.subjectsInfo);
        subjectsInfoService.execute("http://www.mocky.io/v2/554542c45c5bf0bc00ef880a");  // REVIEW
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.CAREER_SUBJECTS_SERVICE_ID){
            this.careerSubjectsRetrived = true;
            this.careerSubjects = responseElements;
        }
        else if(serviceId == this.APPROVED_SUBJECTS_SERVICE_ID){
            this.approvedSubjectsRetrived = true;
            this.approvedSubjects = responseElements;
        }
        else if(serviceId == this.SUBJECTS_INFO_SERVICE_ID){
            this.subjectsInfoRetrived = true;
        }
        if(this.isResponseComplete()){
            List<String> approvedSubjetsToShow = this.formatApprovedSubjects();
            this.fiubaAcademicToShowLines.add(this.getApprobedRatioString());

            // Le decimos al que nos llamó que terminamos y le devolvemos las materias aprobadas ya formateadas
            this.callingScreen.onServiceCallback(approvedSubjetsToShow, this.serviceId);
        }
    }

    private List<String> formatApprovedSubjects() {
        List<String> formattedSubjects = new ArrayList<String>();
        for(String approvedSubjectCode : this.approvedSubjects){
            String subjectName = "Materia no cargada";
            if(this.subjectsInfo.containsKey(approvedSubjectCode)){
                subjectName = this.subjectsInfo.get(approvedSubjectCode);
            }
            formattedSubjects.add(approvedSubjectCode + " - " + subjectName);
        }
        return formattedSubjects;
    }

    public boolean isResponseComplete() {
        return (this.approvedSubjectsRetrived && this.careerSubjectsRetrived && this.subjectsInfoRetrived);
    }

    public String getApprobedRatioString() {
        // TODO: No calcular solo con la cantidad de materias, sino tambien con los creditos desde web service
        return "Materias aprobadas (en creditos): " + (this.approvedSubjects.size()*6) +  "/" +  (this.careerSubjects.size()*6);
    }

    /**
     * Helper para crear un dialog para ver las materias aprobadas
     * @param approvedSubjects
     * @return
     */
    public static Dialog createApprovedSubjectsDialog(Activity callingActivity,  List<String> approvedSubjects) {
        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
        // Get the layout inflater
        LayoutInflater inflater = callingActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_list_dialog, null);

        // Cambiamos el titulo del dialog
        ((TextView) dialogView.findViewById(R.id.title)).setText("Materias aprobadas");

        // Llenamos la lista con las materias
        ListView listView = (ListView) dialogView.findViewById(R.id.list);
        ArrayAdapter adapter = new ArrayAdapter<>(callingActivity, android.R.layout.simple_list_item_1, approvedSubjects);
        listView.setAdapter(adapter);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hace falta hacer nada
                    }
                });
        return builder.create();
    }
}
