package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class SignUpHttpAsyncTask extends HttpAsyncTask {

    String email, password, firstName, lastName, padron;

    public SignUpHttpAsyncTask(Activity callingActivity, String firstName, String lastName, String email, String padron, String password) {
        super(callingActivity);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.padron = padron;
        this.password = password;
    }

    @Override
    protected void configureRequestFields() {
        this.addRequestField("email", this.email);
        this.addRequestField("password", this.password);
        this.addRequestField("firstName", this.firstName);
        this.addRequestField("lastName", this.lastName);
        this.addRequestField("padron", this.padron);
    }

    @Override
    protected void configureResponseFields() {

    }

    @Override
    protected void onResponseArrival() {
        if (this.responseCode == HttpURLConnection.HTTP_CREATED) {
            final String thanksText = String.format("Gracias %s por registrarte. A la brevedad se enviará un mail a %s confirmando tu cuenta.", this.firstName, this.email);
            AlertDialog helpDialog = this.generateAlert("Gracias !", thanksText);
            helpDialog.show();
        } else if (this.responseCode == HttpURLConnection.HTTP_CONFLICT) {
            AlertDialog helpDialog = this.generateAlert("Error", "El usuario ingresado ya existe");
            helpDialog.show();
        } else {
            AlertDialog helpDialog = this.generateAlert("Error", "Error en la conexión con el servidor");
            helpDialog.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }

    private AlertDialog generateAlert(String alertName, String alertContent) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this.callingActivity);
        helpBuilder.setTitle(alertName);
        helpBuilder.setMessage(alertContent);
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        return helpBuilder.create();
    }

}
