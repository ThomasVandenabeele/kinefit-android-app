package com.KineFit.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.services.JSONParser;
import com.KineFit.app.services.SessionManager;

import org.json.JSONObject;

/**
 * Created by Thomas on 22/04/16.
 */
public class DashboardActivity extends BaseActivity {

    Button btnSteps;
    Button btnTasks;
    Button btnLoggings;
    TextView welcome;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // url to get all products list
    private static String url_count_new_tasks = "http://thomasvandenabeele.no-ip.org/KineFit/get_count_new_tasks.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TASKSCOUNT = "tasksCount";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        welcome = (TextView) findViewById(R.id.Welcome);
        welcome.setText("Welkom, " + session.getVoornaam() +"!");
        btnSteps = (Button) findViewById(R.id.btnSteps);
        btnTasks = (Button) findViewById(R.id.btnTasks);
        btnLoggings = (Button) findViewById(R.id.btnLoggings);

        btnSteps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), StepActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);

            }
        });

        btnTasks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), TaskActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);

            }

        });

        btnLoggings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DiaryActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);

            }

        });
        if(session.isLoggedIn()) new CountNewTasks().execute();

    }



    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class CountNewTasks extends AsyncTask<String, String, String> {

        private int aantal;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            ContentValues params = new ContentValues();
            params.put("username", session.getUsername());

            JSONObject json = jParser.makeHttpRequest(url_count_new_tasks, "GET", params);
            Log.d("New tasks: ", json.toString());

            try {
                if (json.getInt(TAG_SUCCESS) == 1) {
                    aantal = json.getInt(TAG_TASKSCOUNT);
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
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if(aantal != 0) btnTasks.setText(btnTasks.getText() + " (" + aantal + ")");
                }
            });

        }

    }

}
