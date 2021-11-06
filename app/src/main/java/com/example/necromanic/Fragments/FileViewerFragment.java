package com.example.necromanic.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.necromanic.Adapters.FileViewerAdapter;
import com.example.necromanic.Database.DBHelper;
import com.example.necromanic.Models.RecordingItem;
import com.example.necromanic.R;

import java.util.ArrayList;

public class FileViewerFragment extends Fragment {

    RecyclerView recyclerView;
    DBHelper dbHelper;
    private FileViewerAdapter fileViewerAdapter;
    ArrayList<RecordingItem> arrayListAudios;
    Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(getContext());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);



        arrayListAudios = dbHelper.getAllAudios();
        if(arrayListAudios == null){
            Toast.makeText(getContext(),"No Audio files",Toast.LENGTH_LONG).show();
        }
        else{
            fileViewerAdapter = new FileViewerAdapter(getActivity(), arrayListAudios, llm);
            recyclerView.setAdapter(fileViewerAdapter);
        }



    }



}
