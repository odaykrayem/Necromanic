package com.example.necromanic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.necromanic.Utils.PreferencesManager;

import java.util.Random;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class SplashActivity extends AppCompatActivity {


    MaterialProgressBar emfProgress;
    MaterialProgressBar accProgress;
    MaterialProgressBar grProgress;

    ImageView splashEmfIcon;
    ImageView splachAccIcon;
    ImageView splashGIcon;

    // Sensors & SensorManager
    private Sensor magnetometer;
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a reference to the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get a reference to the magnetometer
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);



        boolean firstTime = PreferencesManager.getBooleanPrefVal(this, Constants.FIRST_TIME);

        if(!firstTime){
            animateSplash();
            setContentView(R.layout.activity_splash);

            emfProgress = findViewById(R.id.splash_emf_progress);
            accProgress = findViewById(R.id.splash_acc_progress);
            grProgress = findViewById(R.id.splash_gravity_progress);

            splashEmfIcon = findViewById(R.id.splash_emf_icon);
            splachAccIcon = findViewById(R.id.splash_acc_icon);
            splashGIcon = findViewById(R.id.splash_gravity_icon);



           boolean emfhere = animateEmfProgress(generate());

        }else{
            Intent toMAin = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(toMAin);  // Launch next activity
            finish();
        }

    }

    private void animateSplash() {
        PreferencesManager.setBooleanPrefVal(this, Constants.FIRST_TIME, true);


    }



    public boolean animateEmfProgress(int time){
        int from = 0;
        int to = 100;
        boolean returnValue;
        ValueAnimator progressAnimator = ValueAnimator.ofInt(from, to);
        progressAnimator.setDuration(time); // milliseconds
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                emfProgress.setProgress( (int) animator.getAnimatedValue());
            }

        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if(magnetometer == null){
                    splashEmfIcon.setBackground(getResources().getDrawable(R.drawable.not_found_24));
                }else{

                    splashEmfIcon.setBackground(getResources().getDrawable(R.drawable.done_24));
                }
                splashEmfIcon.setVisibility(View.VISIBLE);
                emfProgress.setVisibility(View.INVISIBLE);

                animateAccProgress(generate());
            }
        });

        progressAnimator.start();
        return (magnetometer != null);
    }

    public boolean animateAccProgress(int time){
        int from = 0;
        int to = 100;
        boolean returnValue;
        ValueAnimator progressAnimator = ValueAnimator.ofInt(from, to);
        progressAnimator.setDuration(time); // milliseconds
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                accProgress.setProgress( (int) animator.getAnimatedValue());
            }

        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if(mAccelerometer == null){
                    splachAccIcon.setBackground(getResources().getDrawable(R.drawable.not_found_24));
                }else{

                    splachAccIcon.setBackground(getResources().getDrawable(R.drawable.done_24));
                }
                splachAccIcon.setVisibility(View.VISIBLE);
                accProgress.setVisibility(View.INVISIBLE);
                animateGProgress(generate());
            }
        });

        progressAnimator.start();
        return (mAccelerometer != null);
    }

    public boolean animateGProgress(int time){
        int from = 0;
        int to = 100;
        boolean returnValue;
        ValueAnimator progressAnimator = ValueAnimator.ofInt(from, to);
        progressAnimator.setDuration(time); // milliseconds
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                grProgress.setProgress( (int) animator.getAnimatedValue());
            }

        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if(mGravity == null){
                    splashGIcon.setBackground(getResources().getDrawable(R.drawable.not_found_24));
                }else{

                    splashGIcon.setBackground(getResources().getDrawable(R.drawable.done_24));
                }
                splashGIcon.setVisibility(View.VISIBLE);
                grProgress.setVisibility(View.INVISIBLE);

                Handler h = new Handler();
                h.postDelayed(r, 1000);

            }
        });

        progressAnimator.start();
        return (mGravity != null);
    }

public int generate(){
    Random random = new Random();
    int randomNumber = random.nextInt(3-1) + 1;
    randomNumber *= 1000;
    return randomNumber;
}

    Runnable r = new Runnable() {
        @Override
        public void run(){
            Intent toMAin = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(toMAin);  // Launch next activity
            finish();
        }
    };



}