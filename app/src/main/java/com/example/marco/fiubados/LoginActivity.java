package com.example.marco.fiubados;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.marco.fiubados.httpAsyncTasks.LoginHttpAsyncTask;


public class LoginActivity extends ActionBarActivity {

    private static final String LOGIN_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/sign_in";

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
        LoginHttpAsyncTask loginRequest = new LoginHttpAsyncTask(this, username.getText().toString(), password.getText().toString());
        loginRequest.execute(this.LOGIN_ENDPOINT_URL);
        //loginRequest.execute("http://www.mocky.io/v2/55298a2c22258fea02a378a1");
    }

    private void showSignUpMenu(){
        Intent intent = new Intent(this, SignUpActivity.class);
        this.startActivity(intent);
    }
}
