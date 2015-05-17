package com.example.marco.fiubados.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.TwoLinesListAdapter;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupDiscussionsHttpAsyncTask;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.GroupDiscussion;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupMainActivity extends AppCompatActivity implements CallbackScreen{

    private static final int GET_DISCUSSIONS_SERVICE_ID = 0;
    private static final String GET_DISCUSSIONS_ENDPOINT_URL = "http://www.mocky.io/v2/555902e73c2e8f020b9e764f";
    private ListView discussionsListView;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        this.discussionsListView = (ListView) this.findViewById(R.id.discussionsListView);
        this.group = ContextManager.getInstance().groupToView;
        this.onFocus();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_group_information) {
            startActivity(new Intent(this, GroupInformationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocus() {
        // Buscamos las discuciones del grupo
        GetGroupDiscussionsHttpAsyncTask service = new GetGroupDiscussionsHttpAsyncTask(this, this, this.GET_DISCUSSIONS_SERVICE_ID, this.group);
        service.execute(this.GET_DISCUSSIONS_ENDPOINT_URL);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.GET_DISCUSSIONS_SERVICE_ID){
           // Actalizamos las discusiones de la lista
           this.addDiscussionsToUIList();
        }
    }

    private void addDiscussionsToUIList() {
        List<DualField> finalListViewLines = new ArrayList<>();
        Iterator<GroupDiscussion> it = this.group.getDiscussions().iterator();
        while(it.hasNext()){
            GroupDiscussion discussion = it.next();
            finalListViewLines.add(new DualField(new Field("Nombre", discussion.getName()), new Field("Autor", "Creado por " + discussion.getAuthor())));
        }
        this.discussionsListView.setAdapter(new TwoLinesListAdapter(this.getApplicationContext(), finalListViewLines));
    }
}
