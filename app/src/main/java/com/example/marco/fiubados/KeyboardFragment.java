package com.example.marco.fiubados;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.SendCommentHttpAsyncTask;

import java.util.List;

public class KeyboardFragment extends Fragment implements CallbackScreen {

    public static final String EXTRA_PARAM_SEND_COMENTARY_URL = "extra_param_send_cometary_url";
    public static final String EXTRA_PARAM_CONTAINER_ID = "extra_param_container_id";
    public static final String EXTRA_PARAM_PARENT_COMENTARY_ID = "extra_param_parent_comentary_id";

    private static final int SEND_COMENTARY_SERVICE_ID = 0;

    private String sendComentaryUrl;
    private String containerId;
    private String parentComentaryId;

    private EditText comentaryEditText;
    private Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_keyboard, container, false);

        this.comentaryEditText = (EditText) rootView.findViewById(R.id.comentaryEditText);
        this.sendButton = (Button) rootView.findViewById(R.id.sendButton);

        // Conseguimos el parametro que nos paso el activity que nos llamó
        Bundle params = this.getActivity().getIntent().getExtras();
        this.parentComentaryId = params.getString(EXTRA_PARAM_PARENT_COMENTARY_ID);
        this.sendComentaryUrl = params.getString(EXTRA_PARAM_SEND_COMENTARY_URL);
        this.containerId = params.getString(EXTRA_PARAM_CONTAINER_ID);

        this.configureComponents();
        this.onFocus();

        return rootView;
    }

    private void configureComponents() {
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComentary();
            }
        });
    }

    private void sendComentary() {
        String message = this.comentaryEditText.getText().toString();
        if(!message.isEmpty()){
            SendCommentHttpAsyncTask service = new SendCommentHttpAsyncTask(this.getActivity(), this, SEND_COMENTARY_SERVICE_ID, this.containerId, this.parentComentaryId, message);
            service.execute(this.sendComentaryUrl);
        }
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
        if(serviceId == SEND_COMENTARY_SERVICE_ID){
            // Indicamos al activity que se vuelva a crear para recargar la lista
            this.getActivity().finish();
            this.getActivity().startActivity(this.getActivity().getIntent());
        }
    }
}