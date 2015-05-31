package com.example.marco.fiubados.TabScreens;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.NotificationsActivity;
import com.example.marco.fiubados.ProfileActivity;
import com.example.marco.fiubados.TabbedActivity;
import com.example.marco.fiubados.adapters.TwoLinesAndImageListAdapter;
import com.example.marco.fiubados.httpAsyncTasks.DownloadPictureHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.FriendshipResponseHttpAsynkTask;
import com.example.marco.fiubados.httpAsyncTasks.GetComentariesHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.SendFriendRequestHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.UploadPictureHttpAsyncTask;
import com.example.marco.fiubados.model.Comentary;
import com.example.marco.fiubados.model.Field;
import com.example.marco.fiubados.model.TripleField;
import com.example.marco.fiubados.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 08/04/2015.
 *
 * Maneja la lógica interna del tab de muro
 */
public class WallTabScreen implements CallbackScreen {

    private static final String SEND_FRIENDSHIP_REQUEST_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends/send_friendship_request";
    private static final String UPLOAD_IMAGE_SERVICE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users/upload_profile_picture";

    private static final String GET_COMENTARIES_SERVICE_ENDPOINT = "http://www.mocky.io/v2/556a3abee66746240a151b49";
    private static final String SEND_COMENTARY_SERVICE_ENDPOINT = "http://www.mocky.io/v2/556a3abee66746240a151b49";

    private static final String DEFAULT_PROFILE_PICTURE = "ic_action_picture_holo_light";
    private static final int PROFILE_PICTURE_MAX_SIZE = 524228;

    private final int SEND_FRIEND_REQUEST_SERVICE_ID = 0;
    private final int RESPOND_FRIEND_REQUEST_SERVICE_ID = 1;
    public static final int RESULT_LOAD_IMAGE = 2;
    private static final int UPLOAD_IMAGE_SERVICE_ID = 3;
    private static final int RELOAD_PROFILE_SERVICE_ID = 4;
    private static final int GET_PROFILE_PICTURE_SERVICE_ID = 5;
    private static final int GET_COMENTARIES_SERVICE_ID = 6;

    private TabbedActivity tabOwnerActivity;
    private User userOwnerOfTheWall;
    private Button addFriendButton, confirmFriendRequestButton;
    private TextView wallTitleTextView;
    private TextView friendRequestSent;
    private ImageView profileImageView;
    private ListView wallCommentsListView;
    private EditText wallCommentEditText;
    private Button wallCommentSendButton;

    public WallTabScreen(TabbedActivity tabOwnerActivity, Button addFriendButton, Button confirmFriendRequestButton,
                         TextView wallTitleTextView, TextView friendRequestSent, ImageView profileImageView,
                         ListView wallCommentsListView, EditText wallCommentEditText,
                         Button wallCommentSendButton){
        this.tabOwnerActivity = tabOwnerActivity;
        this.wallTitleTextView = wallTitleTextView;
        this.friendRequestSent = friendRequestSent;
        this.addFriendButton = addFriendButton;
        this.confirmFriendRequestButton = confirmFriendRequestButton;
        this.profileImageView = profileImageView;
        this.wallCommentsListView = wallCommentsListView;
        this.wallCommentEditText = wallCommentEditText;
        this.wallCommentSendButton = wallCommentSendButton;

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

        wallCommentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hacer algo, je
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
        this.wallCommentsListView.setVisibility(View.GONE);
        this.wallCommentEditText.setVisibility(View.GONE);
        this.wallCommentSendButton.setVisibility(View.GONE);
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

            // Si es el propio muro o el de un amigo, se deja escribir en el muro
            if (this.userOwnerOfTheWall.equals(ContextManager.getInstance().getMyUser()) ||
                    this.userOwnerOfTheWall.getFriendshipStatus().equals(User.FRIENDSHIP_STATUS_FRIEND)){
                this.wallCommentEditText.setVisibility(View.VISIBLE);
                this.wallCommentSendButton.setVisibility(View.VISIBLE);
                this.wallCommentsListView.setVisibility(View.VISIBLE);
                this.findComments();
            }
        }

        // Si hay, vamos a buscar la imagen de perfil
        this.findProfilePicture();
    }

    private void findComments(){
        GetComentariesHttpAsyncTask comentariesService = new GetComentariesHttpAsyncTask(this.tabOwnerActivity, this, GET_COMENTARIES_SERVICE_ID, this.getUserOwnerOfTheWall().getId());
        comentariesService.execute(GET_COMENTARIES_SERVICE_ENDPOINT);
    }

    private void findProfilePicture(){
        // Temporalmente, cargamos una imagen de perfil por defecto de los assets
        int resId = this.tabOwnerActivity.getResources().getIdentifier(DEFAULT_PROFILE_PICTURE, "drawable", this.tabOwnerActivity.getPackageName());
        this.profileImageView.setImageResource(resId);

        // Traemos del servidor la imagen de perfil y la mostramos (si hay)
        String profilePictureUrl = this.getUserOwnerOfTheWall().getProfilePicture();
        DownloadPictureHttpAsyncTask pictureService = new DownloadPictureHttpAsyncTask(profilePictureUrl, this.tabOwnerActivity, this, GET_PROFILE_PICTURE_SERVICE_ID);
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
        else if(serviceId == RELOAD_PROFILE_SERVICE_ID){
            // Vamos a buscar a intenet nuestra imagen de perfil
            this.findProfilePicture();
        }
        else if(serviceId == GET_PROFILE_PICTURE_SERVICE_ID){
            // Hemos conseguido una imagen, vamos a presentarla
            this.presentProfilePicture(responseElements);
        }

        else if(serviceId == GET_COMENTARIES_SERVICE_ID){
            // Llenamos los comentarios que nos devuelve el servicio
            this.fillUIListWithComentaries(responseElements);
        }
    }

    private void fillUIListWithComentaries(List<Comentary> responseElements) {
        List<TripleField> finalListViewLines = new ArrayList<>();

        for (Comentary comentary : responseElements){
            String authorName = comentary.getAuthor().getFirstName() + " " + comentary.getAuthor().getLastName();
            finalListViewLines.add(new TripleField(new Field("Autor", authorName),
                    new Field("Mensaje", comentary.getMessage()), new Field("ImageURL", comentary.getImageUrl())));
        }

        this.wallCommentsListView.setAdapter(new TwoLinesAndImageListAdapter(finalListViewLines, this.tabOwnerActivity, this.wallCommentsListView));

        if(responseElements.isEmpty()){
            this.wallCommentsListView.setVisibility(View.GONE);
        }
        else{
            this.wallCommentsListView.setVisibility(View.VISIBLE);
        }

    }

    private void onProfileImageChanged() {
        // Recargamos el perfil para recargar la nueva imagen del usuario
        // REVIEW: Para esto podria evitarse traerse el perfil entero
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
        Cursor cursor = tabOwnerActivity.getContentResolver().query(selectedImage, filePathColumn,
                null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePathUploading = cursor.getString(columnIndex);
        cursor.close();

        // Enviamos la imagen al servidor (si no supera un limite máximo)
        File file = new File(picturePathUploading);
        long length = file.length();
        //Bitmap bm = BitmapFactory.decodeFile(picturePathUploading);
        if(length <= PROFILE_PICTURE_MAX_SIZE) {
            UploadPictureHttpAsyncTask service = new UploadPictureHttpAsyncTask(tabOwnerActivity,
                    this, UPLOAD_IMAGE_SERVICE_ID, picturePathUploading);
            service.execute(UPLOAD_IMAGE_SERVICE_ENDPOINT_URL);
        } else {
            String message = "La imagen supera los " + (PROFILE_PICTURE_MAX_SIZE / 1024 + 1) + "KB. Suba una imagen mas pequeña.";
            Toast toast = Toast.makeText(tabOwnerActivity.getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
