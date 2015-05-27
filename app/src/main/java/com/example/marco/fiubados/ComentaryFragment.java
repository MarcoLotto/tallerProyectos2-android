package com.example.marco.fiubados;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.*;
import com.example.marco.fiubados.httpAsyncTasks.GetComentariesHttpAsyncTask;
import com.example.marco.fiubados.model.Comentary;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.TripleField;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComentaryFragment extends Fragment implements CallbackScreen {

    public static final String EXTRA_PARAM_GET_COMENTARIES_URL = "extra_param_get_cometaries_url";
    public static final String EXTRA_PARAM_CONTAINER_ID = "extra_param_container_id";

    private static final int GET_COMENTARIES_SERVICE_ID = 0;

    private TextView noCommentsTextView;
    private ListView comentaryListView;
    private String getComentariesUrl;
    private String containerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comentary, container, false);

        this.noCommentsTextView = (TextView) rootView.findViewById(R.id.noCommentsTextView);
        this.comentaryListView = (ListView) rootView.findViewById(R.id.comentaryListView);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = this.getActivity().getIntent().getExtras();
        if (params != null){
            this.getComentariesUrl = params.getString(EXTRA_PARAM_GET_COMENTARIES_URL);
            this.containerId = params.getString(EXTRA_PARAM_CONTAINER_ID);
        }

        this.onFocus();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        this.onFocus();
    }

    @Override
    public void onFocus() {
        // Llamamos al servicio de obtención de comentarios
        GetComentariesHttpAsyncTask service = new GetComentariesHttpAsyncTask(this.getActivity(), this, GET_COMENTARIES_SERVICE_ID, this.containerId);
        service.execute(this.getComentariesUrl);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == GET_COMENTARIES_SERVICE_ID){
            // Llenamos la lista con los comentarios
            this.fillUIListWithComentaries(responseElements);
        }
    }

    private void fillUIListWithComentaries(List<Comentary> responseElements) {
        List<TripleField> finalListViewLines = new ArrayList<>();
        Iterator<Comentary> it = responseElements.iterator();
        while(it.hasNext()){
            Comentary comentary = it.next();
            finalListViewLines.add(new TripleField(new Field("Autor", comentary.getAuthor()),
                    new Field("Mensaje", comentary.getMessage()), new Field("ImageURL", comentary.getImageUrl())));
        }

        this.comentaryListView.setAdapter(new TwoLinesAndImageListAdapter(finalListViewLines, this.getActivity(), this.comentaryListView));

        if(responseElements.isEmpty()){
            this.noCommentsTextView.setVisibility(View.VISIBLE);
            this.comentaryListView.setVisibility(View.GONE);
        }
        else{
            this.noCommentsTextView.setVisibility(View.GONE);
            this.comentaryListView.setVisibility(View.VISIBLE);
        }
    }
}