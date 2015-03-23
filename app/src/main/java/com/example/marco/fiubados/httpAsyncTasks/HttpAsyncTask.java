package com.example.marco.fiubados.httpAsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public abstract class HttpAsyncTask extends AsyncTask <String, Integer, JSONObject> {

    private ProgressDialog dialog;
    private Map<String, String> requestFields, responseFields;
    protected Activity callingActivity;

    public HttpAsyncTask(Activity callingActivity) {
        this.dialog = new ProgressDialog(callingActivity);
        this.callingActivity = callingActivity;
        this.requestFields = new HashMap<String, String>();
        this.responseFields = new HashMap<String, String>();
    }

    /**
     * Guardar dentro de requestFields los campos que se enviaran en el request
     */
    protected abstract void configureRequestFields();

    /**
     * Guardar dentro de responseFields los campos que se esperan en el response
     */
    protected abstract void configureResponseFields();

    /**
     * Se utiliza para una vez que llega la respuesta del servidor, se procese los campos
     */
    protected abstract void onResponseArrival();

    public void onPreExecute() {
        this.configureRequestFields();
        this.configureResponseFields();
        this.dialog.setMessage("Esperando Respuesta ...");
        this.dialog.show();
    }

    @Override
    public JSONObject doInBackground(String... params) {
        String url = params[0];
        // TODO: Appendear cada uno de los atributos del request!
        HttpURLConnection urlToRequest = null;
        try {
            urlToRequest = (HttpURLConnection) (new URL(url)).openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return handleError();
        } catch (IOException e) {
            e.printStackTrace();
            return handleError();
        }
        urlToRequest.setReadTimeout(10000);
        urlToRequest.setConnectTimeout(10000);
        int responseCode = 0;
        try {
            responseCode = urlToRequest.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return handleError();
        }
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            return handleError();
        }
        InputStream in = null;
        try {
            in = urlToRequest.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = new Scanner(in).useDelimiter("\\A").next();
        JSONObject json = null;
        try {
            json = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private JSONObject handleError() {
        this.dialog.dismiss();
        Toast.makeText(this.callingActivity.getApplicationContext(), "Error de Conexión", Toast.LENGTH_LONG).show();
        return null;
    }

    public void onPostExecute(JSONObject result) {
        this.dialog.dismiss();
        if (result != null) {
            // Conseguimos los valores de todos los campos que pedimos
            Iterator<String> it = this.responseFields.keySet().iterator();
            while (it.hasNext()) {
                String fieldName = it.next();
                try {
                    this.responseFields.put(fieldName, result.getString(fieldName));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // Mandamos a que los hijos procesen el resultado como quieran
        this.onResponseArrival();
    }

    public void addResponseField(String name) {
        this.responseFields.put(name, "");
    }

    public String getResponseField(String name) {
        return this.responseFields.get(name);
    }

    public void addRequestField(String name, String value) {
        this.requestFields.put(name, value);
    }
}
