package com.KineFit.app.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.services.JSONParser;

import org.json.JSONObject;

/**
 * Activity voor het Dashboard.
 * Op deze activity kunnen de gebruikers navigeren naar de verschillende andere activities.
 *
 * Created by Thomas on 22/04/16.
 * @author Thomas Vandenabeele
 */
public class DashboardActivity extends BasisActivity {

    //region DATAMEMBERS

    /** Button voor StapActivity */
    private Button btnStappen;

    /** Button voor TakenActivity */
    private Button btnTaken;

    /** Button voor LogboekActivity */
    private Button btnLogboek;

    /** Textview voor het welkom-bericht */
    private TextView txtWelkom;

    //endregion

    //region REST: TAGS & URL

    /** JSONParser voor de REST client aan te spreken */
    JSONParser jParser = new JSONParser();

    /** URL om het aantal nieuwe taken op te vragen */
    private static String url_get_nieuwe_taken = "http://thomasvandenabeele.no-ip.org/KineFit/get_count_new_tasks.php";

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCES = "success";

    /** Tag voor aantal onbekeken taken */
    private static final String TAG_AANTALTAKEN = "tasksCount";

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        //region UI componenten toekennen
        txtWelkom = (TextView) findViewById(R.id.Welcome);
        btnStappen = (Button) findViewById(R.id.btnSteps);
        btnTaken = (Button) findViewById(R.id.btnTasks);
        btnLogboek = (Button) findViewById(R.id.btnLoggings);
        //endregion

        // Welkoms-bericht plaatsen
        txtWelkom.setText("Welkom, " + sessie.getVoornaam() +"!");

        // ClickListener voor btnStappen
        btnStappen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Start StapActivity
                Intent i = new Intent(getApplicationContext(), StapActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        // ClickListener voor btnTaken
        btnTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start TakenActivity
                Intent i = new Intent(getApplicationContext(), TakenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }

        });

        // ClickListener voor btnLogboek
        btnLogboek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Start LogboekActivity
                Intent i = new Intent(getApplicationContext(), LogboekActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }

        });

        // Als er ingelogde gebruiker is: tel ongelezen taken (async taak).
        if(sessie.isLoggedIn()) {
            ContentValues params = new ContentValues();
            params.put("username", sessie.getUsername());
            new CountNewTasks().execute(params);
        }

    }

    /**
     * Async Taak op achtergrond om het aantal ongelezen taken op te halen en weer te geven.
     * Via HTTP Request naar REST client.
     * */
    class CountNewTasks extends AsyncTask<ContentValues, String, Integer> {

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return aantal nieuwe taken
         */
        protected Integer doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_get_nieuwe_taken, "GET", params[0]);
            Log.d("Aantal nieuwe taken: ", json.toString());

            try{
                if (json.getInt(TAG_SUCCES) == 1) {
                    return json.getInt(TAG_AANTALTAKEN);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }

        /**
         * Methode voor na uitvoering taak.
         * Update de UI door het aantal nieuwe taken weer te geven als deze verschillend is van 0.
         * **/
        protected void onPostExecute(int aantal) {
            if(aantal != 0) btnTaken.setText(btnTaken.getText() + " (" + aantal + ")");
        }

    }

}
