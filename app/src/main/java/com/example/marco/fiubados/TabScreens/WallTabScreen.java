package com.example.marco.fiubados.TabScreens;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.NotificationsActivity;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.DownloadPictureHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
import com.example.marco.fiubados.httpAsyncTasks.SendFriendRequestHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.UploadPictureHttpAsyncTask;
import com.example.marco.fiubados.model.User;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Marco on 08/04/2015.
 *
 * Maneja la lógica interna del tab de muro
 */
public class WallTabScreen implements CallbackScreen {

    private static final String SEND_FRIENDSHIP_REQUEST_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends/send_friendship_request";
    private static final String UPLOAD_IMAGE_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/upload_profile_picture";
    private final int SEND_FRIEND_REQUEST_SERVICE_ID = 0;
    private final int RESPOND_FRIEND_REQUEST_SERVICE_ID = 1;
    public static final int RESULT_LOAD_IMAGE = 2;
    private static final int UPLOAD_IMAGE_SERVICE_ID = 3;
    private TabbedActivity tabOwnerActivity;
    private User userOwnerOfTheWall;
    private Button addFriendButton, confirmFriendRequestButton;
    private TextView wallTitle;
    private ImageView profileImageView;
    private String picturePathUploading;

    public WallTabScreen(TabbedActivity tabOwnerActivity, Button addFriendButton, Button confirmFriendRequestButton, TextView wallTitle, ImageView profileImageView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.wallTitle = wallTitle;
        this.addFriendButton = addFriendButton;
        this.confirmFriendRequestButton = confirmFriendRequestButton;
        this.profileImageView = profileImageView;

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
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProfileImageTouch();
            }
        });
    }

    private void onProfileImageTouch() {
        if(this.userOwnerOfTheWall.equals(ContextManager.getInstance().getMyUser())){
            // Buscamos una nueva imagen y mandamos a cambiar la imagen de perfil
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            this.tabOwnerActivity.startActivityForResult(intent, this.RESULT_LOAD_IMAGE);
        }
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
            this.wallTitle.setText(this.userOwnerOfTheWall.getName() + " " + this.userOwnerOfTheWall.getLastName());

            // Hago visibles o no los botones de amistad
            if(!this.userOwnerOfTheWall.equals(ContextManager.getInstance().getMyUser())) {
                if (this.userOwnerOfTheWall.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_UNKNOWN)) {
                    this.addFriendButton.setVisibility(View.VISIBLE);
                } else if (this.userOwnerOfTheWall.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_WAITING)) {
                    this.confirmFriendRequestButton.setVisibility(View.VISIBLE);
                }
            }
        }
        // Si hay, mostramos la imagen de perfil
        this.presentProfilePicture();
    }

    private void presentProfilePicture(){
        // Traemos del servidor la imagen de perfil y la mostramos (si hay)
        String profilePictureUrl = ContextManager.WS_SERVER_URL + this.getUserOwnerOfTheWall().getProfilePicture();
        DownloadPictureHttpAsyncTask pictureService = new DownloadPictureHttpAsyncTask(profilePictureUrl);
        try {
            Drawable d = pictureService.execute().get();
            if(d != null) {
                this.profileImageView.setImageDrawable(d);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
        else if(serviceId == this.RESPOND_FRIEND_REQUEST_SERVICE_ID){
            // Pudimos confirmar el request de amistad y ya somos amigos, sacamos el botón de confirmación
            this.confirmFriendRequestButton.setVisibility(View.GONE);
            Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), "Ahora son amigos", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(serviceId == this.UPLOAD_IMAGE_SERVICE_ID){
            // Pudimos cambiar la imagen de perfil correctamente
            this.onProfileImageChanged();
            Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), "Ha cambiado su foto de perfil", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void onProfileImageChanged() {
        // Colocamos la nueva foto de perfil en nuestro muro
        this.profileImageView.setImageBitmap(BitmapFactory.decodeFile(this.picturePathUploading));
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

    public void processProfileImageChange(Intent data) {
        // Obtenemos el path de la imagen conseguida en la galería
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.tabOwnerActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        this.picturePathUploading = cursor.getString(columnIndex);
        cursor.close();

        // Enviamos la imagen al servidor
        UploadPictureHttpAsyncTask service = new UploadPictureHttpAsyncTask(this.tabOwnerActivity, this, this.UPLOAD_IMAGE_SERVICE_ID, this.picturePathUploading);
        service.execute(this.UPLOAD_IMAGE_SERVICE_ENDPOINT_URL);
    }
}

