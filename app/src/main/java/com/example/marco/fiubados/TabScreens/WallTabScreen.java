package com.example.marco.fiubados.TabScreens;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.NotificationsActivity;
import com.example.marco.fiubados.ProfileActivity;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.httpAsyncTasks.DownloadPictureHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SendFriendRequestHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.UploadPictureHttpAsyncTask;
import com.example.marco.fiubados.model.User;

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
    private static final String DEFAULT_PROFILE_PICTURE = "ic_action_picture_holo_light";
    private static final int PROFILE_PICTURE_MAX_SIZE = 524228;

    private final int SEND_FRIEND_REQUEST_SERVICE_ID = 0;
    private final int RESPOND_FRIEND_REQUEST_SERVICE_ID = 1;
    public static final int RESULT_LOAD_IMAGE = 2;
    private static final int UPLOAD_IMAGE_SERVICE_ID = 3;
    private static final int RELOAD_PROFILE_SERVICE_ID = 4;
    private static final int GET_PROFILE_PICTURE_SERVICE_ID = 5;

    private TabbedActivity tabOwnerActivity;
    private User userOwnerOfTheWall;
    private Button addFriendButton, confirmFriendRequestButton;
    private TextView wallTitleTextView;
    private TextView friendRequestSent;
    private ImageView profileImageView;

    public WallTabScreen(TabbedActivity tabOwnerActivity, Button addFriendButton, Button confirmFriendRequestButton, TextView wallTitleTextView, TextView friendRequestSent, ImageView profileImageView){
        this.tabOwnerActivity = tabOwnerActivity;
        this.wallTitleTextView = wallTitleTextView;
        this.friendRequestSent = friendRequestSent;
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
            this.tabOwnerActivity.startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
        this.friendRequestSent.setVisibility(View.GONE);
        if(this.userOwnerOfTheWall != null) {
            // Seteamos como titulo del muro el nombre de la persona
            this.wallTitleTextView.setText(this.userOwnerOfTheWall.getFullName());

            // Hago visibles o no los botones de amistad
            if(!this.userOwnerOfTheWall.equals(ContextManager.getInstance().getMyUser())) {
                if (this.userOwnerOfTheWall.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_UNKNOWN)) {
                    this.addFriendButton.setVisibility(View.VISIBLE);
                } else if (this.userOwnerOfTheWall.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_WAITING)) {
                    this.confirmFriendRequestButton.setVisibility(View.VISIBLE);
                } else if (this.userOwnerOfTheWall.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_REQUESTED)) {
                    this.friendRequestSent.setVisibility(View.VISIBLE);
                }
            }
        }
        // Si hay, vamos a buscar la imagen de perfil
        this.findProfilePicture();
    }

    private void findProfilePicture(){
        // Temporalmente, cargamos una imagen de perfil por defecto de los assets
        int resId = this.tabOwnerActivity.getResources().getIdentifier(this.DEFAULT_PROFILE_PICTURE, "drawable", this.tabOwnerActivity.getPackageName());
        this.profileImageView.setImageResource(resId);

        // Traemos del servidor la imagen de perfil y la mostramos (si hay)
        String profilePictureUrl = ContextManager.WS_SERVER_URL + this.getUserOwnerOfTheWall().getProfilePicture();
        DownloadPictureHttpAsyncTask pictureService = new DownloadPictureHttpAsyncTask(profilePictureUrl, this.tabOwnerActivity, this, this.GET_PROFILE_PICTURE_SERVICE_ID);
        pictureService.execute();

    }

    private void presentProfilePicture(List drawables){
        // Seteamos la nueva imagen
        if(!drawables.isEmpty()){
            Drawable d = (Drawable) drawables.get(0);
            if(d != null) {
                this.profileImageView.setImageDrawable(d);
            }
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
        else if(serviceId == RESPOND_FRIEND_REQUEST_SERVICE_ID){
            // Pudimos confirmar el request de amistad y ya somos amigos, sacamos el botón de confirmación
            this.confirmFriendRequestButton.setVisibility(View.GONE);
            Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), "Ahora son amigos", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(serviceId == UPLOAD_IMAGE_SERVICE_ID){
            // Pudimos cambiar la imagen de perfil correctamente
            this.onProfileImageChanged();
            Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), "Ha cambiado su foto de perfil", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(serviceId == this.RELOAD_PROFILE_SERVICE_ID){
            // Vamos a buscar a intenet nuestra imagen de perfil
            this.findProfilePicture();
        }
        else if(serviceId == this.GET_PROFILE_PICTURE_SERVICE_ID){
            // Hemos conseguido una imagen, vamos a presentarla
            this.presentProfilePicture(responseElements);
        }
    }

    private void onProfileImageChanged() {
        // Recargamos el perfil para recargar la nueva imagen del usuario
        // REVIEW: Para esto podria evitarse traerse todo el perfil
        ProfileInfoHttpAsyncTask service = new ProfileInfoHttpAsyncTask(this.tabOwnerActivity, this, RELOAD_PROFILE_SERVICE_ID, this.getUserOwnerOfTheWall());
        service.execute(ProfileActivity.SHOW_PROFILE_ENDPOINT_URL);
    }

    private void sendFriendRequest(){
        SendFriendRequestHttpAsyncTask sendFriendRequest = new SendFriendRequestHttpAsyncTask(this.tabOwnerActivity, this,
                SEND_FRIEND_REQUEST_SERVICE_ID, this.userOwnerOfTheWall.getId());
        sendFriendRequest.execute(SEND_FRIENDSHIP_REQUEST_ENDPOINT_URL);
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
        String picturePathUploading = cursor.getString(columnIndex);
        cursor.close();

        // Enviamos la imagen al servidor (si no supera un limite máximo)
        Bitmap bm = BitmapFactory.decodeFile(picturePathUploading);
        if(bm.getByteCount() <= this.PROFILE_PICTURE_MAX_SIZE) {
            UploadPictureHttpAsyncTask service = new UploadPictureHttpAsyncTask(this.tabOwnerActivity, this, UPLOAD_IMAGE_SERVICE_ID, picturePathUploading);
            service.execute(UPLOAD_IMAGE_SERVICE_ENDPOINT_URL);
        }
        else{
            String message = "La imagen supera los " + (this.PROFILE_PICTURE_MAX_SIZE / 1024 + 1) + "Kb. Suba una imagen mas pequeña.";
            Toast toast = Toast.makeText(this.tabOwnerActivity.getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}

