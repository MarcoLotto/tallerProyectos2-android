package com.example.marco.fiubados;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.marco.fiubados.commons.AlertDialogBuilder;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.SignUpHttpAsyncTask;

import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends ActionBarActivity {

    private static final String SIGNUP_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/sign_up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Cargamos el onClick listener al boton de signUp
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSignUp();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    private void makeSignUp(){
        EditText firstName = (EditText) findViewById(R.id.firstNameTextBox);
        EditText lastName = (EditText) findViewById(R.id.lastNameTextBox);
        EditText padron = (EditText) findViewById(R.id.padronTextBox);
        EditText email = (EditText) findViewById(R.id.emailTextBox);
        EditText password = (EditText) findViewById(R.id.passwordTextBox);

        if(!FieldsValidator.isTextFieldValid(firstName.getText().toString(), 1)){
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this, "Atención", "El campo nombre no puede estar vacío");
            helpDialog.show();
            return;
        }
        if(!FieldsValidator.isTextFieldValid(lastName.getText().toString(), 1)){
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this, "Atención", "El campo apellido no puede estar vacío");
            helpDialog.show();
            return;
        }
        if(!FieldsValidator.isNumericFieldValid(padron.getText().toString(), 5)){
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this, "Atención", "El campo padrón debe ser numerico y tener un mínimo de 5 digitos");
            helpDialog.show();
            return;
        }
        List<String> validationList = new ArrayList<String>();
        validationList.add("@");
        validationList.add(".");
        if(!FieldsValidator.isTextFieldValid(email.getText().toString(), 1, validationList)){
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this, "Atención", "El campo email no puede estar vacío y debe corresponder a un mail fiuba");
            helpDialog.show();
            return;
        }
        if(!FieldsValidator.isTextFieldValid(password.getText().toString(), 8)){
            AlertDialog helpDialog = AlertDialogBuilder.generateAlert(this, "Atención", "El campo contraseña debe tenér como mínimo 8 caracteres");
            helpDialog.show();
            return;
        }

        SignUpHttpAsyncTask signUpRequest = new SignUpHttpAsyncTask(this, firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), padron.getText().toString(), password.getText().toString());
        signUpRequest.execute(this.SIGNUP_ENDPOINT_URL);
    }
}
