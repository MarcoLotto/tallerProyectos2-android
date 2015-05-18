package com.example.marco.fiubados.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.marco.fiubados.R;

public class GroupMainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_main, menu);

        // Mostramos el boton de agregar discución
        menu.findItem(R.id.addAction).setVisible(true);  // TODO: Solo hacer visible si estoy suscripto al grupo

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_group_information) {
            startActivity(new Intent(this, GroupInformationActivity.class));
            return true;
        }

        if (id == R.id.action_group_members) {
            startActivity(new Intent(this, GroupMembersActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}