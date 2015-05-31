package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Marco on 06/05/2015.
 *
 * HTTP AsyncTask para subir imágenes.
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
            imageJsonObject.put("filename", filepath);
            imageJsonObject.put("content", getPictureContentParsedForRequest());
            imageJsonObject.put("content_type", getFileMimeType());
            JSONObject mainJsonObject = new JSONObject();
            mainJsonObject.put("userToken", ContextManager.getInstance().getUserToken());
            mainJsonObject.put("image", imageJsonObject);
            setResquestPostData(mainJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPictureContentParsedForRequest() {
        // Codificamos la data de la imagen en base64 para enviarla al servidor
        File file = new File(filepath);
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(filepath);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedfile;
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

    // url = file path or whatever suitable URL you want.
    public String getFileMimeType() {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filepath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
