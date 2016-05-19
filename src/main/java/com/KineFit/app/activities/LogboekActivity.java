package com.KineFit.app.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.adapters.LoggingsAdapter;
import com.KineFit.app.model.Logging;
import com.KineFit.app.services.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Activity voor het Logboek.
 * Op deze activity kan de gebruiker data selecteren en loggings van deze data bekijken.
 *
 * Created by Thomas on 28/04/16.
 * @author Thomas Vandenabeele
 */
public class LogboekActivity extends BasisActivity {


    //region DATAMEMBERS

    /** TextView voor de huidige datum */
    private TextView txtHuidigeDatum;

    /** TextView voor een nieuwe logging te maken */
    private TextView txtNieuweLogging;

    /** CalendarView om datum te selecteren */
    private CalendarView cvKalender;

    /** TextView voor weer te geven als er geen loggings gevonden zijn */
    private TextView txtGeenLoggings;

    /** ListView als de gevonden loggings-lijst */
    private ListView lvLoggingsLijst;

    /** Lang uitgeschreven datum-formatter in nl */
    private SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("nl_NL"));

    /** Kort uitgeschreven datum-formatter */
    private SimpleDateFormat sdf_sql = new SimpleDateFormat("yyyy-MM-dd");

    //endregion

    //region REST: TAGS & URL

    /** JSONParser voor de REST client aan te spreken */
    JSONParser jParser = new JSONParser();

    /** URL om loggings op te vragen van bepaalde datum */
    private static String url_get_loggings_op_datum = "http://thomasvandenabeele.no-ip.org/KineFit/get_logs_on_date.php";

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCES = "success";

    /** Tag voor loggings json-array */
    private static final String TAG_LOGGINGS = "logs";

    /** Tag voor return bericht */
    private static final String TAG_BERICHT = "message";

    /** Tag voor id logging */
    private static final String TAG_LOG_ID = "id";

    /** Tag voor beschrijving logging */
    private static final String TAG_LOG_BESCHRIJVING = "description";

    /** Tag voor tijd logging */
    private static final String TAG_LOG_TIJD = "time";

    /** Tag voor datum logging */
    private static final String TAG_LOG_DATUM = "date";

    /** Tag voor hoeveelheid logging */
    private static final String TAG_LOG_HOEVEELHEID = "amount";

    /** Tag voor eenheid logging */
    private static final String TAG_LOG_EENHEID = "unit";

    /** Tag voor tevredenheid score logging */
    private static final String TAG_LOG_TSCORE = "sScore";

    /** Tag voor pijn score logging */
    private static final String TAG_LOG_PSCORE = "pScore";

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logboek);

        //region UI componenten toekennen
        txtHuidigeDatum = (TextView) findViewById(R.id.huidigeDatum);
        txtNieuweLogging = (TextView) findViewById(R.id.nieuweLoggingBtn);
        txtGeenLoggings = (TextView) findViewById(R.id.tvGeenLoggings);
        cvKalender = (CalendarView) findViewById(R.id.kalender);
        lvLoggingsLijst = (ListView) findViewById(R.id.lvLoggingsLijst);
        //endregion

        // Geef de huidig geselecteerde datum op UI
        txtHuidigeDatum.setText(sdf.format(new Date(cvKalender.getDate())));

        // OnDateChangeListener voor cvKalender
        cvKalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                // Update de huidig geselecteerde datum op UI
                txtHuidigeDatum.setText(sdf.format(new Date(view.getDate())));

                // Laadt de loggings voor deze datum
                doeLaadLoggings(sdf_sql.format(new Date(view.getDate())));

            }

        });

        // OnClickListener voor txtNieuweLogging
        txtNieuweLogging.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start NieuweLoggingActivity
                Intent i = new Intent(getApplicationContext(), NieuweLoggingActivity.class);
                startActivity(i);
            }

        });

        // Laadt de loggings voor de geselecteerde datum
        doeLaadLoggings(sdf_sql.format(new Date()));
    }

    /**
     * Laadt de loggings van de opgegeven datum in de ListView.
     * @param datum de geselecteerde datum (string)
     */
    private void doeLaadLoggings(String datum){

        ContentValues parameters = new ContentValues();
        parameters.put("username", sessie.getUsername());
        parameters.put("date", datum);

        new LaadLoggings().execute(parameters);

    }

    /**
     * Async Taak op achtergrond om de loggings van bepaalde datum op te halen en weer te geven in de ListView.
     * Via HTTP Request naar REST client.
     * */
    class LaadLoggings extends AsyncTask<ContentValues, String, ArrayList<Logging>> {

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return ArrayList<Logging> lijst van loggings
         */
        protected ArrayList<Logging> doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_get_loggings_op_datum, "GET", params[0]);
            Log.d("Loggings: ", json.toString());

            // Nieuwe lege ArrayList<Logging>
            ArrayList<Logging> loggingsLijst = new ArrayList<>();

            try {
                if (json.getInt(TAG_SUCCES) == 1) {
                    // Check of er effectief loggings zijn
                    if(!json.getString(TAG_BERICHT).equals("No logs found")){
                        JSONArray loggings = json.getJSONArray(TAG_LOGGINGS);

                        for (int i = 0; i < loggings.length(); i++) {
                            JSONObject c = loggings.getJSONObject(i);

                            // Maak een nieuwe logging
                            Logging l = new Logging(
                                                c.getInt(TAG_LOG_ID),
                                                c.getString(TAG_LOG_BESCHRIJVING),
                                                Time.valueOf(c.getString(TAG_LOG_TIJD)),
                                                java.sql.Date.valueOf(c.getString(TAG_LOG_DATUM)),
                                                c.getInt(TAG_LOG_HOEVEELHEID),
                                                c.getString(TAG_LOG_EENHEID),
                                                c.getInt(TAG_LOG_TSCORE),
                                                c.getInt(TAG_LOG_PSCORE));

                            // logging toevoegen aan de lijst
                            loggingsLijst.add(l);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Geef de loggings lijst terug
            return loggingsLijst;
        }

        /**
         * Methode voor na uitvoering taak.
         * Update de UI door de loggings weer te geven in de ListView.
         * **/
        protected void onPostExecute(ArrayList<Logging> logList) {

            // Kijk of er effectief loggings zijn
            if(logList.size()>0){

                // Verberg info (geen loggings), toon lijst
                txtGeenLoggings.setVisibility(View.GONE);
                lvLoggingsLijst.setVisibility(View.VISIBLE);

                // LoggingsAdapter voor ListView
                LoggingsAdapter ad = new LoggingsAdapter(LogboekActivity.this, R.layout.logs_list_item, logList);

                // Adapter aan ListView hangen
                lvLoggingsLijst.setAdapter(ad);

            }
            else{
                // Verberg lijst, toon info (geen loggings)
                txtGeenLoggings.setVisibility(View.VISIBLE);
                lvLoggingsLijst.setVisibility(View.GONE);
            }

        }

    }

}
