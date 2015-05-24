package com.example.marco.fiubados;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.adapters.*;
import com.example.marco.fiubados.httpAsyncTasks.GetComentariesHttpAsyncTask;
import com.example.marco.fiubados.model.Comentary;
import com.example.marco.fiubados.model.DualField;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.TripleField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyboardFragment extends Fragment implements CallbackScreen {

    public static final String EXTRA_PARAM_SEND_COMENTARY_URL = "extra_param_send_cometary_url";
    public static final String EXTRA_PARAM_CONTAINER_ID = "extra_param_container_id";

    private static final int SEND_COMENTARY_SERVICE_ID = 0;

    private String sendComentaryUrl;
    private String containerId;
    private EditText comentaryEditText;
    private Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);

        this.comentaryEditText = (EditText) rootView.findViewById(R.id.noCommentsTextView);
        this.sendButton = (Button) rootView.findViewById(R.id.comentaryListView);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = this.getActivity().getIntent().getExtras();
        this.sendComentaryUrl = params.getString(this.EXTRA_PARAM_SEND_COMENTARY_URL);
        this.containerId = params.getString(this.EXTRA_PARAM_CONTAINER_ID);

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

    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.SEND_COMENTARY_SERVICE_ID){
            // TODO
        }
    }
}