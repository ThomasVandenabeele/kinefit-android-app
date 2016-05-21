package com.KineFit.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * StartTaakServiceOntvanger
 * Zorgt ervoor dat de taak volledig wordt uitgevoerd, CPU blijft actief.
 *
 * Created by Thomas on 20/05/16.
 * @author Thomas Vandenabeele
 */
public class StartTaakServiceOntvanger extends BroadcastReceiver {

    /**
     * Opgeroepen wanneer BroadcastReceiver een Intent Broadcast ontvangt.
     * @param context huidige context
     * @param intent huidige intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("----------------START TAAK OPHALEN---------------");
        Intent i = new Intent(context, TaakService.class);
        context.startService(i);

    }

}