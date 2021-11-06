package com.example.necromanic;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.necromanic.Utils.PreferencesManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.Locale;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {


    private static final String TAG = "AccelerometerClass";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    boolean noGravity = false;

    CircleProgressBar accShow;
    CircleProgressBar accX;
    CircleProgressBar accY;
    CircleProgressBar accZ;
    int maximumProgressScale = 500;
    float maximumAxisScale = 50f;

    float maxReading = 10f;
    float minReading = 9.8f;
    float avgOfReadings = 0f;

    TextView accMax;
    TextView accMin;
    TextView accAvg;

    float accValue[];

    private LineChart mChart;
    private Thread thread;
    boolean plotData = false;

    AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        setDefaultLanguage(this, "en_US");
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        noGravity = PreferencesManager.getBooleanPrefVal(this, Constants.ACC_NO_GRAVITY);
        if(!noGravity){
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }else{
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }


        accValue = new float[3];


        maximumAxisScale = PreferencesManager.getAccRangeIntPrefVal(this, Constants.ACC_DRAWING_RANGE, 20);
        maximumProgressScale = (int) maximumAxisScale * 10;


        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        accShow = findViewById(R.id.line_progress);
        accX = findViewById(R.id.x_progress);
        accY = findViewById(R.id.y_progress);
        accZ = findViewById(R.id.z_progress);

        accMax = findViewById(R.id.acc_tv_max);
        accMin = findViewById(R.id.acc_tv_min);
        accAvg = findViewById(R.id.acc_tv_avg);

        mChart = (LineChart) findViewById(R.id.acc_chart);

        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawBorders(false);


        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // set an alternative background color
        mChart.setBackgroundColor(getResources().getColor(R.color.neumorphism_light));
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        // add empty data
        mChart.setData(data);


        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setDrawAxisLine(false);
        xl.setAvoidFirstLastClipping(false);
        xl.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMaximum(maximumAxisScale);
        leftAxis.setAxisMinimum(-1f);
        leftAxis.setCenterAxisLabels(true);
        mChart.moveViewTo(xl.mAxisMaximum, 0f, YAxis.AxisDependency.LEFT);
        leftAxis.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        Description d = new Description();
        d.setText("");
        mChart.setDescription(d);


        accMax.setText(String.valueOf(maxReading));
        accMin.setText(String.valueOf(minReading));
        accAvg.setText(String.valueOf(avgOfReadings));

        accShow.setProgressFormatter(new MyProgressFormatter());
        accX.setProgressFormatter(new XProgressFormatter());
        accY.setProgressFormatter(new YProgressFormatter());
        accZ.setProgressFormatter(new ZProgressFormatter());

        accShow.setMax(maximumProgressScale);
        accX.setMax(maximumProgressScale);
        accY.setMax(maximumProgressScale);
        accZ.setMax(maximumProgressScale);

        feedMultiple();


    }


    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(this);
        thread.interrupt();
        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        accValue = event.values;

        for(int i = 0; i < accValue.length; i++){
            if(accValue[i] < 0)
                accValue[i] = 0;
        }

        addEntry(event);

        float reading = calculateAerage(accValue);

        setupConstants(reading);

        setUpProgress(accValue);


    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), calculateAerage(event.values)), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);;
        set.setLineWidth(2f);
        set.setColor(getResources().getColor(R.color.design_concept));
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.3f);
        return set;
    }

    private void setupConstants(float reading) {

        if(reading > maxReading){
            maxReading = reading;
        }
        if(reading < minReading){
            minReading = reading;
        }
        avgOfReadings = ( maxReading + minReading ) / 2 ;

        accMax.setText(String.valueOf(maxReading));
        accMin.setText(String.valueOf(minReading));
        accAvg.setText(String.valueOf(avgOfReadings));
    }

    private void setUpProgress(float accValue[]) {
        accShow.setProgress((int)(calculateAerage(accValue) * 10));

        accX.setProgress((int) accValue[0] * 10);
        accY.setProgress((int) accValue[1] * 10);
        accZ.setProgress((int) accValue[2] * 10);
    }

    float calculateAerage(float accValue[])
    {
        double x2 = Math.pow(accValue[0], 2);
        double y2 = Math.pow(accValue[1], 2);
        double z2 = Math.pow(accValue[2], 2);
        return (float) Math.sqrt((x2 + y2 + z2));
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                }
            }
        });

        thread.start();
    }


    public static void setDefaultLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.createConfigurationContext(config);
    }

}