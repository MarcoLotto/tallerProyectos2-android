package com.example.marco.fiubados.TabScreens;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.NotificationsActivity;
import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
import com.example.marco.fiubados.httpAsyncTasks.GetPendingRequestsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.LoginHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SendFriendRequestHttpAsyncTask;
import com.example.marco.fiubados.model.User;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 08/04/2015.
 *
 * Maneja la lógica interna del tab de muro
 */
public class WallTabScreen implements TabScreen{

    private static final String SEND_FRIENDSHIP_REQUEST_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends/send_friendship_request";
    private final int SEND_FRIEND_REQUEST_SERVICE_ID = 0;
    private final int GET_FRIEND_REQUESTS_SERVICE_ID = 1;
    private final int RESPOND_FRIEND_REQUEST_SERVICE_ID = 2;
    private TabbedActivity tabOwnerActivity;
    private User userOwnerOfTheWall;
    private Button addFriendButton, confirmFriendRequestButton;
    private TextView wallTitle;

    public WallTabScreen(TabbedActivity tabOwnerActivity, Button addFriendButton, Button confirmFriendRequestButton, TextView wallTitle){
        this.tabOwnerActivity = tabOwnerActivity;
        this.wallTitle = wallTitle;
        this.addFriendButton = addFriendButton;
        this.confirmFriendRequestButton = confirmFriendRequestButton;

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFriendRequest();
            }
        });
        confirmFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmFriendRequestButtonClick();
            }
        });
    }

    private void onConfirmFriendRequestButtonClick() {
        // Llamamos al servicio para confirmar la solicitud de amistad
        FriendshipResponseHttpAsynkTask service = new FriendshipResponseHttpAsynkTask(this.tabOwnerActivity, this, this.RESPOND_FRIEND_REQUEST_SERVICE_ID,
                this.userOwnerOfTheWall.getFriendshipRequestId(), NotificationsActivity.FRIENDSHIP_RESPONSE_STATUS_ACCEPT);
        service.execute(NotificationsActivity.FRIENDSHIP_CONFIRMATION_ENDPOINT_URL);
    }

    @Override
    public void onFocus() {
        this.addFriendButton.setVisibility(View.GONE);
        this.confirmFriendRequestButton.setVisibility(View.GONE);
        if(this.userOwnerOfTheWall != null) {
            // Seteamos como titulo del muro el nombre de la persona
            this.wallTitle.setText(this.userOwnerOfTheWall.getName());

            // Hacemos la llamada al servicio de busqueda de solicitudes de amistad
            GetPendingRequestsHttpAsyncTask httpService = new GetPendingRequestsHttpAsyncTask(this.tabOwnerActivity, this, this.GET_FRIEND_REQUESTS_SERVICE_ID, "TODO");
            httpService.execute(NotificationsActivity.PENDING_FRIEND_REQUESTS_ENDPOINT_URL);
        }
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == SEND_FRIEND_REQUEST_SERVICE_ID){
            if(responseElements.size() > 0 && responseElements.get(0).equals("ok")){
                // Si se envio la solicitud de amistad quitamos el boton
                this.addFriendButton.setVisibility(View.GONE);
                this.confirmFriendRequestButton.setVisibility(View.GONE);
                Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), "Solicitud enviada", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if(serviceId == GET_FRIEND_REQUESTS_SERVICE_ID){
            // Si el usuario del muro me mando una solicitud de amistad, ponemos el botón de responder solicitud de amistad
            Iterator<User> it = responseElements.iterator();
            while(it.hasNext()){
                User user = it.next();
                if(this.userOwnerOfTheWall.equals(user)){
                    this.addFriendButton.setVisibility(View.GONE);
                    this.confirmFriendRequestButton.setVisibility(View.VISIBLE);
                    this.userOwnerOfTheWall = user; // Equals es distinto de =, esto es necesario
                    return;
                }
            }
            // No nos mandó una solicitud, manejamos cuando mostrar el botón de enviar amistad
            boolean isCurrentUserMyUser = this.userOwnerOfTheWall.getId() == ContextManager.getInstance().getMyUser().getId();
            if(!isCurrentUserMyUser && this.userOwnerOfTheWall.getFriendshipStatus() == User.FRIENDSHIP_STATUS_UNKNOWN) {
                this.addFriendButton.setVisibility(View.VISIBLE);
                this.confirmFriendRequestButton.setVisibility(View.GONE);
            }
        }
        else if(serviceId == this.RESPOND_FRIEND_REQUEST_SERVICE_ID){
            // Pudimos confirmar el request de amistad y ya somos amigos, sacamos el botón de confirmación
            this.confirmFriendRequestButton.setVisibility(View.GONE);
            Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), "Ahora son amigos", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void sendFriendRequest(){
        SendFriendRequestHttpAsyncTask sendFriendRequest = new SendFriendRequestHttpAsyncTask(this.tabOwnerActivity, this,
                SEND_FRIEND_REQUEST_SERVICE_ID, this.userOwnerOfTheWall.getId());
        sendFriendRequest.execute(this.SEND_FRIENDSHIP_REQUEST_ENDPOINT_URL);
    }

    public User getUserOwnerOfTheWall() {
        return userOwnerOfTheWall;
    }

    public void setUserOwnerOfTheWall(User userOwnerOfTheWall) {
        // TODO: Si nos falta información del usuario deberiamos ir a buscarla
        this.userOwnerOfTheWall = userOwnerOfTheWall;
    }
}

