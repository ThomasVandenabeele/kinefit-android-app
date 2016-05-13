package com.example.myfirstapp;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thomas on 30/04/16.
 */
public class TaskActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<Task> tasksList;

    // url to get all products list
    private static String url_all_tasks = "http://thomasvandenabeele.no-ip.org/KineFit/get_all_tasks.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "message";
    private static final String TAG_CREATE_DATETIME = "created_at";
    private static final String TAG_STATUS = "status";

    // products JSONArray
    JSONArray tasks = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        tasksList = new ArrayList<Task>();

        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.task_pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        DashboardActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TaskActivity.this);
            pDialog.setMessage("Loading tasks. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            ContentValues params = new ContentValues();
            params.put("username", "TVDA");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_tasks, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    tasks = json.getJSONArray(TAG_TASKS);

                    // looping through All Products
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        SimpleDateFormat srcDfT = new SimpleDateFormat("HH:mm:ss");
                        DateFormat srcDfD = new SimpleDateFormat("yyyy-MM-dd");
                        Date creation_date = new java.sql.Date(srcDfD.parse(c.getString(TAG_CREATE_DATETIME)).getTime());
                        Task t = new Task(c.getInt(TAG_PID),
                                            c.getString(TAG_NAME),
                                            creation_date,
                                            TaskStatus.valueOf(c.getString(TAG_STATUS)));

                        // adding HashList to ArrayList
                        tasksList.add(t);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            DashboardActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                    TasksAdapter ta = new TasksAdapter(TaskActivity.this, R.layout.task_list_item, tasksList);
                    setListAdapter(ta);

                }
            });

        }

    }
}



