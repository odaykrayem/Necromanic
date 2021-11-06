package com.example.necromanic.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.necromanic.R;
import com.example.necromanic.Services.RecordingService;
import com.github.sshadkany.CircleButton;

import java.io.File;

public class RecordFragment extends Fragment {

    Chronometer chronometer;
    TextView recordingStatusTxt;
    CircleButton btnRecord;
    ImageView recordBtnIcon;


    private boolean mStartRecording = true;

    File folder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);
        chronometer = recordView.findViewById(R.id.chronometer);
        recordingStatusTxt = recordView.findViewById(R.id.recording_status_txt);
        btnRecord = recordView.findViewById(R.id.btn_record);
        recordBtnIcon = recordView.findViewById(R.id.record_btn_icon);

        btnRecord.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                recordAudio();
            }
        });

        return recordView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void recordAudio() {
        onRecord(mStartRecording);
        mStartRecording = !mStartRecording;
    }

    private void onRecord(boolean start) {
        final int RECORD_AUDIO = 0;
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }

        Intent  intent = new Intent(getActivity(), RecordingService.class);
        folder = new File(this.getContext().getExternalFilesDir(null) + "/MySoundRec" );

        if (start) {
            recordBtnIcon.setImageResource(R.drawable.ic_medis_stop);
            Toast.makeText(getContext(), "Recording Started", Toast.LENGTH_SHORT).show();
         //   File folder = new File(getContext().getExternalFilesDir(null) + "/MySoundRec");
            // folder = new File(getExternalFilesDir(null) + "/MySoundRec");

       //     Toast.makeText(this.getContext(), getExternalFilesDir(null),Toast.LENGTH_LONG).show();
            if (!folder.exists()) {
                folder.mkdir();
            }
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            //starting Activity
            getActivity().startService(intent);
            //Keeping Window ON
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            recordingStatusTxt.setText("Recording....");

        } else {
            recordBtnIcon.setImageResource(R.drawable.ic_mic_white);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            recordingStatusTxt.setText("Tap the button to start recording....");
        //    Toast.makeText(this.getContext(),Environment.getExternalStorageDirectory() + "/MySoundRec" ,Toast.LENGTH_LONG).show();
            getActivity().stopService(intent);

        }
    }
}

