package com.KineFit.app.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.KineFit.app.R;
import com.KineFit.app.activities.DashboardActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Service om te kijken voor nieuwe taken.
 *
 * Created by Thomas on 22/04/16.
 * @author Thomas Vandenabeele
 */
public class TaakService extends IntentService {

    //region DATAMEMBERS

    /** SessieManager voor gebruikerinfo */
    private SessieManager sessie;

    //endregion

    //region REST: TAGS & URL

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    /** URL om het aantal nieuwe taken op te vragen */
    private static String url_get_nieuwe_taken = "http://thomasvandenabeele.no-ip.org/KineFit/get_count_new_tasks.php";

    /** Tag voor gebruikersnaarm */
    private static final String TAG_GEBRUIKERSNAAM = "username";

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCES = "success";

    /** Tag voor aantal onbekeken taken */
    private static final String TAG_AANTALTAKEN = "tasksCount";

    //endregion

    /**
     * Constructor voor TaakService
     */
    public TaakService() {
        // Naam geven aan de werkende thread
        super("taak-service");

    }

    /**
     * Methode die opgeroepen wordt bij aanmaak service.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiëer SessieManager
        sessie = new SessieManager(getApplicationContext());

        ContentValues params = new ContentValues();
        params.put(TAG_GEBRUIKERSNAAM, sessie.getUsername());

        // Voer async task uit
        new TelNieuweTaken(this).execute(params);

    }


    /**
     * Opgeroepen op de werkende thread.
     * @param intent nodige intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
    }

    /**
     * Stuur een notificatie naar het toestel
     * @param context huidige context
     * @param aantal aantal nieuwe taken
     */
    private void stuurNotificatie(Context context, int aantal) {

        String bericht = "";
        Boolean stuur = true;
        if(aantal == 1) bericht = "Er is 1 nieuwe taak";
        else if(aantal > 1) bericht = "Er zijn " + aantal + " nieuwe taken";
        else stuur = false;

        if(stuur){

            Intent myIntent = new Intent(context, DashboardActivity.class);
            PendingIntent intent2 = PendingIntent.getActivity(context, 1,
                    myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(bericht)
                    .setContentText("Tik om KineFit te starten.")
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0));

            notificationBuilder.setContentIntent(intent2);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notificationBuilder.build());

        }

    }


    /**
     * Async Taak op achtergrond om het aantal ongelezen taken op te halen en weer te geven.
     * Via HTTP Request naar REST client.
     * */
    class TelNieuweTaken extends AsyncTask<ContentValues, String, Integer> {

        private Context c;

        public TelNieuweTaken(Context c){
            this.c = c;
        }
        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return aantal nieuwe taken
         */
        protected Integer doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jsonParser.makeHttpRequest(url_get_nieuwe_taken, "GET", params[0]);
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
         * Stuur notificatie
         * **/
        protected void onPostExecute(Integer aantal) {
            stuurNotificatie(c, aantal);
        }

    }

}
