package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

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

    public DownloadPictureHttpAsyncTask(String pictureUrl, Activity callingActivity, CallbackScreen callbackScreen, int serviceId){
        this.pictureUrl = pictureUrl;
        this.callbackScreen = callbackScreen;
        this.serviceId = serviceId;
        this.callingActivity = callingActivity;
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
                Drawable d = Drawable.createFromStream(is, this.pictureUrl);
                this.drawables = new ArrayList<Drawable>();
                this.drawables.add(d);
                // Forzamos a que la parte de vista del codigo se muestre en el thread principal
                this.callingActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackScreen.onServiceCallback(drawables, serviceId);
                    }
                });
            }
        } catch (Exception e) {
            // La imagen no se pudo obtener
        }
    }
}
