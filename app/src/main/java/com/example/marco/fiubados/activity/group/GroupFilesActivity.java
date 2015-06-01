package com.example.marco.fiubados.activity.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.commons.ActivityStackManager;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.GroupDiscussionCreateHttpAsyncTask;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;

public class GroupFilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_files);
        ActivityStackManager.getInstance().addActivityToStack(this);

        // Create new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment newFragment;

        newFragment = new GroupFilesFragment();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.add(R.id.group_main_container, newFragment);

        // Commit the transaction
        transaction.commit();
}

    @Override
    public void onDestroy(){
        ActivityStackManager.getInstance().removeActivityFromStack(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_files, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_group_files) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
