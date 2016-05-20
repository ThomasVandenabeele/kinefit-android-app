package com.KineFit.app.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Service om de stappen te registreren
 *
 * Created by Thomas on 22/04/16.
 * @author Thomas Vandenabeele
 */
public class RegistreerStappenService extends IntentService implements SensorEventListener {

    //region DATAMEMBERS

    /** SensorManager om de sensor aan te spreken */
    private SensorManager sensorManager;

    /** behaalde stappen sinds registratie listener */
    private int stepCounter = 0;

    /** eerste stapwaarde van sensor */
    private int counterSteps = 0;

    /** boolean om te posten of niet */
    private boolean post = false;

    /** start tijd in millis */
    private long startTijdMillis;

    /** SessieManager voor gebruikerinfo */
    private SessieManager sessie;

    //endregion

    //region REST: TAGS & URL

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    /** URL om stappen te registreren */
    private static final String url_steps = "http://thomasvandenabeele.no-ip.org/KineFit/register_steps.php";

    /** Tag voor gebruikersnaam */
    private static final String TAG_GEBRUIKERSNAAM = "username";

    /** Tag voor stap aantal */
    private static final String TAG_AANTALSTAPPEN = "number_of_steps";

    /** Tag voor stap starttijd */
    private static final String TAG_STARTTIJD = "start_time";

    /** Tag voor stap eindtijd */
    private static final String TAG_EINDTIJD = "end_time";

    //endregion

    /**
     * Constructor voor RegistreerStappenService
     */
    public RegistreerStappenService() {
        // Naam geven aan de werkende thread
        super("step-service");
    }

    /**
     * Methode die opgeroepen wordt bij aanmaak service.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiëer SessieManager
        sessie = new SessieManager(getApplicationContext());

        // Instantiëer de sensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Instantiëer de sensor
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Kijk of het toestel de stappensensor aan boord heeft
        if (countSensor != null) {
            // Registreer Listener
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Stappen sensor beschikbaar, je stappen worden geregistreerd!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Stappen sensor niet beschikbaar!", Toast.LENGTH_LONG).show();
        }

        // Stel starttijd in
        startTijdMillis = System.currentTimeMillis();
    }


    /**
     * Opgeroepen op de werkende thread.
     * @param intent nodige intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {}

    /**
     * Methode wordt opgeroepen wanneer de sensor waarde veranderd.
     * @param event sensor event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        int firstSteps = counterSteps;

        if (counterSteps < 1) { // initiële waarde
            counterSteps = (int) event.values[0];
        }

        // Bepaal gezette stappen bij event
        int aantalStappen = ((int) event.values[0] - counterSteps) - stepCounter;
        stepCounter = (int) event.values[0] - counterSteps;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date datumke = new java.util.Date();
        String timestamp = df.format(datumke.getTime() + (event.timestamp - System.nanoTime()) / 1000000L);

        System.out.println("Aantal stappen: "+ aantalStappen);

        // Op UI stappen weergeven: DISABLED
        // Toast.makeText(this, "Aantal: "+ stepCounter, Toast.LENGTH_SHORT).show();

        // Bij geen extra stappen, niet posten. Anders wel.
        if(aantalStappen==0) post = false;
        else post = true;

        if(post){
            // tijden formatteren
            String starttijd = df.format(startTijdMillis);
            String eindtijd = timestamp;

            ContentValues parameters = new ContentValues();
            parameters.put(TAG_GEBRUIKERSNAAM, sessie.getUsername());
            parameters.put( TAG_AANTALSTAPPEN, aantalStappen);
            parameters.put(TAG_STARTTIJD, starttijd);
            parameters.put(TAG_EINDTIJD, eindtijd);

            new RegisterSteps().execute(parameters);

            startTijdMillis = System.currentTimeMillis();
        }

        // Aanmaken van sensor ook niet posten!
        if(firstSteps < 1){
            post = true;
        }
    }

    /**
     * Methode die opgeroepen wordt wanneer de nauwkeurigheid van de sensor veranderd.
     * Momenteel niet gebruikt.
     * @param sensor sensor
     * @param accuracy nauwkeurigheid
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    /**
     * Async Taak op achtergrond om login te verifiëren.
     * Via HTTP Request naar REST client.
     * */
    class RegisterSteps extends AsyncTask<ContentValues, String, String> {

        protected String doInBackground(ContentValues... params) {

            // Maakt de request
            jsonParser.makeHttpRequest(url_steps, "POST", params[0]);

            // Niet relevant
            return null;

        }

    }

}
