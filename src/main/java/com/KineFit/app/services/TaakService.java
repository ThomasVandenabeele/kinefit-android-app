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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.Header;

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

    /** SyncHttpClient voor service */
    private AsyncHttpClient aClient = new SyncHttpClient();

    /*+ De huidige context */
    private Context _context;

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

        _context = getApplicationContext();
    }


    /**
     * Opgeroepen op de werkende thread.
     * @param intent nodige intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Kijk voor nieuwe taken.
        aClient.get(url_get_nieuwe_taken, new RequestParams(TAG_GEBRUIKERSNAAM, sessie.getUsername()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int aantalTaken = 0;
                // If the response is JSONObject instead of expected JSONArray
                System.out.println("Taken ophalen gelukt:" + response.toString());

                try{
                    if (response.getInt(TAG_SUCCES) == 1) {
                        aantalTaken = response.getInt(TAG_AANTALTAKEN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                stuurNotificatie(_context, aantalTaken);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) { }

        });
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

}
