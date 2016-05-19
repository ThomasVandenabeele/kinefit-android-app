package com.KineFit.app.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by Thomas on 22/04/16.
 */
public class RegisterStepsService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single login url
    private static final String url_steps = "http://thomasvandenabeele.no-ip.org/KineFit/register_steps.php";

    private long startTime;

    // Must create a default constructor
    public RegisterStepsService() {
        // Used to name the worker thread, important only for debugging.
        super("step-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Sensor listener registered!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Stappen sensor niet beschikbaar!", Toast.LENGTH_LONG).show();
        }



        startTime = System.currentTimeMillis();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);

    }

    private int stepCounter = 0;
    private int counterSteps = 0;
    private boolean post=false;
    private ContentValues parameters;

    @Override
    public void onSensorChanged(SensorEvent event) {

        int firstSteps = counterSteps;
        if (counterSteps < 1) { // initial value
            counterSteps = (int) event.values[0];
        }

        int count = ((int) event.values[0] - counterSteps)-stepCounter;

        // Calculate steps taken based on first counter value received.
        stepCounter = (int) event.values[0] - counterSteps;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date datumke = new java.util.Date();
        String timestamp = df.format(datumke.getTime()
                + (event.timestamp - System.nanoTime()) / 1000000L);

        System.out.println("Interval: "+ count);
        Toast.makeText(this, "Aantal: "+stepCounter, Toast.LENGTH_SHORT).show();

        if(count==0) post = false;
        else post = true;

        if(post){
            String starttime = df.format(startTime);
            String endtime = timestamp;

            parameters = new ContentValues();
            parameters.put("username", "TVDA");
            parameters.put("number_of_steps", count);
            parameters.put("start_time", starttime);
            parameters.put("end_time", endtime);

            new RegisterSteps().execute();

            startTime = System.currentTimeMillis();
        }


        if(firstSteps < 1){
            post = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    /**
     * Background Async Taak to register steps
     * */
    class RegisterSteps extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            JSONObject json = jsonParser.makeHttpRequest(url_steps, "POST", parameters);
            Log.d("Register Steps: ", json.toString());
            return null;

        }

    }

}
