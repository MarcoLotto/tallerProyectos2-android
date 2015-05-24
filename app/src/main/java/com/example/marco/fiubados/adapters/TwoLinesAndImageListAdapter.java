package com.example.marco.fiubados.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marco.fiubados.R;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.httpAsyncTasks.DownloadPictureHttpAsyncTask;
import com.example.marco.fiubados.model.TripleField;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Marco on 23/05/2015.
 */
public class TwoLinesAndImageListAdapter extends BaseAdapter implements CallbackScreen {

    private static final int DOWNLOAD_IMAGE_SERVICE_ID = 0;

    private List<TripleField> fields;
    private Activity activity;
    private ListView listView;

    public TwoLinesAndImageListAdapter(List<TripleField> fields, Activity activity, ListView listView) {
        this.fields = fields;
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return this.fields.size();
    }

    @Override
    public Object getItem(int i) {
        return this.fields.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = this.activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.layout_comment_adapter, parent, false);
        TextView titleTextView = (TextView) row.findViewById(R.id.title);
        TextView messageTextView = (TextView) row.findViewById(R.id.message);
        ImageView imageView = (ImageView)row.findViewById(R.id.image);
        titleTextView.setText(this.fields.get(position).getField1().getValue());
        messageTextView.setText(this.fields.get(position).getField2().getValue());
        String imageUrl = this.fields.get(position).getField3().getValue();
        DownloadPictureHttpAsyncTask service = new DownloadPictureHttpAsyncTask(imageUrl, imageView, this.activity, this, this.DOWNLOAD_IMAGE_SERVICE_ID);
        service.execute();
        return (row);
    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == this.DOWNLOAD_IMAGE_SERVICE_ID){
            // Le enviamos un evento de touch al list view para que actualice la vista y se muestre la imagen conseguida
            this.listView.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }
    }
}

