package com.KineFit.app.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.KineFit.app.R;
import com.KineFit.app.adapters.TasksAdapter;
import com.KineFit.app.model.Task;
import com.KineFit.app.model.enums.TaskStatus;
import com.KineFit.app.services.JSONParser;
import com.KineFit.app.services.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Thomas on 30/04/16.
 */
public class TaskActivity extends BaseActivity {

    // Progress Dialog
    private ProgressDialog pDialog;
    private LinearLayout filterTasksMenu;
    private ToggleButton toggleButtonFilter;
    private Switch switchClosedTasks;
    private Switch switchFailedTasks;
    private boolean closedTasks;
    private boolean failedTasks;
    private ListView lv;
    private TextView noTasksTV;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<Task> tasksList;

    // url to get all products list
    private static String url_all_tasks = "http://thomasvandenabeele.no-ip.org/KineFit/get_all_tasks.php";
    private static String url_set_status_task = "http://thomasvandenabeele.no-ip.org/KineFit/update_status_task.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "message";
    private static final String TAG_CREATE_DATETIME = "created_at";
    private static final String TAG_STATUS = "status";

    // products JSONArray
    JSONArray tasks = null;

    ContentValues parameters = new ContentValues();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        noTasksTV = (TextView) findViewById(R.id.noTasksTV);
        filterTasksMenu = (LinearLayout)findViewById(R.id.filterMenuTasks);
        toggleButtonFilter = (ToggleButton)findViewById(R.id.toggleButtonFilter);
        toggleButtonFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    filterTasksMenu.setVisibility(View.VISIBLE);
                }
                else{
                    filterTasksMenu.setVisibility(View.GONE);
                }
            }
        });
        switchClosedTasks = (Switch)findViewById(R.id.switchClosedTasks);
        switchClosedTasks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                closedTasks = isChecked;
                new LoadAllTasks().execute();
            }
        });
        switchFailedTasks = (Switch)findViewById(R.id.switchFailedTasks);
        switchFailedTasks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                failedTasks = isChecked;
                new LoadAllTasks().execute();
            }
        });
        closedTasks = switchClosedTasks.isChecked();
        failedTasks = switchFailedTasks.isChecked();

        tasksList = new ArrayList<Task>();

        // Loading products in Background Thread
        new LoadAllTasks().execute();

        // Get listview
        lv = (ListView)findViewById(R.id.listTasks);

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                Task t = tasksList.get(position);
                if(t.getStatus().equals(TaskStatus.OPEN)||t.getStatus().equals(TaskStatus.NEW)){
                    final String pid = ((TextView) view.findViewById(R.id.task_pid)).getText()
                            .toString();
                    parameters.clear();
                    parameters.put("id", pid);
                    new AlertDialog.Builder(TaskActivity.this)
                            .setTitle("Complete Task")
                            .setMessage("Did you succeeded this task?")
                            .setPositiveButton("Done",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            parameters.put("name", TaskStatus.DONE.toString());
                                            new UpdateTaskStatus().execute();
                                            dialog.cancel();

                                        }
                                    })
                            .setNeutralButton("Failed",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            parameters.put("name", TaskStatus.FAILED.toString());
                                            new UpdateTaskStatus().execute();
                                            dialog.cancel();

                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

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
    class LoadAllTasks extends AsyncTask<String, String, String> {

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
            params.put("username", session.getUsername());
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
                    tasksList.clear();
                    // looping through All Products
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);
                        boolean add = true;

                        SimpleDateFormat srcDfT = new SimpleDateFormat("HH:mm:ss");
                        DateFormat srcDfD = new SimpleDateFormat("yyyy-MM-dd");
                        Date creation_date = new java.sql.Date(srcDfD.parse(c.getString(TAG_CREATE_DATETIME)).getTime());
                        Task t = new Task(c.getInt(TAG_PID),
                                            c.getString(TAG_NAME),
                                            creation_date,
                                            TaskStatus.valueOf(c.getString(TAG_STATUS)));
                        if(!closedTasks && t.getStatus().equals(TaskStatus.DONE)) add = false;
                        if(!failedTasks && t.getStatus().equals(TaskStatus.FAILED)) add = false;
                        // adding HashList to ArrayList
                        if(add) tasksList.add(t);
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
            if(tasksList.size()>0) {
                noTasksTV.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                // updating UI from Background Thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        TasksAdapter ta = new TasksAdapter(TaskActivity.this, R.layout.task_list_item, tasksList);
                        lv.setAdapter(ta);

                    }
                });
            }
            else {
                noTasksTV.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
            }


        }

    }

    /**
     * Background Async Task to Create new product
     * */
    class UpdateTaskStatus extends AsyncTask<String, String, String> {
        private int success;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TaskActivity.this);
            pDialog.setMessage("Updating Task ...");
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
            JSONObject json = jParser.makeHttpRequest(url_set_status_task, "POST", parameters);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);
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
            if (success == 1) {
                new LoadAllTasks().execute();
            }

        }

    }
}



