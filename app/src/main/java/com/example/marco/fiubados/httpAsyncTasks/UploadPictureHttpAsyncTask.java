package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Marco on 06/05/2015.
 */
public class UploadPictureHttpAsyncTask extends HttpAsyncTask {

    private String filepath;

    public UploadPictureHttpAsyncTask(Activity callingActivity, CallbackScreen callbackScreen, int serviceId, String filepath) {
        super(callingActivity, callbackScreen, serviceId);
        this.filepath = filepath;
    }

    @Override
    protected void configureRequestFields() {
        // Armamos la data personalizada para el envio por POST
        try {
            JSONObject imageJsonObject = new JSONObject();
            imageJsonObject.put("filename", this.filepath);
            imageJsonObject.put("content", this.getPictureContentParsedForRequest());
            imageJsonObject.put("content_type", "image/jpeg");  // REVIEW
            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("userToken", ContextManager.getInstance().getUserToken());
            mainJsonObject.put("image", imageJsonObject);
            this.setResquestPostData(mainJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPictureContentParsedForRequest() {
        // Codificamos la data de la imagen en base64 para enviarla al servidor
        Bitmap bm = BitmapFactory.decodeFile(this.filepath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    @Override
    protected void configureResponseFields() {
        this.addResponseField("result");
    }

    @Override
    protected void onResponseArrival() {
        if(this.responseCode == HttpURLConnection.HTTP_OK || this.responseCode == HttpURLConnection.HTTP_CREATED){
            if(!this.getResponseField("result").equals("ok")){
                this.dialog.setMessage("Ocurrió un error durante la carga de imagen");
                this.dialog.show();
            }
            else {
                // Le indicamos al servicio que nos llamo que terminó la carga con exito
                this.callbackScreen.onServiceCallback(new ArrayList<String>(), this.serviceId);
            }
        }
        else if(this.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            this.dialog.setMessage("Usted no esta autorizado para realizar esto");
            this.dialog.show();
        }
        else{
            this.dialog.setMessage("Error en la conexión con el servidor");
            this.dialog.show();
        }
    }

    @Override
    protected String getRequestMethod() {
        return POST_REQUEST_TYPE;
    }
}
