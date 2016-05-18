package com.KineFit.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.KineFit.app.R;
import com.KineFit.app.adapters.TasksAdapter;
import com.KineFit.app.model.Logging;
import com.KineFit.app.model.Step;
import com.KineFit.app.model.Task;
import com.KineFit.app.model.enums.TaskStatus;
import com.KineFit.app.services.JSONParser;
import com.KineFit.app.services.SessionManager;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Thomas on 17/04/16.
 */
public class StepActivity extends BaseActivity {

    private ProgressDialog pDialog;
    private GraphView stapGrafiek;
    private TextView selectedWeekSteps;
    private TextView totalSteps;
    private TextView stepWeekCount;
    private TextView stepDayCount;
    private Button weekTerug;
    private Button weekVerder;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // url to get all products list
    private static String url_get_steps_between_dates = "http://thomasvandenabeele.no-ip.org/KineFit/get_steps_between_datetimes.php";
    private static String url_get_step_info = "http://thomasvandenabeele.no-ip.org/KineFit/get_step_info.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STEPS = "steps";
    private static final String TAG_ID = "id";
    private static final String TAG_AANTALSTAPPEN = "number_of_steps";
    private static final String TAG_STARTTIJD = "start_time";
    private static final String TAG_EINDTIJD = "end_time";
    private static final String TAG_TOTAALSTAPPEN = "totaalStappen";
    private static final String TAG_STAPPENDEZEWEEK = "stappenDezeWeek";
    private static final String TAG_STAPPENVANDAAG = "stappenVandaag";

    private int weekNummer;
    private int jaar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step);

        totalSteps = (TextView) findViewById(R.id.totalSteps);
        stepWeekCount = (TextView) findViewById(R.id.stepWeekCount);
        stepDayCount = (TextView) findViewById(R.id.stepDayCount);
        selectedWeekSteps = (TextView) findViewById(R.id.selectedWeekSteps);

        stapGrafiek = (GraphView) findViewById(R.id.graphSteps);
        stapGrafiek.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(StepActivity.this));
        stapGrafiek.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        stapGrafiek.getViewport().setScrollable(true);
        stapGrafiek.getViewport().setScalable(true);

        final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance();
        stapGrafiek.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");

                    String date = formatDate.format(new Date((long) value));
                    String time = formatTime.format(new Date((long) value));

                    return "\n" + time + "\n" + date;
                }
                return super.formatLabel(value, isValueX); // let graphview generate Y-axis label for us
            }
        });

        weekNummer = new GregorianCalendar().get(Calendar.WEEK_OF_YEAR);
        jaar = new GregorianCalendar().get(Calendar.YEAR);

        updateGrafiek();

        ContentValues para = new ContentValues();
        para.put("username", session.getUsername());
        new GetStepInfo().execute(para);


        weekTerug = (Button) findViewById(R.id.weekTerugBtn);
        weekVerder = (Button) findViewById(R.id.weekVerderBtn);

        weekTerug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekVerder.setEnabled(true);
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.WEEK_OF_YEAR, weekNummer);
                calendar.set(Calendar.YEAR, jaar);
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                weekNummer = calendar.get(Calendar.WEEK_OF_YEAR);
                jaar = calendar.get(Calendar.YEAR);

                updateGrafiek();
            }
        });

        weekVerder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.WEEK_OF_YEAR, weekNummer);
                calendar.set(Calendar.YEAR, jaar);
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                weekNummer = calendar.get(Calendar.WEEK_OF_YEAR);
                jaar = calendar.get(Calendar.YEAR);

                calendar = Calendar.getInstance();
                if(weekNummer==calendar.get(Calendar.WEEK_OF_YEAR) && jaar==calendar.get(Calendar.YEAR)){
                    weekVerder.setEnabled(false);
                }
                else{
                    weekVerder.setEnabled(true);
                }

                updateGrafiek();
            }
        });

    }

    public void updateGrafiek(){
        ContentValues cv = new ContentValues();
        cv.put("username", session.getUsername());
        cv.put("startdate", getStartVanWeek(weekNummer, jaar));
        cv.put("enddate", getEindeVanWeek(weekNummer, jaar));

        new LoadAllSteps().execute(cv);
    }

    public String getStartVanWeek(int week, int jaar){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, jaar);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date start = calendar.getTime();
        return formatter.format(start);
    }

    public String getEindeVanWeek(int week, int jaar){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, jaar);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        calendar.add(Calendar.DATE, 7);
        java.util.Date einde = calendar.getTime();
        return formatter.format(einde);
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class GetStepInfo extends AsyncTask<ContentValues, String, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(ContentValues... params) {

            JSONObject json = jParser.makeHttpRequest(url_get_step_info, "GET", params[0]);
            Log.d("Steps info: ", json.toString());

            try {
                if (json.getInt(TAG_SUCCESS) == 1) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(JSONObject json) {
            // updating UI from Background Thread
            try {
                if(json != null){
                    totalSteps.setText(Integer.toString(json.getInt(TAG_TOTAALSTAPPEN)));
                    stepWeekCount.setText(Integer.toString(json.getInt(TAG_STAPPENDEZEWEEK)));
                    stepDayCount.setText(Integer.toString(json.getInt(TAG_STAPPENVANDAAG)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * Background Async Task to Load all steps by making HTTP Request
     * */
    class LoadAllSteps extends AsyncTask<ContentValues, String, ArrayList<Step>> {

        private String startDatum;
        private String eindDatum;
        private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StepActivity.this);
            pDialog.setMessage("Grafiek laden, gelieve te wachten...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All steps from url
         * */
        protected ArrayList<Step> doInBackground(ContentValues... params) {

            ContentValues parameters = params[0];
            startDatum = parameters.getAsString("startdate");
            eindDatum = parameters.getAsString("enddate");

            JSONObject json = jParser.makeHttpRequest(url_get_steps_between_dates, "GET", parameters);

            Log.d("All Steps: ", json.toString());

            ArrayList<Step> stepList = new ArrayList<>();

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    JSONArray stappenJson = json.getJSONArray(TAG_STEPS);

                    // looping through All Products
                    for (int i = 0; i < stappenJson.length(); i++) {
                        JSONObject c = stappenJson.getJSONObject(i);
                        Step s = new Step(  c.getInt(TAG_ID),
                                            c.getInt(TAG_AANTALSTAPPEN),
                                            new java.sql.Date(formatter.parse(c.getString(TAG_STARTTIJD)).getTime()),
                                            new java.sql.Date(formatter.parse(c.getString(TAG_EINDTIJD)).getTime()));
                        stepList.add(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return stepList;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(ArrayList<Step> steps) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            int maxAantalStappen=0;
            DataPoint[] waarden = new DataPoint[steps.size()];

            for (int i=0; i<steps.size(); i++) {
                Step s = steps.get(i);

                if(s.getAantalStappen() > maxAantalStappen) maxAantalStappen= s.getAantalStappen();

                DataPoint dp = new DataPoint(s.getGemDateTime(), s.getAantalStappen());
                waarden[i] = dp;
            }

            Date start = new Date(0, 0, 0), eind = new Date(0, 0, 0);

            if(steps.size()>0) {
                try {
                    start = new java.sql.Date(formatter.parse(startDatum).getTime());
                    eind = new java.sql.Date(formatter.parse(eindDatum).getTime());

                    stapGrafiek.getViewport().setMinX(start.getTime());
                    stapGrafiek.getViewport().setMaxX(eind.getTime());
                    stapGrafiek.getViewport().setXAxisBoundsManual(true);

                    stapGrafiek.getViewport().setMinY((int) 0);
                    stapGrafiek.getViewport().setMaxY((int) maxAantalStappen+5);
                    stapGrafiek.getViewport().setYAxisBoundsManual(true);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            stapGrafiek.removeAllSeries();
            if(steps.size()>0){
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(waarden);
                stapGrafiek.addSeries(series);
                series.setColor(Color.rgb(241, 104, 54));
            }

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            selectedWeekSteps.setText("Week = " + weekNummer + " (" + format.format(start) + " - " + format.format(eind) + ")");
        }

    }

}
