package com.softengi.mobcomp.softwareengi_mobile;

import com.softengi.mobcomp.softwareengi_mobile.Actions.StepAction;
import com.softengi.mobcomp.softwareengi_mobile.Utils.StepDetector;
import com.softengi.mobcomp.softwareengi_mobile.Utils.StepListener;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.LinkedList;


public class StepActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int planId = 0;
    private String planDescription = "";
    private TextView tvStep, tvPace, tvPlanDescription;
    private Button btnStartPause;
    private Button btnStop;

    private Chronometer mChronometer;

    private int numSteps = 0;
    private long lastPause = 0;
    private int pausedSteps = 0;

    private LinkedList<Integer> timestamps;
    String pace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_monitor);

        tvStep              = findViewById(R.id.tvStep);
        tvPace              = findViewById(R.id.tvPace);
        tvPlanDescription   = findViewById(R.id.tvPlanDescription);
        btnStartPause       = findViewById(R.id.btnStartPause);
        btnStop             = findViewById(R.id.btnStop);
        simpleStepDetector  = new StepDetector();
        sensorManager       = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel               = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // if the intent of this Activity contains a planId, we'll be using it
        // to attach to the new step database record. If it does not, we use the "Overall"
        // plan by using planId = 0.
        Intent intent = getIntent();
        if(intent != null) {
            planId = intent.getIntExtra("planId", 0);
            intent.getStringExtra("planDescription");
            tvPlanDescription.setText(planDescription);
        }

        mChronometer = findViewById(R.id.chrStopWatch);
        mChronometer.setCountDown(false);

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            @Override
            public void onChronometerTick(Chronometer chronometer)
            {
                calculatePace();
            }
        });

        simpleStepDetector.registerListener(this);
        btnStartPause.setSelected(false);

        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnStartPause.isSelected()) {
                    // pause
                    lastPause = SystemClock.elapsedRealtime();
                    mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    mChronometer.stop();

                    pausedSteps = numSteps;
                    sensorManager.unregisterListener(StepActivity.this);
                    btnStartPause.setBackground(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp,null));
                } else {
                    // resume
                    if(lastPause == 0) {
                        timestamps = new LinkedList<>();
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        pace = "0 " + R.string.pace;
                        tvPace.setText(pace);
                    } else {
                        mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    }
                    mChronometer.start();

                    numSteps = pausedSteps;
                    sensorManager.registerListener(StepActivity.this, accel,SensorManager.SENSOR_DELAY_FASTEST);
                    btnStartPause.setBackground(getResources().getDrawable(R.drawable.ic_pause_black_24dp,null));
                }

                btnStartPause.setSelected(!btnStartPause.isSelected());

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StepAction.postSteps(
                        getApplicationContext(),
                        numSteps,
                        planId
                );

                numSteps = 0;
                pausedSteps = 0;
                tvStep.setText("0");
                btnStartPause.setSelected(false);
                btnStartPause.setBackground(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp,null));
                sensorManager.unregisterListener(StepActivity.this);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        tvStep.setText(String.valueOf(numSteps));
        calculatePace();
    }

    private void calculatePace() {
        /*
        //pace change on every steps made
        if (timestamps.size() >= 5) {
            timestamps.removeFirst();
        }
        timestamps.addLast(lastPause = SystemClock.elapsedRealtime());

        pace = timestamps.size() / (timestamps.getLast() - timestamps.getFirst()) + " " + R.string.pace;
        tvPace.setText(pace);
        */

        //pace change on every seconds passed
        if (timestamps.size() >= 5) {
            timestamps.removeFirst();
        }
        timestamps.addLast(numSteps);
        pace = (timestamps.getLast() - timestamps.getFirst()) / timestamps.size() + " " + R.string.pace;
        tvPace.setText(pace);
    }
}
