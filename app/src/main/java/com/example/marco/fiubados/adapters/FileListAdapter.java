package com.example.marco.fiubados.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marco.fiubados.R;
import com.example.marco.fiubados.model.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter de archivos.
 * Muestra el nombre del archivo y como subtitulo el nombre de la persona que lo subió.
 * Si un archivo corresponde a un video de Youtube, lo indicará con un ícono.
 *
 * Created by ezequiel on 31/05/15.
 */
public class FileListAdapter extends BaseAdapter {

    private List<File> mFiles = new ArrayList<>();
    private LayoutInflater mInflater;


    public FileListAdapter(Context context, List<File> files) {
        mInflater = LayoutInflater.from(context);
        mFiles = files;
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileItemView fileItemView;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_file_adapter, null);

            fileItemView = new FileItemView();
            fileItemView.fileName = (TextView) convertView.findViewById(R.id.file_name_text_view);
            fileItemView.uploaderName = (TextView) convertView.findViewById(R.id.uploader_name_text_view);
            fileItemView.fileType = (ImageView) convertView.findViewById(R.id.file_type_image_view);

            convertView.setTag(fileItemView);
        } else {
            fileItemView = (FileItemView) convertView.getTag();
        }

        File file = (File) getItem(position);

        fileItemView.fileName.setText(file.getName());
        fileItemView.uploaderName.setText("Subido por " + file.getUploaderFullName());

        if (isYoutubeLink(file.getUrl())) {
            fileItemView.fileType.setImageResource(R.drawable.ic_action_youtube_holo_light);
        }

        return convertView;
    }

    private boolean isYoutubeLink(String url) {
        final String YOUTUBE = "youtube.com";
        final String YOUTUBE_SHORT = "youtu.be";
        return url.contains(YOUTUBE) || url.contains(YOUTUBE_SHORT);
    }

    class FileItemView {
        public TextView fileName;
        public TextView uploaderName;
        public ImageView fileType;
    }
}
