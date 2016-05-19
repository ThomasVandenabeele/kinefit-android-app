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
import com.KineFit.app.services.SessieManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity voor de Login pagina.
 * Op deze activity kan de gebruiker inloggen in de app.
 *
 * Created by Thomas on 16/04/16.
 * @author Thomas Vandenabeele
 */
public class LoginActivity extends Activity {

    //region DATAMEMBERS

    /** EditText voor gebruikersnaam */
    private EditText txtGebruikersnaam;

    /** EditText voor wachtwoord */
    private EditText txtWachtwoord;

    /** Button voor login */
    private Button btnDoeLogin;

    /** TextView voor login bericht */
    private TextView tvLoginBericht;

    /** TextView voor registreer (knop) */
    private TextView tvRegistreer;

    /** CheckBox voor herinnerMij gebruiker */
    private CheckBox herinnerMij;

    /** ProgressDialog voor informatie op UI */
    private ProgressDialog pDialog;

    /** SessieManager voor gebruikerinfo */
    private SessieManager sessie;

    //endregion

    //region REST: TAGS & URL

    /** JSONParser voor de REST client aan te spreken */
    JSONParser jsonParser = new JSONParser();

    /** URL voor login te checken */
    private static final String url_login = "http://thomasvandenabeele.no-ip.org/KineFit/login.php";

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCESS = "success";

    /** Tag voor post gebruikersnaam */
    private static final String TAG_GEBRUIKERSNAAM = "username";

    /** Tag voor post wachtwoord */
    private static final String TAG_WACHTWOORD= "password";

    /** Tag voor return email */
    private static final String TAG_EMAIL = "email";

    /** Tag voor return voornaam */
    private static final String TAG_VOORNAAM = "firstname";

    /** Tag voor return naam */
    private static final String TAG_NAAM = "name";

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // SessieManager instantiëren
        sessie = new SessieManager(getApplicationContext());

        //region UI componenten toekennen
        btnDoeLogin = (Button) findViewById(R.id.btnDoeLogin);
        txtWachtwoord = (EditText) findViewById(R.id.txtWachtwoord);
        txtGebruikersnaam = (EditText) findViewById(R.id.txtGebruikersnaam);
        tvLoginBericht = (TextView) findViewById(R.id.loginBericht);
        herinnerMij = (CheckBox) findViewById(R.id.herinnerMijCB);
        tvRegistreer = (TextView) findViewById(R.id.registreerBtn);
        //endregion

        // OnClickListener voor registreer knop
        tvRegistreer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Start registratie scherm
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }

        });

        // OnClickListener voor login knop
        btnDoeLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean fout = false;

                // Kijk of velden ingevuld zijn
                if(isLeeg(txtGebruikersnaam)){
                    fout = true;
                    txtGebruikersnaam.setError("Geef een gebruikersnaarm.");
                }
                if(isLeeg(txtWachtwoord)){
                    fout = true;
                    txtWachtwoord.setError("Geef een wachtwoord.");
                }

                // Inloggen wanneer er geen fout optrad
                if(!fout){
                    tvLoginBericht.setText("");

                    // Doe login async taak
                    ContentValues parameters = new ContentValues();
                    parameters.put(TAG_GEBRUIKERSNAAM, txtGebruikersnaam.getText().toString());
                    parameters.put(TAG_WACHTWOORD, txtWachtwoord.getText().toString());

                    new DoeLogin(herinnerMij.isChecked()).execute(parameters);
                }

            }

        });

        // Kijk of er een gebruiker opgeslagen is
        if(sessie.getHerinnerdeGebruiker() != null){
            herinnerMij.setChecked(true);

            // Plaats opgeslagen gebruikersnaam in txtGebruikersnaam
            txtGebruikersnaam.setText(sessie.getHerinnerdeGebruiker());
        }
        else{
            herinnerMij.setChecked(false);
        }

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
     * Async Taak op achtergrond om login te verifiëren.
     * Via HTTP Request naar REST client.
     * */
    class DoeLogin extends AsyncTask<ContentValues, String, String> {

        /** Boolean voor herinnerMij */
        private boolean herinnerMij;

        /**
         * Constructor voor DoeLogin
         * @param herinnerMij boolean voor gebruiker te herinneren
         */
        public DoeLogin(boolean herinnerMij){
            this.herinnerMij = herinnerMij;
        }

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Aan het inloggen, even geduld aub...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return ArrayList<Logging> lijst van loggings
         */
        protected String doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jsonParser.makeHttpRequest(url_login, "POST", params[0]);
            Log.d("Login: ", json.toString());

            String bericht = "";
            try {
                if (json.getInt(TAG_SUCCESS) == 1) {
                    // Maak login sessie aan voor gebruiker
                    sessie.createLoginSession(  params[0].getAsString(TAG_GEBRUIKERSNAAM),
                                                json.getString(TAG_NAAM),
                                                json.getString(TAG_VOORNAAM),
                                                json.getString(TAG_EMAIL),
                                                herinnerMij);

                    // Start het Dashboard
                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(i);
                    bericht = "";
                    finish();

                } else {
                    // Login failed
                    bericht = "Login gefaald. Probeer over enkele ogenblikken opnieuw.";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Geef het bericht terug
            return bericht;
        }

        /**
         * Methode voor na uitvoering taak. Update de UI.
         * **/
        protected void onPostExecute(String bericht) {
            // Dialoogvenster sluiten
            pDialog.dismiss();

            // Set bericht in tvLoginBericht
            tvLoginBericht.setText(bericht);
        }
    }

}
