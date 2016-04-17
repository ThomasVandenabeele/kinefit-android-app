package com.example.myfirstapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thomas on 16/04/16.
 */
public class LoginActivity extends Activity {
    EditText txtUsername;
    EditText txtPassword;
    Button btnDoLogin;
    TextView loginMessage;

    public String message = "";
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single login url
    private static final String url_login = "http://thomasvandenabeele.no-ip.org/android_connect/login.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESCRIPTION = "description";

    public String username, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnDoLogin = (Button) findViewById(R.id.btnDoLogin);
        txtPassword = (EditText) findViewById(R.id.inputPassword);
        txtUsername = (EditText) findViewById(R.id.inputUsername);
        loginMessage = (TextView) findViewById(R.id.loginMessage);

        // getting product details from intent
        //Intent i = getIntent();

        // getting product id (pid) from intent
        //pid = i.getStringExtra(TAG_PID);

        // Getting complete product details in background thread
        //new GetProductDetails().execute();

        // save button click event
        btnDoLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                new DoLogin().execute();

            }
        });

    }

    /**
     * Background Async Task to check login
     * */
    class DoLogin extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Checking login. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
                        ContentValues parameters = new ContentValues();
                        parameters.put("username", username);
                        parameters.put("password", password);

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_login, "POST", parameters);

                        // check your log for json response
                        Log.d("Login", json.toString());

                        try {
                            int successt = json.getInt(TAG_SUCCESS);

                            if (successt == 1) {
                                // successfully updated
                                Intent i = getIntent();
                                // send result code 100 to notify about product update
                                setResult(100, i);
                                i = new Intent(getApplicationContext(), StepActivity.class);
                                startActivity(i);
                                message = "";
                                finish();
                            } else {
                                // failed to update product
                                message = "Login Failed";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
            loginMessage.setText(message);
            //Intent i = new Intent(getApplicationContext(), MainActivity.class);
            //startActivity(i);
        }
    }

}
