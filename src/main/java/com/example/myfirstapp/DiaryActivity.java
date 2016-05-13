package com.example.myfirstapp;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Thomas on 28/04/16.
 */
public class DiaryActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    private TextView currentDate;
    private TextView newLogBtn;
    private CalendarView calendar;
    private TextView noLogTV;
    private ListView listLogs;
    private String currentDateSel;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<Logging> logList = new ArrayList<>();

    // url to get all products list
    private static String url_get_logs_on_date = "http://thomasvandenabeele.no-ip.org/KineFit/get_logs_on_date.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LOGS = "logs";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";

    // products JSONArray
    JSONArray loggings = null;


    SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("nl_NL"));
    SimpleDateFormat sdf_sql = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);
        currentDate = (TextView) findViewById(R.id.currentDate);
        newLogBtn = (TextView) findViewById(R.id.newLogBtn);
        noLogTV = (TextView) findViewById(R.id.noLogTV);

        calendar = (CalendarView) findViewById(R.id.calendar);
        currentDate.setText(sdf.format(new Date(calendar.getDate())));



        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //Toast.makeText(getApplicationContext(), ""+dayOfMonth, Toast.LENGTH_SHORT).show();// TODO Auto-generated method stub
                logList.clear();
                currentDateSel = sdf_sql.format(new Date(view.getDate()));
                currentDate.setText(sdf.format(new Date(view.getDate())));
                new LoadLoggings().execute();
            }
        });

        newLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = getIntent();
                // send result code 100 to notify about product update
                setResult(100, i);
                i = new Intent(getApplicationContext(), NewLogging.class);
                startActivity(i);

            }
        });


        listLogs = (ListView) findViewById(R.id.listLogs);


        listLogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }

        });


        currentDateSel = sdf_sql.format(new Date());
        currentDate.setText(sdf.format(new Date()));
        new LoadLoggings().execute();
    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadLoggings extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(DiaryActivity.this);
            pDialog.setMessage("Loading loggings. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            ContentValues parameters = new ContentValues();
            parameters.put("username", "TVDA");
            parameters.put("date", currentDateSel);
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_logs_on_date, "GET", parameters);

            // Check your log cat for JSON reponse
            //Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    if(!json.getString("message").equals("No logs found")){
                        loggings = json.getJSONArray(TAG_LOGS);

                        // looping through All Products
                        for (int i = 0; i < loggings.length(); i++) {
                            JSONObject c = loggings.getJSONObject(i);

                            // Storing each json item in variable

                            Logging l = new Logging(
                                                c.getInt("id"),
                                                c.getString("description"),
                                                Time.valueOf(c.getString("time")),
                                                java.sql.Date.valueOf(c.getString("date")),
                                                c.getInt("amount"),
                                                c.getString("unit"),
                                                c.getInt("sScore"),
                                                c.getInt("pScore"));

                            // adding HashList to ArrayList
                            logList.add(l);
                        }
                    }

                } else {
                    // no products found
                    // Launch Add New product Activity
                    /*Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
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

            if(logList.size()>0){
                noLogTV.setVisibility(View.GONE);
                listLogs.setVisibility(View.VISIBLE);

                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */

                        LoggingsAdapter ad = new LoggingsAdapter(DiaryActivity.this, R.layout.logs_list_item, logList);


                        //ArrayAdapter<Logging> adapter = new ArrayAdapter<Logging>(DiaryActivity.this,
                                //R.layout.logs_list_item, R.id.pid, logList);

                        listLogs.setAdapter(ad);

                    }
                });

            }
            else{
                noLogTV.setVisibility(View.VISIBLE);
                listLogs.setVisibility(View.GONE);
            }
            // updating UI from Background Thread


        }

    }






}
