package com.example.necromanic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.necromanic.Utils.PreferencesManager;
import com.jaredrummler.materialspinner.MaterialSpinner;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

public class SettingActivity extends AppCompatActivity {

    MaterialSpinner emfUnitSpinner;

    SwitchCompat evpDisableFilterSwitch;

    SeekBarCompat accRangeSeekBar;
    TextView accRangeTxt;

    SwitchCompat accNoGraitySwitch;

    SwitchCompat testSensorsCheckSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        emfUnitSpinner = findViewById(R.id.emf_unit_spinner);
        evpDisableFilterSwitch = findViewById(R.id.evp_filter_switch);
        accRangeSeekBar = findViewById(R.id.acc_range_seek_bar);
        accRangeTxt = findViewById(R.id.acc_text_value);
        accNoGraitySwitch = findViewById(R.id.acc_no_gravity);
        testSensorsCheckSwitch = findViewById(R.id.test_sensors_switch);

        setUpEmfSpinner();

        setUpEvpFilterSwich();

        setUpAccRangeSeekBar();

        setUpAccNoGravitySwitch();

        setUpTestSensors();


    }


    public void setUpEmfSpinner(){

        String emfUnits[] = getResources().getStringArray(R.array.spinnerItems);
        emfUnitSpinner.setItems(emfUnits[0], emfUnits[1]);

        emfUnitSpinner.setSelectedIndex(PreferencesManager.getIntPrefVal(this, Constants.EMF_UNIT));

        emfUnitSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                PreferencesManager.setIntPrefVal(SettingActivity.this, Constants.EMF_UNIT, position);
            }
        });


    }

    public void setUpEvpFilterSwich(){

        evpDisableFilterSwitch.setChecked(PreferencesManager.getBooleanPrefVal(this, Constants.EVP_FILTER_SWITCH_VALUE));

        evpDisableFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setBooleanPrefVal(SettingActivity.this, Constants.EVP_FILTER_SWITCH_VALUE, isChecked);
            }
        });
    }

    private void setUpAccRangeSeekBar() {
        int range = PreferencesManager.getIntPrefVal(this, Constants.ACC_DRAWING_RANGE);

        accRangeTxt.setText(String.valueOf(range) + " " +Constants.ACC_UNIT);
        accRangeSeekBar.setProgress(range);

        accRangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 10;
                progress = progress * 10;
                accRangeTxt.setText(String.valueOf(progress) + " " + Constants.ACC_UNIT);
                accRangeSeekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PreferencesManager.setIntPrefVal(SettingActivity.this, Constants.ACC_DRAWING_RANGE, accRangeSeekBar.getProgress());
            }
        });

    }

    private void setUpAccNoGravitySwitch() {
        accNoGraitySwitch.setChecked(PreferencesManager.getBooleanPrefVal(this, Constants.ACC_NO_GRAVITY));

        accNoGraitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setBooleanPrefVal(SettingActivity.this, Constants.ACC_NO_GRAVITY, isChecked);
            }
        });
    }


    private void setUpTestSensors() {
        testSensorsCheckSwitch.setChecked(!PreferencesManager.getBooleanPrefVal(this, Constants.FIRST_TIME));

        testSensorsCheckSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setBooleanPrefVal(SettingActivity.this, Constants.FIRST_TIME, !isChecked);

                Intent splashIntent = new Intent(SettingActivity.this, SplashActivity.class);
                startActivity(splashIntent);
            }
        });
    }




}