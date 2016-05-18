package com.KineFit.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KineFit.app.R;
import com.KineFit.app.services.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thomas on 18/05/16.
 */
public class RegisterActivity extends Activity {

    private EditText voornaam;
    private EditText naam;
    private EditText email;
    private EditText gebruikersnaam;
    private EditText wachtwoord;
    private EditText bevWachtwoord;
    private Button registreer;
    private TextView registerMessage;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single login url
    private static final String url_register = "http://thomasvandenabeele.no-ip.org/KineFit/register_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_VOORNAAM = "firstname";
    private static final String TAG_NAAM = "name";
    private static final String TAG_WACHTWOORD = "password";
    private static final String TAG_GEBRUIKERSNAAM = "username";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.registreer);

        registerMessage = (TextView) findViewById(R.id.registerMessage);
        voornaam = (EditText) findViewById(R.id.reg_voornaam);
        naam = (EditText) findViewById(R.id.reg_naam);
        email = (EditText) findViewById(R.id.reg_email);
        gebruikersnaam = (EditText) findViewById(R.id.reg_gebruikersnaam);
        wachtwoord = (EditText) findViewById(R.id.reg_wachtwoord);
        bevWachtwoord = (EditText) findViewById(R.id.reg_bev_wachtwoord);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    email.setBackgroundColor(Color.rgb(205,85,85));
                } else {
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) email.setBackgroundColor(Color.rgb(0, 177, 106));
                    else email.setBackgroundColor(Color.rgb(205,85,85));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bevWachtwoord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = wachtwoord.getText().toString();
                if((s.toString()).equals(wachtwoord.getText().toString())){
                    bevWachtwoord.setBackgroundColor(Color.rgb(0, 177, 106));
                    wachtwoord.setBackgroundColor(Color.rgb(0, 177, 106));
                }
                else{
                    bevWachtwoord.setBackgroundColor(Color.rgb(205,85,85));
                    wachtwoord.setBackgroundColor(Color.rgb(205,85,85));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        registreer = (Button) findViewById(R.id.btnRegister);

        registreer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                boolean fout = false;
                if(isEmpty(voornaam)) {
                    voornaam.setError("Je voornaam is verplicht!");
                    fout = true;
                }
                if(isEmpty(naam)) {
                    naam.setError("Je naam is verplicht!");
                    fout = true;
                }
                if(isEmpty(email) | !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    email.setError("Geef een geldig emailadres!");
                    fout = true;
                }
                if(isEmpty(gebruikersnaam)){
                    gebruikersnaam.setError("Je gebruikersnaam is verplicht!");
                    fout = true;
                }
                if(isEmpty(wachtwoord)){
                    wachtwoord.setError("Geef een geldig wachtwoord!");
                    if(isEmpty(bevWachtwoord)) bevWachtwoord.setError("Vul eerst een wachtwoord in!");
                    fout = true;
                }
                if(!bevWachtwoord.getText().toString().equals(wachtwoord.getText().toString())){
                    wachtwoord.setError("Het wachtwoord komt niet overeen!");
                    bevWachtwoord.setError("Het wachtwoord komt niet overeen!");
                    fout = true;
                }

                if(!fout){
                    ContentValues parameters = new ContentValues();
                    parameters.put(TAG_NAAM, naam.getText().toString());
                    parameters.put(TAG_VOORNAAM, voornaam.getText().toString());
                    parameters.put(TAG_EMAIL, email.getText().toString());
                    parameters.put(TAG_GEBRUIKERSNAAM, gebruikersnaam.getText().toString());
                    parameters.put(TAG_WACHTWOORD, bevWachtwoord.getText().toString());

                    new RegisterUser().execute(parameters);

                }
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    class RegisterUser extends AsyncTask<ContentValues, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Account registreren, even geduld aub...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Checking login in background thread
         * */
        protected String doInBackground(ContentValues... params) {
            JSONObject json = jsonParser.makeHttpRequest(url_register, "POST", params[0]);
            Log.d("Register: ", json.toString());

            try {
                if (json.getInt(TAG_SUCCESS) == 1) {
                    finish();
                } else {
                    // Login failed
                    return "Login Failed. Try again.";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String message) {
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Registratie gelukt! U kan nu inloggen.", Toast.LENGTH_LONG).show();
            registerMessage.setText(message);
        }
    }

}
