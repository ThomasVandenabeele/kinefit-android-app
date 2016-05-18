package com.KineFit.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.services.JSONParser;
import com.KineFit.app.services.SessionManager;

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
    TextView registreer;
    CheckBox herinner;

    // Session Manager Class
    private SessionManager session;

    public String message = "";
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single login url
    private static final String url_login = "http://thomasvandenabeele.no-ip.org/KineFit/login.php"; //"http://michielarits.ddns.net/login.php"; //"http://thomasvandenabeele.no-ip.org/KineFit/login.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_VOORNAAM = "firstname";
    private static final String TAG_NAAM = "name";
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

        // Session Manager
        session = new SessionManager(getApplicationContext());

        btnDoLogin = (Button) findViewById(R.id.btnDoLogin);
        txtPassword = (EditText) findViewById(R.id.inputPassword);
        txtUsername = (EditText) findViewById(R.id.inputUsername);
        loginMessage = (TextView) findViewById(R.id.loginMessage);
        herinner = (CheckBox) findViewById(R.id.herinnerCB);
        registreer = (TextView) findViewById(R.id.registreerBtn);

        registreer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        // save button click event
        btnDoLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                    loginMessage.setText("");
                    new DoLogin(herinner.isChecked()).execute();
                }else{
                    if(username.trim().length()==0) txtUsername.setError("Please insert an username.");
                    if(password.trim().length()==0) txtPassword.setError("Please insert a password.");
                }
            }

        });

        if(session.getRememberedUser() != null){
            herinner.setChecked(true);
            txtUsername.setText(session.getRememberedUser());
        }
        else{
            herinner.setChecked(false);
        }

    }

    /**
     * Background Async Task to check login
     * */
    class DoLogin extends AsyncTask<String, String, String> {

        private boolean herinner;

        public DoLogin (boolean herinner){
            this.herinner = herinner;
        }

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
            ContentValues parameters = new ContentValues();
            parameters.put("username", username);
            parameters.put("password", password);

            JSONObject json = jsonParser.makeHttpRequest(url_login, "POST", parameters);
            Log.d("Login", json.toString());

            try {
                if (json.getInt(TAG_SUCCESS) == 1) {

                    session.createLoginSession( username,
                                                json.getString(TAG_NAAM),
                                                json.getString(TAG_VOORNAAM),
                                                json.getString(TAG_EMAIL),
                                                herinner);

                    // Door naar dashboard.
                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(i);
                    message = "";
                    finish();

                } else {
                    // Login failed
                    message = "Login Failed. Try again.";
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
            pDialog.dismiss();
            loginMessage.setText(message);
        }
    }

}
