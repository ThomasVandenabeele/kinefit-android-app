package com.example.myfirstapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Thomas on 28/04/16.
 */
public class NewLogging extends Activity {

    private Spinner type;
    private TextView val_unit;

    private SeekBar sb_pScore;
    private SeekBar sb_sScore;
    private TextView sScore;
    private TextView pScore;

    private Button btnCreateLog;
    private ProgressDialog pDialog;
    private Logs_Type sel_lt;
    private EditText et_amount;
    ContentValues parameters = new ContentValues();

    private static String url_get_all_logs_types = "http://thomasvandenabeele.no-ip.org/KineFit/get_all_logs_type.php";
    private static String url_create_log = "http://thomasvandenabeele.no-ip.org/KineFit/create_log.php";
    JSONParser jParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LOGS_TYPES = "logs_type";
    JSONArray logs_types = null;
    ArrayList<Logs_Type> logtypList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_logging);

        //TYPE SPINNER
        type = (Spinner) findViewById(R.id.sel_type);
        val_unit = (TextView) findViewById(R.id.val_unit);
        new LoadLogsType().execute();
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Logs_Type lt = (Logs_Type) parent.getItemAtPosition(position);
                String key = (String) lt.getUnit();
                val_unit.setText(key);
                sel_lt = lt;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //PAIN & SATTISFACTION SCORE
        sb_pScore = (SeekBar) findViewById(R.id.sb_pScore);
        sb_sScore = (SeekBar) findViewById(R.id.sb_sScore);
        sScore = (TextView) findViewById(R.id.val_sScore);
        pScore = (TextView) findViewById(R.id.val_pScore);


        sb_pScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int c = Color.BLACK;
                int score = progress;

                if(score >= 0 && score < 3) c = Color.GREEN;
                else if(score > 7 && score <= 10) c = Color.RED;

                pScore.setTextColor(c);
                pScore.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_sScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int c = Color.BLACK;
                int score = progress;

                if (score >= 0 && score < 3) c = Color.RED;
                else if(score > 7 && score <= 10) c = Color.GREEN;

                sScore.setTextColor(c);
                sScore.setText(String.valueOf(score));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        et_amount = (EditText) findViewById(R.id.et_amount);
        btnCreateLog = (Button) findViewById(R.id.btnCreateLog);
        btnCreateLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parameters.put("username", "TVDA");
                parameters.put("type_id", sel_lt.getId());
                parameters.put("amount", Integer.valueOf(et_amount.getText().toString()));
                parameters.put("sScore", Integer.valueOf(sScore.getText().toString()));
                parameters.put("pScore", Integer.valueOf(pScore.getText().toString()));

                new CreateNewLog().execute();
            }
        });

    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadLogsType extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            ContentValues parameters = new ContentValues();
            JSONObject json = jParser.makeHttpRequest(url_get_all_logs_types, "GET", parameters);

            // Check your log cat for JSON reponse
            //Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    logs_types = json.getJSONArray(TAG_LOGS_TYPES);
                    logtypList.clear();

                    for (int i = 0; i < logs_types.length(); i++) {
                        JSONObject c = logs_types.getJSONObject(i);
                        logtypList.add(new Logs_Type(c.getInt("id"), c.getString("description"), c.getString("unit")));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();

            if(logtypList.size()>0){

                runOnUiThread(new Runnable() {
                    public void run() {

                        ArrayAdapter<Logs_Type> spinnerAdapter = new ArrayAdapter<Logs_Type>(NewLogging.this, android.R.layout.simple_spinner_item, logtypList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        type.setAdapter(spinnerAdapter);

                    }
                });

            }



        }

    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewLog extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewLogging.this);
            pDialog.setMessage("Creating Log..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_create_log, "POST", parameters);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), DiaryActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
