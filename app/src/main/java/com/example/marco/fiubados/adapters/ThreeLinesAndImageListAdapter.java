package com.example.marco.fiubados.adapters;

import android.app.Activity;
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
import com.example.marco.fiubados.model.MultipleField;

import java.util.List;


public class ThreeLinesAndImageListAdapter extends BaseAdapter implements CallbackScreen {

    private static final int DOWNLOAD_IMAGE_SERVICE_ID = 0;

    private List<MultipleField> fields;
    private Activity activity;
    private ListView listView;

    public ThreeLinesAndImageListAdapter(List<MultipleField> fields, Activity activity, ListView listView) {
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
        TextView dateTextView = (TextView) row.findViewById(R.id.date);
        ImageView imageView = (ImageView)row.findViewById(R.id.image);

        titleTextView.setText(this.fields.get(position).getField("Titulo").getValue());
        messageTextView.setText(this.fields.get(position).getField("Mensaje").getValue());
        dateTextView.setText(this.fields.get(position).getField("Date").getValue());
        String imageUrl = this.fields.get(position).getField("ImageURL").getValue();

        DownloadPictureHttpAsyncTask service = new DownloadPictureHttpAsyncTask(imageUrl, imageView, this.activity, this, DOWNLOAD_IMAGE_SERVICE_ID);
        service.execute();
        return (row);    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {
        if(serviceId == DOWNLOAD_IMAGE_SERVICE_ID){
            // Le enviamos un evento de touch al list view para que actualice la vista y se muestre la imagen conseguida
            this.listView.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }
    }
}
