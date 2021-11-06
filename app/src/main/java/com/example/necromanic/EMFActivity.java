package com.example.necromanic;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.necromanic.Utils.PreferencesManager;
import com.github.sshadkany.shapes.RoundRectView;


public class EMFActivity extends AppCompatActivity implements SensorEventListener {


    View emf_bars[];
    TextView mEMFTextView;
    private int colors[];

    // Sensors & SensorManager
    private Sensor magnetometer;
    private SensorManager mSensorManager;

    // Storage for Sensor readings
    private float[] mGeomagnetic = null;
    private float magAbsVal;
    String finalValueString;

    MediaPlayer m1 = null;
    int warningSound;

    int neuWhiteColor;

    RoundRectView container;

    int unitType;
    String unitString;
    boolean gauss = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_m_f);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        warningSound = R.raw.warning;

        emf_bars = new View[8];
        emf_bars[0] = findViewById(R.id.emf_green);
        emf_bars[1] = findViewById(R.id.emf_green_light);
        emf_bars[2] = findViewById(R.id.emf_yellow_light);
        emf_bars[3] = findViewById(R.id.emf_yellow);
        emf_bars[4] = findViewById(R.id.emf_orange_light);
        emf_bars[5] = findViewById(R.id.emf_orange);
        emf_bars[6] = findViewById(R.id.emf_red_light);
        emf_bars[7] = findViewById(R.id.emf_red);

        mEMFTextView = findViewById(R.id.emf_tv_value);

        colors = getResources().getIntArray(R.array.emf_allert);

        container = findViewById(R.id.emf_bars_container);

        // Get a reference to the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get a reference to the magnetometer
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(null != magnetometer){
            mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        // Exit unless sensor are available
        if (null == magnetometer) {
            Toast.makeText(this, "there is problem with the EMF reader or its missing in your device !", Toast.LENGTH_LONG).show();
            finish();
        }

        turnLightsOff();

        neuWhiteColor = getResources().getColor(R.color.neumorphism_light);


        unitType = PreferencesManager.getIntPrefVal(this, Constants.EMF_UNIT);
        switch (unitType){
            case 0 : {
                unitString = " µT";
                gauss = false;
                break;
            } case 1 : {
                unitString = " gauss";
                gauss = true;
                break;
            }
        }


    }

    public void turnLightsOff() {
        for (View s : emf_bars) {
            s.setVisibility(View.INVISIBLE);
//            onStartAnimation((RoundRectView) s.getParent());
        }
//        onStartAnimation(container);
    }

    public void setUpLights(int numOfBars) {
        for (int i = 0; i < numOfBars; i++) {
            emf_bars[i].setVisibility(View.VISIBLE);
        }
        for (int i = numOfBars; i < emf_bars.length; i++) {
            emf_bars[i].setVisibility(View.INVISIBLE);
        }

    }

    public int shouldBeActive(float emfValue) {
        int numOfBars = 0;
        if (emfValue > 140)
            return 8;
        if (emfValue > 120)
            return 7;
        if (emfValue > 100)
            return 6;
        if (emfValue > 80)
            return 5;
        if (emfValue > 60)
            return 4;
        if (emfValue > 40)
            return 3;
        if (emfValue > 20)
            return 2;
        if (emfValue > 0)
            return 1;

        return numOfBars;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register for sensor updates
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister all sensors
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Acquire magnetometer event data
        float finalValue = 0;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            mGeomagnetic = new float[3];
            System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);
            magAbsVal = (float) Math.sqrt(mGeomagnetic[0] * mGeomagnetic[0] +
                    mGeomagnetic[1] * mGeomagnetic[1] +
                    mGeomagnetic[2] * mGeomagnetic[2]);
            finalValue = magAbsVal;

            if(gauss){
                finalValue = finalValue / 100;
                finalValueString = String.format("%.2f", finalValue);
            }else{
                finalValueString = String.valueOf((int) finalValue);
            }



        }


        //finally !!!
        setUpLights(shouldBeActive(magAbsVal));
        playSound(magAbsVal);

        mEMFTextView.setText(finalValueString + unitString);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void playSound(float emf) {
        if (emf > 130) {
            // This play function
            // takes five parameter
            // leftVolume, rightVolume,
            // priority, loop and rate.
            if(m1 == null) {
                m1 = MediaPlayer.create(EMFActivity.this, warningSound);
                m1.setVolume(1.0f, 1.0f);
                m1.setLooping(true);
                m1.start();
            }else
                m1.start();
        }else{
            if(m1!= null)
                m1.pause();
        }

    }

//    public void onStartAnimation(final RoundRectView s){
//        int colorFrom = 0;
//        int colorTo = 25;
//        ValueAnimator colorAnimation = ValueAnimator.ofInt(colorFrom, colorTo);
//        colorAnimation.setDuration(00); // milliseconds
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator) {
//                s.setShadow_position_x((int)animator.getAnimatedValue());
//                s.setShadow_position_y((int)animator.getAnimatedValue());
//            }
//
//        });
//
//        colorAnimation.addListener(new AnimatorListenerAdapter() {
//            public void onAnimationEnd(Animator animation) {
//                mSensorManager.registerListener(EMFActivity.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
//            }
//        });
//
//        colorAnimation.start();
//    }

}
