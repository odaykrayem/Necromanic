package com.example.necromanic.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.necromanic.Database.DBHelper;
import com.example.necromanic.Models.RecordingItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingService extends Service {
    MediaRecorder mediaRecorder;
    long mStartingTimeMillis = 0;
    long mElapsedMillis = 0;

    File file;
    String fileName;
    DBHelper dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    private void startRecording() {
        //Time Stamp
        Long tslong = System.currentTimeMillis() / 1000;
        String ts = tslong.toString();

        fileName = "_audio" + ts.toString();

        //    file = new File(getExternalFilesDir("mm") + "/MySoundRec" + fileName + ".mp3");
        file = new File(getExternalFilesDir(null) + "/MySoundRec" , fileName + ".mp3");


        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioEncodingBitRate(32);
        mediaRecorder.setAudioChannels(1);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecording() {

        try {
            mediaRecorder.stop();
        } catch(RuntimeException stopException) {
            // handle cleanup here
        }
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
      //A  mediaRecorder.reset();
        mediaRecorder.release();
       //A mediaRecorder =null;
        Toast.makeText(getApplicationContext(),getExternalFilesDir(null) + "/MySoundRec" + fileName + ".mp3",Toast.LENGTH_LONG).show();
        RecordingItem recordingItem = new RecordingItem(fileName, file.getAbsolutePath(), mElapsedMillis, System.currentTimeMillis());
        dbHelper.addRecording(recordingItem);
    }

        @Override
    public void onDestroy() {
        if(mediaRecorder != null){
            stopRecording();
        }

        super.onDestroy();
    }

    public String getFileName() {
        return fileName;
    }
    public String getCurrentTimeStamp() {
        SimpleDateFormat dateFormat;
        String currentTimeStamp;

        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            return currentTimeStamp;

        } catch (Exception e) {
            e.printStackTrace();


        }
        return null;
    }

}
