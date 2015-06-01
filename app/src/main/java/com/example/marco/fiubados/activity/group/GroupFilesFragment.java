package com.example.marco.fiubados.activity.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marco.fiubados.R;
import com.example.marco.fiubados.adapters.FileListAdapter;
import com.example.marco.fiubados.model.File;
import com.example.marco.fiubados.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupFilesFragment extends Fragment {

    private List<File> mFiles = new ArrayList<>();
    private FileListAdapter mGroupFilesAdapter;

    public GroupFilesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_files, container, false);

        User user = new User("1", "Ezequiel", "Pérez Dittler");
        File youtubeFile = new File("El experto",
                                    "https://www.youtube.com/watch?v=BKorP55Aqvg",
                                    user);
        File pdfFile = new File("User Stories Applieds",
                                "http://www.mountaingoatsoftware.com/system/asset/file/259/User-Stories-Applied-Mike-Cohn.pdf",
                                user);
        mFiles.add(youtubeFile);
        mFiles.add(pdfFile);

        mGroupFilesAdapter = new FileListAdapter(getActivity(), mFiles);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.group_files_list_view);
        listView.setAdapter(mGroupFilesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                File file = (File) mGroupFilesAdapter.getItem(position);
                // Procesar archivo seleccionado
                Toast.makeText(getActivity(), "Se ha seleccionado " + file.getName(), Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}
