package com.KineFit.app.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import com.KineFit.app.adapters.TakenAdapter;
import com.KineFit.app.model.Taak;
import com.KineFit.app.model.enums.TaskStatus;
import com.KineFit.app.services.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Activity voor het taken overzicht.
 * Op deze activity kan de gebruiker taken bekijken en voltooien.
 *
 * Created by Thomas on 30/04/16.
 * @author Thomas Vandenabeele
 */
public class TakenActivity extends BasisActivity {

    //region DATAMEMBERS

    /** pDialog voor de UI */
    private ProgressDialog pDialog;

    /** LinearLayout voor het filtermenu */
    private LinearLayout filterTakenMenu;

    /** ToggleButton voor filteren */
    private ToggleButton tbFilter;

    /** Switch voor gesloten taken */
    private Switch sGeslotenTaken;

    /** Switch voor gefaalde taken */
    private Switch sGefaaldeTaken;

    /** Boolean gesloten taken */
    private boolean geslotenTaken;

    /** Boolean gefaalde taken */
    private boolean gefaaldeTaken;

    /** ListView voor de taken */
    private ListView lvTaken;

    /** TextView voor melding geen taken */
    private TextView txtGeenTaken;

    /** TakenLijst */
    private ArrayList<Taak> takenLijst;

    /** SQL datum formatter */
    private SimpleDateFormat sqlDatumFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Korte datum formatter */
    private SimpleDateFormat korteDatum = new SimpleDateFormat("dd-MM-yyyy");

    /** Korte tijd formatter */
    private SimpleDateFormat korteTijd = new SimpleDateFormat("HH:mm");

    //endregion

    //region REST: TAGS & URL

    /** JSONParser voor de REST client aan te spreken */
    JSONParser jParser = new JSONParser();

    /** Tag voor gebruikersnaam */
    private static final String TAG_GEBRUIKERSNAAM = "username";

    //-------------------------------------------------------------------------------------------------------------------------------//

    /** URL om alle taken op te vragen */
    private static String url_alle_taken = "http://thomasvandenabeele.no-ip.org/KineFit/get_all_tasks.php";

    /** Tag voor succes */
    private static final String TAG_SUCCES = "success";

    /** Tag voor taken */
    private static final String TAG_TAKEN = "tasks";

    /** Tag voor taak id */
    private static final String TAG_ID = "id";

    /** Tag voor taak naam */
    private static final String TAG_NAAM = "message";

    /** Tag voor taak aanmaak datum */
    private static final String TAG_AANMAAKDATUM = "created_at";

    /** Tag voor taak status */
    private static final String TAG_STATUS = "status";

    //-------------------------------------------------------------------------------------------------------------------------------//

    /** URL om het de status van een taak te updaten */
    private static String url_update_taak_status = "http://thomasvandenabeele.no-ip.org/KineFit/update_status_task.php";

    //-------------------------------------------------------------------------------------------------------------------------------//

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taken_lijst);

        //region UI componenten toekennen
        txtGeenTaken = (TextView) findViewById(R.id.txtGeenTaken);
        filterTakenMenu = (LinearLayout)findViewById(R.id.filterMenuTaken);
        tbFilter = (ToggleButton)findViewById(R.id.tbFilter);
        sGeslotenTaken = (Switch)findViewById(R.id.sGeslotenTaken);
        sGefaaldeTaken = (Switch)findViewById(R.id.sGefaaldeTaken);
        lvTaken = (ListView)findViewById(R.id.lvTaken);
        //endregion

        // OnCheckedChangeListener voor togglebutton filteren
        tbFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Toon filter menu
                    filterTakenMenu.setVisibility(View.VISIBLE);
                }
                else{
                    // Verberg filter menu
                    filterTakenMenu.setVisibility(View.GONE);
                }
            }
        });

        // OnCheckedChangeListener voor sGeslotenTaken
        sGeslotenTaken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                geslotenTaken = isChecked;

                // Herlaad de listView
                new LaadAlleTaken().execute();
            }
        });

        // OnCheckedChangeListener voor sGefaaldeTaken
        sGefaaldeTaken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gefaaldeTaken = isChecked;

                // Herlaad de listView
                new LaadAlleTaken().execute();
            }
        });

        // Instantiëren members
        geslotenTaken = sGeslotenTaken.isChecked();
        gefaaldeTaken = sGefaaldeTaken.isChecked();
        takenLijst = new ArrayList<Taak>();

        // Laad de listView
        new LaadAlleTaken().execute();

        // OnItemClickListener voor ListView
        lvTaken.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get de geselecteerde taak
                Taak t = takenLijst.get(position);

                // Volgende code enkel wanneer de status nieuw of open is:
                if(t.getStatus().equals(TaskStatus.OPEN)||t.getStatus().equals(TaskStatus.NEW)){
                    final String pid = ((TextView) view.findViewById(R.id.task_pid)).getText().toString();
                    final ContentValues parameters = new ContentValues();
                    parameters.put(TAG_ID, pid);

                    // Maak alert venster
                    new AlertDialog.Builder(TakenActivity.this)
                            .setTitle("Voltooi taak")
                            .setMessage("Is u in deze taak geslaagd?")
                            .setPositiveButton("Geslaagd",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            parameters.put(TAG_NAAM, TaskStatus.DONE.toString());
                                            new UpdateTaakStatus().execute(parameters);

                                            // Sluit alert
                                            dialog.cancel();
                                        }
                                    })
                            .setNeutralButton("Gefaald",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            parameters.put(TAG_NAAM, TaskStatus.FAILED.toString());
                                            new UpdateTaakStatus().execute(parameters);

                                            // Sluit alert
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Annuleren",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Sluit alert
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

    /**
     * Async Taak op achtergrond om het aantal ongelezen taken op te halen en weer te geven.
     * Via HTTP Request naar REST client.
     * */
    class LaadAlleTaken extends AsyncTask<String, String, String> {

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TakenActivity.this);
            pDialog.setMessage("Taken laden, gelieve even te wachten aub...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params Strings niet relevant
         * @return niet relevant
         */
        protected String doInBackground(String... params) {

            // Maakt de request en geeft het resultaat
            ContentValues parameters = new ContentValues();
            parameters.put(TAG_GEBRUIKERSNAAM, sessie.getUsername());
            JSONObject json = jParser.makeHttpRequest(url_alle_taken, "GET", parameters);
            Log.d("Alle taken: ", json.toString());

            try {
                if (json.getInt(TAG_SUCCES) == 1) {

                    // Taken ophalen
                    JSONArray tasks = json.getJSONArray(TAG_TAKEN);
                    takenLijst.clear();

                    // Omzetten in ArrayList<Taak>
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);
                        boolean add = true;

                        Date creation_date = new java.sql.Date(korteDatum.parse(c.getString(TAG_AANMAAKDATUM)).getTime());
                        Taak t = new Taak(c.getInt(TAG_ID),
                                            c.getString(TAG_NAAM),
                                            creation_date,
                                            TaskStatus.valueOf(c.getString(TAG_STATUS)));

                        // Filtercriteria checken
                        if(!geslotenTaken && t.getStatus().equals(TaskStatus.DONE)) add = false;
                        if(!gefaaldeTaken && t.getStatus().equals(TaskStatus.FAILED)) add = false;

                        // Toevoegen aan lijst
                        if(add) takenLijst.add(t);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Methode voor na uitvoering taak.
         * Update de UI door ListView te vullen.
         * **/
        protected void onPostExecute(String file_url) {
            // pDialog sluiten
            pDialog.dismiss();
            if(takenLijst.size()>0) {
                // Geen melding
                txtGeenTaken.setVisibility(View.GONE);
                lvTaken.setVisibility(View.VISIBLE);

                runOnUiThread(new Runnable() {
                    public void run() {
                        TakenAdapter ta = new TakenAdapter(TakenActivity.this, R.layout.task_list_item, takenLijst);
                        lvTaken.setAdapter(ta);

                    }
                });
            }
            else {
                // Toon melding
                txtGeenTaken.setVisibility(View.VISIBLE);
                lvTaken.setVisibility(View.GONE);
            }


        }

    }

    /**
     * Async Taak op achtergrond om het aantal ongelezen taken op te halen en weer te geven.
     * Via HTTP Request naar REST client.
     * */
    class UpdateTaakStatus extends AsyncTask<ContentValues, String, Integer> {

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TakenActivity.this);
            pDialog.setMessage("Updating Taak ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param parameters Strings niet relevant
         * @return int geslaagd
         */
        protected Integer doInBackground(ContentValues... parameters) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_update_taak_status, "POST", parameters[0]);
            Log.d("Update taak status: ", json.toString());

            try {
                return json.getInt(TAG_SUCCES);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }

        /**
         * Methode voor na uitvoering taak.
         * Update de UI door de ListView voor taken te herladen.
         * **/
        protected void onPostExecute(int success) {

            // pDialog sluiten
            pDialog.dismiss();

            if (success == 1) {
                new LaadAlleTaken().execute();
            }

        }

    }
}



