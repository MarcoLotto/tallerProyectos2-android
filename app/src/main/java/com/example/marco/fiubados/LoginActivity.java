package com.example.marco.fiubados;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.LoginHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.User;

import java.util.List;


public class LoginActivity extends AppCompatActivity implements TabScreen {

    private static final String LOGIN_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/sign_in";
    private static final int LOGIN_SERVICE_ID = 0;
    private static final int SEARCH_PROFILE_INFO_SERVICE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Cargamos el onClick listener al boton de login
        Button loginButton = (Button) findViewById(R.id.signInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeLogin();
            }
        });

        // Cargamos el onClick listener al boton de signUp
        Button loginSignUpButton = (Button) findViewById(R.id.loginSignUpButton);
        loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void makeLogin(){
        EditText username = (EditText) findViewById(R.id.usernameTextBox);
        EditText password = (EditText) findViewById(R.id.passwordTextBox);

        if(!FieldsValidator.isTextFieldValid(password.getText().toString(), 8)){
            Toast toast = Toast.makeText(getApplicationContext(), "La contraseña debe tener un mínimo de 8 caracteres", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            LoginHttpAsyncTask loginRequest = new LoginHttpAsyncTask(this, this, this.LOGIN_SERVICE_ID, username.getText().toString(), password.getText().toString());
            loginRequest.execute(this.LOGIN_ENDPOINT_URL);
        }
    }

    private void showSignUpMenu(){
        Intent intent = new Intent(this, SignUpActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.LOGIN_SERVICE_ID){
            // Pedimos el perfil del usuario para llenarlo
            User myUser = ContextManager.getInstance().getMyUser();
            ProfileInfoHttpAsyncTask personalInfoService = new ProfileInfoHttpAsyncTask(this, this, this.SEARCH_PROFILE_INFO_SERVICE_ID, myUser);
            personalInfoService.execute(ProfileActivity.SHOW_PROFILE_ENDPOINT_URL);
        }
        else if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){
            // Abrimos la pantalla principal
            Intent intent = new Intent(this, MainScreenActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }
}
