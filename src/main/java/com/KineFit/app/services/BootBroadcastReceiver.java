package com.KineFit.app.services;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * BootBroadcastReceiver
 * Bij boot van toestel wordt de stapActivity gestart.
 *
 * Created by Thomas on 22/04/16.
 * @author Thomas Vandenabeele
 */
public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    /**
     * Opgeroepen wanneer BroadcastReceiver een Intent Broadcast ontvangt.
     * @param context huidige context
     * @param intent huidige intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // Start de step service
        Intent startServiceIntent = new Intent(context, RegistreerStappenService.class);
        startWakefulService(context, startServiceIntent);

        // Kijk of scherm uit gaat
        if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            return;
        }

        // Start nieuwe service wanneer scherm uitgaat.
        final Context c = context;
        Runnable runnable = new Runnable() {
            public void run() {
                Intent startServiceIntent = new Intent(c, RegistreerStappenService.class);
                startWakefulService(c, startServiceIntent);
            }
        };

        new Handler().postDelayed(runnable, 500);

    }

}