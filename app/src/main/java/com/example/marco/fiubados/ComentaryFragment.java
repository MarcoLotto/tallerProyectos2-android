package com.example.marco.fiubados;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.*;
import com.example.marco.fiubados.commons.ActivityStackManager;
import com.example.marco.fiubados.httpAsyncTasks.GetCommentsHttpAsyncTask;
import com.example.marco.fiubados.model.Comentary;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.MultipleField;
import com.example.marco.fiubados.model.TripleField;
import com.example.marco.fiubados.model.User;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ComentaryFragment extends Fragment implements CallbackScreen {

    public static final String EXTRA_PARAM_GET_COMENTARIES_URL = "extra_param_get_cometaries_url";
    public static final String EXTRA_PARAM_CONTAINER_ID = "extra_param_container_id";

    private static final int GET_COMENTARIES_SERVICE_ID = 0;

    private TextView noCommentsTextView;
    private ListView comentaryListView;
    private String getComentariesUrl;
    private String containerId;
    private List<Comentary> comments;

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

        this.configureComponents();
        this.onFocus();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onFocus() {
        // Llamamos al servicio de obtención de comentarios
        GetCommentsHttpAsyncTask service = new GetCommentsHttpAsyncTask(this.getActivity(), this, GET_COMENTARIES_SERVICE_ID, this.containerId);
        service.execute(this.getComentariesUrl);
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == GET_COMENTARIES_SERVICE_ID){
            // Llenamos la lista con los comentarios
            this.fillUIListWithComentaries(responseElements);
        }
    }

    private String humanReadableDate(String timestampDate){
        String finalTimeStamp = timestampDate.substring(0, 19);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0"));
        try {
            Date date = sdf.parse(finalTimeStamp);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'a las' HH:mm:ss ");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT-3"));
            return formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void fillUIListWithComentaries(List<Comentary> responseElements) {
        List<MultipleField> finalListViewLines = new ArrayList<>();
        this.comments = responseElements;
        Iterator<Comentary> it = responseElements.iterator();
        while(it.hasNext()){
            Comentary comentary = it.next();
            String authorName = comentary.getAuthor().getFirstName() + " " + comentary.getAuthor().getLastName();
            finalListViewLines.add(new MultipleField(new Field("Titulo", authorName),
                    new Field("Mensaje", comentary.getMessage()),
                    new Field("Date", this.humanReadableDate(comentary.getDate())),
                    new Field("ImageURL", comentary.getImageUrl())));
        }
        this.comentaryListView.setAdapter(new ThreeLinesAndImageListAdapter(finalListViewLines, this.getActivity(), this.comentaryListView));

        if(responseElements.isEmpty()){
            this.noCommentsTextView.setVisibility(View.VISIBLE);
            this.comentaryListView.setVisibility(View.GONE);
        }
        else{
            this.noCommentsTextView.setVisibility(View.GONE);
            this.comentaryListView.setVisibility(View.VISIBLE);
        }
    }

    private void configureComponents() {
        // Configuramos el handler del onClick del friendsListView
        this.comentaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onCommentClickedOnList(position);
            }
        });
    }

    private void onCommentClickedOnList(int position) {
        if(position < this.comments.size()) {
            User author = this.comments.get(position).getAuthor();
            MainScreenActivity mainScreenActivity = ContextManager.getInstance().getMainScreenActivity();
            mainScreenActivity.getWallTabScreen().setUserOwnerOfTheWall(author);
            mainScreenActivity.selectWallTabScreen();
            ActivityStackManager.getInstance().goToMainScreenActivity();
        }
    }
}