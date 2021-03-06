package com.example.marco.fiubados.activity.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.marco.fiubados.R;
import com.example.marco.fiubados.commons.ActivityStackManager;

public class GroupDiscussionActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_DISSCUSION_ID = "extra_param_disscusion_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_discussion);
        ActivityStackManager.getInstance().addActivityToStack(this);
    }

    @Override
    public void onDestroy(){
        ActivityStackManager.getInstance().removeActivityFromStack(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_discussion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
