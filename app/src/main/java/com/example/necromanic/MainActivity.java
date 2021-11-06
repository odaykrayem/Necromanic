package com.example.necromanic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.sshadkany.RectButton;

public class MainActivity extends AppCompatActivity {


    RectButton emfBtn;
    RectButton evpBtn;
    RectButton accBtn;
    RectButton setBtn;

    TextView mAboutUs;

    final int AUDIO_PERMISSION_CODE = 101;
    private static final int REQUEST_WRITE_PERMISSION = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Window window = this.getWindow();

        emfBtn = findViewById(R.id.emf_btn);
        evpBtn = findViewById(R.id.evp_btn);
        accBtn = findViewById(R.id.acc_btn);
        setBtn = findViewById(R.id.settings_btn);
        mAboutUs = findViewById(R.id.about_us_txt);

        //lighting up the status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.neumorphism_light));


        mAboutUs.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToAboutUs();
            }
        });
        emfBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToEMF();
            }
        });
        evpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToEVP();
            }
        });
        accBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToACC();
            }
        });
        setBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                gotoSettings();
            }
        });

    }

    private void goToAboutUs() {
        Intent aboutUsActiityIntent = new Intent(MainActivity.this, AboutUs.class);
        startActivity(aboutUsActiityIntent);
    }

    public void goToEMF() {
        Intent emfActivityIntent = new Intent(MainActivity.this, EMFActivity.class);
        startActivity(emfActivityIntent);
    }

    public void goToEVP() {
            Intent evpActivityIntent = new Intent(MainActivity.this, RecorderActivity.class);
            startActivity(evpActivityIntent);
    }
    public void goToACC() {
        Intent evpActivityIntent = new Intent(MainActivity.this, AccelerometerActivity.class);
        startActivity(evpActivityIntent);
    }

    public void gotoSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingsIntent);
    }

    private boolean checkAudioPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
    }


}