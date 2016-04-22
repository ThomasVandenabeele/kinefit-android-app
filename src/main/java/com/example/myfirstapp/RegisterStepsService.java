package com.example.myfirstapp;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thomas on 22/04/16.
 */
public class RegisterStepsService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single login url
    private static final String url_steps = "http://thomasvandenabeele.no-ip.org/KineFit/register_steps.php";

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
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Stappen sensor niet beschikbaar!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Post waarden in DB !
        int aantal = 2;
        RegisterSteps steps = new RegisterSteps();
        steps.steps = aantal;
        steps.execute();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    /**
     * Background Async Task to check login
     * */
    class RegisterSteps extends AsyncTask<String, String, String> {

        int steps;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Checking login in background thread
         * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            //runOnUiThread(new Runnable() {
            // Check for success tag
            int success;
            // Building Parameters

            Time now = new Time();
            now.setToNow();

            ContentValues parameters = new ContentValues();
            parameters.put("username", "TVDA");
            parameters.put("number_of_steps", steps);
            parameters.put("start_time", now.toString());
            parameters.put("end_time", now.toString());

            // getting product details by making HTTP request
            // Note that product details url will use GET request
            JSONObject json = jsonParser.makeHttpRequest(
                    url_steps, "POST", parameters);

            // check your log for json response
            Log.d("Register Steps: ", json.toString());
/*
            try {
                //int successt = json.getInt(TAG_SUCCESS);

               // if (successt == 1) {
                    // successfully updated
                    //Intent i = getIntent();
                    // send result code 100 to notify about product update
                    //setResult(100, i);
                    //i = new Intent(getApplicationContext(), DashboardActivity.class);
                    //startActivity(i);
                    //message = "";
                    //finish();
                //} else {
                    // failed to update product
                    //message = "Login Failed";
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            //pDialog.dismiss();
            //loginMessage.setText(message);
            //Intent i = new Intent(getApplicationContext(), MainActivity.class);
            //startActivity(i);
        }
    }

}
