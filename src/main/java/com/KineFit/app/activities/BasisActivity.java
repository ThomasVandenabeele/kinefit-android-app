package com.KineFit.app.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.KineFit.app.R;
import com.KineFit.app.services.SessieManager;
import com.KineFit.app.services.StartTaakServiceOntvanger;

/**
 * Basisactivity om de actionbar op iedere pagina hetzelfde te maken.
 * Hiermee kan ook iedereen aan de SessieManager.
 *
 * Created by Thomas on 17/05/16.
 * @author Thomas Vandenabeele
 *
 */
public class BasisActivity extends Activity{

    //region DATAMEMBERS

    /** SessieManager voor login, logout, ingelogde gebruiker enz. */
    protected SessieManager sessie;

    /** Intent voor de alarmservice */
    protected PendingIntent pendingIntent;

    /** De alarmmanager */
    protected AlarmManager manager;

    /** Is alarm opgezet? */
    protected boolean alarmSet = false;

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessie = new SessieManager(getApplicationContext());

        // Kijk of er een ingelogde gebruiker is, anders naar loginscherm.
        sessie.checkLogin();

        Intent alarmIntent = new Intent(this, StartTaakServiceOntvanger.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
    }

    /**
     * Methode om de opmaak van het menu toe te kennen.
     * @param menu Het menu.
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Methode die kijkt op welk menuitem geklikt werd.
     * @param item Het menuitem.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actie_loguit:

                cancelAlarm();

                // Log de ingelogde gebruiker uit, terug naar loginscherm.
                sessie.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Start de alarm service voor notificaties
     */
    protected void startAlarm() {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        long interval = 600000;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Meldingen aan", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stopt de alarm service voor notificaties
     */
    protected void cancelAlarm() {
        if (manager != null) {
            alarmSet = false;
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Meldingen stopgezet", Toast.LENGTH_SHORT).show();
        }
    }

}
