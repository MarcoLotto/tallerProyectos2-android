package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.TabScreens.CallbackScreen;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 06/05/2015.
 */
public class DownloadPictureHttpAsyncTask {

    private String pictureUrl;
    private CallbackScreen callbackScreen;
    private int serviceId;
    private Activity callingActivity;
    private List<Drawable> drawables;
    private ImageView imageView;

    public DownloadPictureHttpAsyncTask(String pictureUrl, Activity callingActivity, CallbackScreen callbackScreen, int serviceId){
        this.pictureUrl = pictureUrl;
        this.callbackScreen = callbackScreen;
        this.serviceId = serviceId;
        this.callingActivity = callingActivity;
    }

    public DownloadPictureHttpAsyncTask(String pictureUrl, ImageView imageView, Activity callingActivity, CallbackScreen callbackScreen, int serviceId){
        this.pictureUrl = pictureUrl;
        this.callbackScreen = callbackScreen;
        this.serviceId = serviceId;
        this.callingActivity = callingActivity;
        this.imageView = imageView;
    }

    public void execute() {
        new Thread(new Runnable() {
            public void run() {
                processInternalExecution();
            }
        }).start();
    }

    private void processInternalExecution(){
        try {
            if(!this.pictureUrl.isEmpty()) {
                InputStream is = (InputStream) new URL(this.pictureUrl).getContent();
                final Drawable d = Drawable.createFromStream(is, this.pictureUrl);
                this.drawables = new ArrayList<Drawable>();
                this.drawables.add(d);

                // Forzamos a que la parte de vista del codigo se muestre en el thread principal
                this.callingActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(imageView != null && d != null){
                            imageView.setImageDrawable(d);
                        }
                        callbackScreen.onServiceCallback(drawables, serviceId);
                    }
                });
            }
        } catch (Exception e) {
            // La imagen no se pudo obtener
        }
    }
}
