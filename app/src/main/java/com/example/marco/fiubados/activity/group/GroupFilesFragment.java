package com.example.marco.fiubados.activity.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.FileListAdapter;
import com.example.marco.fiubados.commons.FieldsValidator;
import com.example.marco.fiubados.httpAsyncTasks.GetGroupFilesHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.GroupFileCreateHttpAsyncTask;
import com.example.marco.fiubados.model.File;
import com.example.marco.fiubados.model.Group;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupFilesFragment extends Fragment implements CallbackScreen {

    private static final String GROUPS_SERVICE_URL = ContextManager.WS_SERVER_URL + "/api/groups/";
    private static final String CREATE_UPLOADED_DATA_SERVICE_ENDPOINT_URL = "/uploaded_data/";
    private static final String GET_UPLOADED_DATA_ENDPOINT_URL = "/uploaded_data/";

    private static final int GET_UPLOADED_DATA_SERVICE_ID = 0;
    private static final int CREATE_UPLOADED_DATA_SERVICE_ID = 1;
    private Group group;
    private User user;

    private View rootView;

    private List<File> mFiles = new ArrayList<>();
    private FileListAdapter mGroupFilesAdapter;

    public GroupFilesFragment() {}

    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
    //       super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
    //       setHasOptionsMenu(true);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group_files, container, false);

        group = ContextManager.getInstance().groupToView;
        user = ContextManager.getInstance().getMyUser();

        //ACA TENGO QUE TRAER LOS FILES

        //User user = new User("1", "Ezequiel", "Pérez Dittler");
        //File youtubeFile = new File("El experto","https://www.youtube.com/watch?v=BKorP55Aqvg",user);
        //File pdfFile = new File("User Stories Applieds","http://www.mountaingoatsoftware.com/system/asset/file/259/User-Stories-Applied-Mike-Cohn.pdf",user);
        //mFiles.add(youtubeFile);
        //mFiles.add(pdfFile);

        // Get a reference to the ListView, and attach this adapter to it.
        //UpdateUIList();
        return rootView;
    }

    private void onFileItemClick(File file) {
        //Toast.makeText(getActivity(), "Se ha seleccionado " + file.getName(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(file.getUrl())));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_group_new_file) {
            createAddFileDialog(getActivity(), this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAddFileDialog(final Activity ownerActivity, final CallbackScreen ownerCallbackScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ownerActivity);
        // Get the layout inflater
        LayoutInflater inflater = ownerActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_file_dialog, null);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Conseguimos todos los valores de los campos
                        String name = ((EditText) dialogView.findViewById(R.id.fieldValueName)).getText().toString();
                        String url = ((EditText) dialogView.findViewById(R.id.fieldValueUrl)).getText().toString();
                        String type = ((EditText) dialogView.findViewById(R.id.fieldValueType)).getText().toString();

                        // TODO:Falta Validar los campos
                        // TODO: el type deberia ser parseado del nombre...
                        // Validate URL
                        if (!URLUtil.isValidUrl(url)) {
                            Toast.makeText(ownerActivity.getApplicationContext(), "URL invalida", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ( (FieldsValidator.isTextFieldValid(name, 1))
                                    // &&(FieldsValidator.isTextFieldValid(type, 1))
                                ){
                            File file = new File("",name,url, user );
                            GroupFileCreateHttpAsyncTask service = new GroupFileCreateHttpAsyncTask(ownerActivity, ownerCallbackScreen, CREATE_UPLOADED_DATA_SERVICE_ID, file);
                            String finalUrl = GROUPS_SERVICE_URL + group.getId() + CREATE_UPLOADED_DATA_SERVICE_ENDPOINT_URL;
                            service.execute(finalUrl);
                    }
                        else
                    {
                        Toast toast = Toast.makeText(ownerActivity.getApplicationContext(), "Error en los campos ingresados, ninguno puede estar vacio", Toast.LENGTH_LONG);
                        toast.show();
                    }
                 }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hace falta hacer ninguna acción
                    }
                });
        builder.create().show();
    }

    @Override
    public void onFocus() {
        // Buscamos los files del grupo
        GetGroupFilesHttpAsyncTask service = new GetGroupFilesHttpAsyncTask(getActivity(), this, GET_UPLOADED_DATA_SERVICE_ID, group);

        //String finalUrl = GROUPS_SERVICE_URL + group.getId() + GET_UPLOADED_DATA_ENDPOINT_URL;
        String finalUrl = "http://www.mocky.io/v2/5574a6bf094d18441711da0e";
        service.execute(finalUrl);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == GET_UPLOADED_DATA_SERVICE_ID){
            // Actalizamos las discusiones de la lista
            this.UpdateUIList();
        }
        else if(serviceId == CREATE_UPLOADED_DATA_SERVICE_ID ){
            this.onFocus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onFocus();
    }

    private void UpdateUIList( ) {
        // Get a reference to the ListView, and attach this adapter to it.
        mGroupFilesAdapter = new FileListAdapter(getActivity(), group.getFiles());
        ListView listView = (ListView) rootView.findViewById(R.id.group_files_list_view);
        listView.setAdapter(mGroupFilesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                File file = (File) mGroupFilesAdapter.getItem(position);
                onFileItemClick(file);
            }
        });
    }
}
