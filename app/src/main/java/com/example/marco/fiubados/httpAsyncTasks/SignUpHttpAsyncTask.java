package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.example.marco.fiubados.LoginActivity;
import com.example.marco.fiubados.SignUpActivity;
import com.example.marco.fiubados.commons.AlertDialogBuilder;

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
            AlertDialog helpDialog = AlertDialogBuilder.generateAlertWithCustomBehavior(this.callingActivity, new Intent(this.callingActivity, LoginActivity.class), "Gracias !", thanksText);
            helpDialog.show();
        } else if (this.responseCode == HttpURLConnection.HTTP_CONFLICT) {
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this.callingActivity, "Error", "El usuario ingresado ya existe");
            helpDialog.show();
        } else {
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this.callingActivity, "Error", "Error en la conexión con el servidor");
            helpDialog.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }

}
