package com.example.marco.fiubados.commons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.JobsProfileEditActivity;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.JobsEditAndCreateHttpAsyncTask;
import com.example.marco.fiubados.model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 10/05/2015.
 */
public class FormDialogBuilder {

    public static void showProfileInstitutionDialog(final Activity ownerActivity, final DialogCallback dialogCallback, final int dialogId, final List<String> inputs, int layoutId) {
        showProfileInstitutionDialog(ownerActivity, dialogCallback, dialogId, inputs, layoutId, R.string.accept, R.string.cancel);
    }
    public static void showProfileInstitutionDialog(final Activity ownerActivity, final DialogCallback dialogCallback, final int dialogId, final List<String> inputs, int layoutId, int acceptLabelId, int cancelLabelId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ownerActivity);
        // Get the layout inflater
        LayoutInflater inflater = ownerActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(layoutId, null);

        // Cargamos los valores iniciales en los campos
        if(inputs.size() >= 4) {
            ((EditText) dialogView.findViewById(R.id.fieldValueCompany)).setText(inputs.get(0));
            ((EditText) dialogView.findViewById(R.id.fieldValuePosition)).setText(inputs.get(1));
            ((TextView) dialogView.findViewById(R.id.fieldValueStartDate)).setText(inputs.get(2));
            ((TextView) dialogView.findViewById(R.id.fieldValueEndDate)).setText(inputs.get(3));
        }
        // Manejamos los botones de date picker
        ImageButton showStartDatePicker = (ImageButton) dialogView.findViewById(R.id.startDateButton);
        showStartDatePicker.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        createDatePickerDialog(ownerActivity, dialogView, R.id.fieldValueStartDate);
                    }
                });
        ImageButton showEndDatePicker = (ImageButton) dialogView.findViewById(R.id.endDateButton);
        showEndDatePicker.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        createDatePickerDialog(ownerActivity, dialogView, R.id.fieldValueEndDate);
                    }
                });
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(acceptLabelId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos y los enviamos al que nos llamó
                        List<String> outputs = new ArrayList<String>();
                        outputs.add(((EditText) dialogView.findViewById(R.id.fieldValueCompany)).getText().toString());
                        outputs.add(((EditText) dialogView.findViewById(R.id.fieldValuePosition)).getText().toString());
                        outputs.add(((TextView) dialogView.findViewById(R.id.fieldValueStartDate)).getText().toString());
                        outputs.add(((TextView) dialogView.findViewById(R.id.fieldValueEndDate)).getText().toString());
                        dialogCallback.onDialogClose(dialogId, outputs, true);
                    }
                })
                .setNegativeButton(cancelLabelId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hace falta hacer ninguna acción
                    }
                });
        builder.create().show();
    }

    private static void createDatePickerDialog(Activity ownerActivity, View callingView, int fieldId){
        AlertDialog.Builder builder = new AlertDialog.Builder(ownerActivity);
        // Get the layout inflater
        LayoutInflater inflater = ownerActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_datepicker, null);

        final TextView dateField = (TextView) callingView.findViewById(fieldId);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);


        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();
                        dateField.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
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
