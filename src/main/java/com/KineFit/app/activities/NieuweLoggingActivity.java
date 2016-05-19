package com.KineFit.app.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.KineFit.app.R;
import com.KineFit.app.model.Logs_Type;
import com.KineFit.app.services.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Activity voor een nieuwe logging te maken.
 * Op deze activity kan de gebruiker een nieuwe logging maken en een loggingstype toevoegen.
 *
 * Created by Thomas on 28/04/16.
 * @author Thomas Vandenabeele
 */
public class NieuweLoggingActivity extends BasisActivity {

    //region DATAMEMBERS

    /** Spinner voor type */
    private Spinner spType;

    /** TextView voor de eenheid */
    private TextView txtEenheid;

    /** SeekBar voor de tevredenheid score */
    private SeekBar sbTScore;

    /** SeekBar voor de pijn score */
    private SeekBar sbSScore;

    /** EditText voor de hoeveelheid */
    private EditText etHoeveelheid;

    /** TextView voor de tevredenheid score */
    private TextView txtTScore;

    /** TextView voor de pijn score */
    private TextView txtPScore;

    /** CheckBox voor verschillend tijdstip te kiezen */
    private CheckBox cbVerschillendTijdstip;

    /** TextView voor de geselecteerde datum weer te geven */
    private TextView txtGeselecteerdeDatum;

    /** TextView voor nieuw log type (knop) */
    private TextView txtNieuwLogType;

    /** Button voor de logging aan te maken */
    private Button btnMaakLogging;

    /** ProgressDialog voor de UI */
    private ProgressDialog pDialog;

    /** De geselecteerde datum */
    private java.util.Date geselecteerdeDatum;

    /** SQL datum formatter */
    SimpleDateFormat sqlDatumString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Korte datum formatter */
    SimpleDateFormat korteDatum = new SimpleDateFormat("dd-MM-yyyy");

    /** Korte tijd formatter */
    SimpleDateFormat korteTijd = new SimpleDateFormat("HH:mm");

    //endregion

    //region REST: TAGS & URL

    /** JSONParser voor de REST client aan te spreken */
    JSONParser jParser = new JSONParser();

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCES = "success";

    //---------------------------------------------------------------------------------------------------------------------//

    /** URL voor de logging types op te halen */
    private static String url_get_alle_loggings_types = "http://thomasvandenabeele.no-ip.org/KineFit/get_all_logs_type.php";

    /** Tag voor loggings-types */
    private static final String TAG_LOGGINGS_TYPES = "logs_type";

    /** Tag voor id loggingstype */
    private static final String TAG_LOGGINGS_ID = "id";

    /** Tag voor eenheid loggingstype */
    private static final String TAG_LOGGINGS_EENHEID = "unit";

    /** Tag voor beschrijving loggingstype */
    private static final String TAG_LOGGINGS_BESCHRIJVING= "description";

    //---------------------------------------------------------------------------------------------------------------------//

    /** URL voor een logging type te maken */
    private static String url_maak_nieuw_loggings_type = "http://thomasvandenabeele.no-ip.org/KineFit/create_logs_type.php";

    /** Tag voor beschrijving nieuw loggingstype */
    private static final String TAG_NIEUW_TYPE_BESCHRIJVING = "description";

    /** Tag voor eenheid nieuw loggingstype */
    private static final String TAG_NIEUW_TYPE_EENHEID = "unit";

    //---------------------------------------------------------------------------------------------------------------------//

    /** URL voor een logging te maken */
    private static String url_maak_nieuwe_logging = "http://thomasvandenabeele.no-ip.org/KineFit/create_log.php";

    /** Tag voor gebruikersnaam */
    private static final String TAG_NIEUW_LOGGING_GEBRUIKERSNAAM = "username";

    /** Tag voor type id */
    private static final String TAG_NIEUW_LOGGING_TYPE_ID = "type_id";

    /** Tag voor hoeveelheid */
    private static final String TAG_NIEUW_LOGGING_HOEVEELHEID = "amount";

    /** Tag voor tevredenheid score */
    private static final String TAG_NIEUW_LOGGING_TSCORE = "sScore";

    /** Tag voor pijn score */
    private static final String TAG_NIEUW_LOGGING_PSCORE = "pScore";

    /** Tag voor datum */
    private static final String TAG_NIEUW_LOGGING_DATUM = "datetime";


    //---------------------------------------------------------------------------------------------------------------------//

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nieuwe_logging);

        //region UI componenten toekennen
        txtGeselecteerdeDatum = (TextView) findViewById(R.id.geselecteerdeDatum);
        txtNieuwLogType = (TextView) findViewById(R.id.nieuwLogType);
        cbVerschillendTijdstip = (CheckBox) findViewById(R.id.cbVerschillendTijdstip);
        spType = (Spinner) findViewById(R.id.spType);
        txtEenheid = (TextView) findViewById(R.id.txtEenheid);
        sbTScore = (SeekBar) findViewById(R.id.sbTScore);
        sbSScore = (SeekBar) findViewById(R.id.sbSScore);
        txtTScore = (TextView) findViewById(R.id.txtTScore);
        txtPScore = (TextView) findViewById(R.id.txtPScore);
        etHoeveelheid = (EditText) findViewById(R.id.etHoeveelheid);
        btnMaakLogging = (Button) findViewById(R.id.btnMaakLogging);
        //endregion

        // Laad spinner voor logs type
        new LaadLoggingsType().execute();

        // Stel huidige datum in als geselecteerd
        geselecteerdeDatum = Calendar.getInstance().getTime();
        txtGeselecteerdeDatum.setText(korteDatum.format(geselecteerdeDatum) + " om " + korteTijd.format(geselecteerdeDatum));

        // OnClickListener voor nieuw logtype
        txtNieuwLogType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Maak een AlertDialog op huidige view.
                final View dialogView = View.inflate(NieuweLoggingActivity.this, R.layout.nieuw_loggings_type, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(NieuweLoggingActivity.this).create();

                dialogView.findViewById(R.id.maakLogsType).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //region UI componenten toekennen
                        TextView beschrijving = (TextView) dialogView.findViewById(R.id.newLog_Description);
                        TextView eenheid = (TextView) dialogView.findViewById(R.id.newLog_Unit);
                        //endregion

                        // Maak het nieuwe type aan via async task
                        ContentValues parameters = new ContentValues();
                        parameters.put(TAG_NIEUW_TYPE_BESCHRIJVING, beschrijving.getText().toString());
                        parameters.put(TAG_NIEUW_TYPE_EENHEID, eenheid.getText().toString());
                        new MaakNieuwLoggingsType().execute(parameters);

                        // Verberg het dialoogvenster
                        alertDialog.dismiss();

                        // Herlaad de logstype spinner
                        new LaadLoggingsType().execute();
                    }});

                alertDialog.setView(dialogView);
                alertDialog.show();
            }

        });

        // OnCheckedChangeListener voor de checkbox voor een verschillend tijdstip
        cbVerschillendTijdstip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Aangevinkt of niet?
                if(isChecked){
                    // Dialoogvenster om een nieuwe datum te kiezen
                    final View dialogView = View.inflate(NieuweLoggingActivity.this, R.layout.datum_en_tijd_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(NieuweLoggingActivity.this).create();

                    dialogView.findViewById(R.id.set_datum_tijd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //region UI componenten toekennen
                            DatePicker datumPicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                            TimePicker tijdPicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                            //endregion

                            // Haal geselecteerde datum/tijd op
                            Calendar kalender = new GregorianCalendar(datumPicker.getYear(),
                                    datumPicker.getMonth(),
                                    datumPicker.getDayOfMonth(),
                                    tijdPicker.getCurrentHour(),
                                    tijdPicker.getCurrentMinute());

                            // Stel deze datum in
                            geselecteerdeDatum = kalender.getTime();
                            txtGeselecteerdeDatum.setText(korteDatum.format(kalender.getTime()) + " om " + korteTijd.format(kalender.getTime()));

                            // Verberg het dialoogvenster
                            alertDialog.dismiss();
                        }});

                    alertDialog.setView(dialogView);
                    alertDialog.show();
                }
                else{
                    // Anders de huidige datum kiezen
                    geselecteerdeDatum = Calendar.getInstance().getTime();
                    txtGeselecteerdeDatum.setText(korteDatum.format(geselecteerdeDatum) + " om " + korteTijd.format(geselecteerdeDatum));
                }

            }
        });

        // OnItemSelectedListener voor het type te kiezen
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Haal gekozen op en pas de TextView aan
                Logs_Type lt = (Logs_Type) parent.getItemAtPosition(position);
                String key = lt.getEenheid();
                txtEenheid.setText(key);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // OnSeekBarChangeListener voor de tevredenheid score
        sbTScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int c = Color.BLACK;

                // Stel de juiste kleuren in, ifv de gekozen waarde
                if(progress >= 0 && progress < 3) c = Color.GREEN;
                else if(progress > 7 && progress <= 10) c = Color.RED;

                // Update UI
                txtPScore.setTextColor(c);
                txtPScore.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // OnSeekBarChangeListener voor de pijn score
        sbSScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int c = Color.BLACK;

                // Stel de juiste kleuren in, ifv de gekozen waarde
                if (progress >= 0 && progress < 3) c = Color.RED;
                else if(progress > 7 && progress <= 10) c = Color.GREEN;

                // Update UI
                txtTScore.setTextColor(c);
                txtTScore.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // OnClickListener om een nieuwe logging aan te maken
        btnMaakLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: VALIDATIE !!!!!!!!!!!!!!!!!!!!!!!!!!

                // Maak de nieuwe logging aan
                ContentValues parameters = new ContentValues();
                parameters.put(TAG_NIEUW_LOGGING_GEBRUIKERSNAAM, sessie.getUsername());
                parameters.put(TAG_NIEUW_LOGGING_TYPE_ID, ((Logs_Type)(spType.getSelectedItem())).getId());
                parameters.put(TAG_NIEUW_LOGGING_HOEVEELHEID, Integer.valueOf(etHoeveelheid.getText().toString()));
                parameters.put(TAG_NIEUW_LOGGING_TSCORE, Integer.valueOf(txtTScore.getText().toString()));
                parameters.put(TAG_NIEUW_LOGGING_PSCORE, Integer.valueOf(txtPScore.getText().toString()));
                parameters.put(TAG_NIEUW_LOGGING_DATUM, String.valueOf(sqlDatumString.format(geselecteerdeDatum)));
                new MaakNieuweLogging().execute(parameters);

            }
        });

    }


    /**
     * Async Taak op achtergrond om alle loggingstypes op te halen en spinner te updaten.
     * Via HTTP Request naar REST client.
     * */
    class LaadLoggingsType extends AsyncTask<String, String, ArrayList<Logs_Type>> {

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param args parameters
         * @return ArrayList<Logs_Type> alle mogelijke types
         */
        protected ArrayList<Logs_Type> doInBackground(String... args) {

            // Maakt de request en geeft het resultaat
            ContentValues parameters = new ContentValues();
            JSONObject json = jParser.makeHttpRequest(url_get_alle_loggings_types, "GET", parameters);
            Log.d("Loggings Types: ", json.toString());

            JSONArray logs_types = null;
            ArrayList<Logs_Type> loggingsTypeLijst = new ArrayList<>();
            try {
                if (json.getInt(TAG_SUCCES) == 1) {
                    logs_types = json.getJSONArray(TAG_LOGGINGS_TYPES);

                    // Haal de loggings types op uit json
                    for (int i = 0; i < logs_types.length(); i++) {
                        JSONObject c = logs_types.getJSONObject(i);
                        loggingsTypeLijst.add(
                                new Logs_Type(  c.getInt(TAG_LOGGINGS_ID),
                                                c.getString(TAG_LOGGINGS_BESCHRIJVING),
                                                c.getString(TAG_LOGGINGS_EENHEID))
                        );
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Geef de loggings types terug
            return loggingsTypeLijst;
        }

        /**
         * Methode voor na uitvoering taak. Update de UI.
         * **/
        protected void onPostExecute(ArrayList<Logs_Type> loggingsTypeLijst) {
            if(loggingsTypeLijst.size()>0){
                ArrayAdapter<Logs_Type> spinnerAdapter = new ArrayAdapter<Logs_Type>(NieuweLoggingActivity.this, android.R.layout.simple_spinner_item, loggingsTypeLijst);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                //Stel de spinner adapter in
                spType.setAdapter(spinnerAdapter);
            }
        }

    }

    /**
     * Async Taak op achtergrond om een nieuw loggingstype aan te maken.
     * Via HTTP Request naar REST client.
     * */
    class MaakNieuwLoggingsType extends AsyncTask<ContentValues, String, Boolean> {

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NieuweLoggingActivity.this);
            pDialog.setMessage("Aanmaken nieuw type...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues met parameters voor de REST client
         * @return
         */
        protected Boolean doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_maak_nieuw_loggings_type, "POST", params[0]);
            Log.d("Maak nieuw log spType: ", json.toString());

            try {
                return (json.getInt(TAG_SUCCES) == 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Methode voor na uitvoering taak. Sluit pDialog.
         * **/
        protected void onPostExecute(Boolean gelukt) {
            // dismiss the dialog once done
            if(gelukt) pDialog.dismiss();
        }

    }


    /**
     * Async Taak op achtergrond om een nieuwe logging aan te maken.
     * Via HTTP Request naar REST client.
     * */
    class MaakNieuweLogging extends AsyncTask<ContentValues, String, String> {

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NieuweLoggingActivity.this);
            pDialog.setMessage("Aanmaken van logging...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST Client
         * @return
         */
        protected String doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_maak_nieuwe_logging, "POST", params[0]);
            Log.d("Nieuwe logging: ", json.toString());

            try {
                if (json.getInt(TAG_SUCCES) == 1) {
                    // Terug naar Logboek
                    Intent i = new Intent(getApplicationContext(), LogboekActivity.class);
                    startActivity(i);

                    // Sluit dit scherm
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Methode voor na uitvoering taak. Sluit pDialog.
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
