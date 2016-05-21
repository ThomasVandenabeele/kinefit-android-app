package com.KineFit.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
 * Activity voor het Registratie scherm.
 * Op deze activity kan er een nieuwe gebruiker aangemaakt worden.
 *
 * Created by Thomas on 18/05/16.
 * @author Thomas Vandenabeele
 */
public class RegisterActivity extends Activity {

    //region DATAMEMBERS

    /** EditText voor voornaam */
    private EditText voornaam;

    /** EditText voor naam */
    private EditText naam;

    /** EditText voor email */
    private EditText email;

    /** EditText voor gebruikersnaam */
    private EditText gebruikersnaam;

    /** EditText voor wachtwoord */
    private EditText wachtwoord;

    /** EditText voor bevesting wachtwoord */
    private EditText bevWachtwoord;

    /** Button voor registratie */
    private Button registreer;

    /** TextView voor registratie bericht */
    private TextView registreerBericht;

    /** ProgressDialog voor UI */
    private ProgressDialog pDialog;

    //endregion

    //region REST: TAGS & URL

    /** JSONParser voor de REST client aan te spreken */
    JSONParser jsonParser = new JSONParser();

    /** URL voor registratie gebruiker */
    private static final String url_registreer = "http://thomasvandenabeele.no-ip.org/KineFit/register_user.php";

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCESS = "success";

    /** Tag voor email */
    private static final String TAG_EMAIL = "email";

    /** Tag voor voornaam */
    private static final String TAG_VOORNAAM = "firstname";

    /** Tag voor naam */
    private static final String TAG_NAAM = "name";

    /** Tag voor wachtwoord */
    private static final String TAG_WACHTWOORD = "password";

    /** Tag voor gebruikersnaam */
    private static final String TAG_GEBRUIKERSNAAM = "username";

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registreer);

        //region UI componenten toekennen
        registreerBericht = (TextView) findViewById(R.id.registerMessage);
        voornaam = (EditText) findViewById(R.id.reg_voornaam);
        naam = (EditText) findViewById(R.id.reg_naam);
        email = (EditText) findViewById(R.id.reg_email);
        gebruikersnaam = (EditText) findViewById(R.id.reg_gebruikersnaam);
        wachtwoord = (EditText) findViewById(R.id.reg_wachtwoord);
        bevWachtwoord = (EditText) findViewById(R.id.reg_bev_wachtwoord);
        registreer = (Button) findViewById(R.id.btnRegister);
        //endregion

        // TextChangedListener voor email TextView
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validatie aan de hand van kleurweergave
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

        // TextChangedListener voor bevestiging wachtwoord en wachtwoord TextViews
        bevWachtwoord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validatie aan de hand van kleurweergave
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

        // OnClickListener voor registratie knop
        registreer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Eerst validatie, lege velden checken, ...

                boolean fout = false;
                if(isLeeg(voornaam)) {
                    voornaam.setError("Je voornaam is verplicht!");
                    fout = true;
                }
                if(isLeeg(naam)) {
                    naam.setError("Je naam is verplicht!");
                    fout = true;
                }
                if(isLeeg(email) | !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    email.setError("Geef een geldig emailadres!");
                    fout = true;
                }
                if(isLeeg(gebruikersnaam)){
                    gebruikersnaam.setError("Je gebruikersnaam is verplicht!");
                    fout = true;
                }
                if(isLeeg(wachtwoord)){
                    wachtwoord.setError("Geef een geldig wachtwoord!");
                    if(isLeeg(bevWachtwoord)) bevWachtwoord.setError("Vul eerst een wachtwoord in!");
                    fout = true;
                }
                if(!bevWachtwoord.getText().toString().equals(wachtwoord.getText().toString())){
                    wachtwoord.setError("Het wachtwoord komt niet overeen!");
                    bevWachtwoord.setError("Het wachtwoord komt niet overeen!");
                    fout = true;
                }

                if(!fout){

                    // Gebruiker registreren
                    ContentValues parameters = new ContentValues();
                    parameters.put(TAG_NAAM, naam.getText().toString());
                    parameters.put(TAG_VOORNAAM, voornaam.getText().toString());
                    parameters.put(TAG_EMAIL, email.getText().toString());
                    parameters.put(TAG_GEBRUIKERSNAAM, gebruikersnaam.getText().toString());
                    parameters.put(TAG_WACHTWOORD, bevWachtwoord.getText().toString());

                    new RegistreerGebruiker().execute(parameters);

                }
            }
        });
    }

    /**
     * Methode om te kijken of een EditText leeg is.
     * @param editText EditText voor check
     * @return true bij leeg, anders false
     */
    private boolean isLeeg(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Async Taak op achtergrond om gebruiker te registreren
     * Via HTTP Request naar REST client.
     * */
    class RegistreerGebruiker extends AsyncTask<ContentValues, String, String> {

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
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
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return bericht voor registratie
         */
        protected String doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jsonParser.makeHttpRequest(url_registreer, "POST", params[0]);
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

            // Geen fout, dan leeg bericht teruggeven
            return "";
        }

        /**
         * Methode voor na uitvoering taak. Update de UI.
         * **/
        protected void onPostExecute(String message) {
            pDialog.dismiss();
            registreerBericht.setText(message);
            if(message.equals("")) Toast.makeText(getApplicationContext(), "Registratie gelukt! U kan nu inloggen.", Toast.LENGTH_LONG).show();
        }

    }

}
