package com.example.marco.fiubados.httpAsyncTasks;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.example.marco.fiubados.ContextManager;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Marco on 06/05/2015.
 */
public class DownloadPictureHttpAsyncTask extends AsyncTask<String, Integer, Drawable> {

    private String pictureUrl;

    public DownloadPictureHttpAsyncTask(String pictureUrl){
        this.pictureUrl = pictureUrl;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        try {
            if(!this.pictureUrl.isEmpty()) {
                InputStream is = (InputStream) new URL(this.pictureUrl).getContent();
                return Drawable.createFromStream(is, this.pictureUrl);
            }
        } catch (Exception e) {
            // La imagen no se pudo obtener
        }
        return null;
    }
}
