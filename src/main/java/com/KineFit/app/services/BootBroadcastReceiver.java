package com.KineFit.app.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Thomas on 22/04/16.
 */
public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this bericht is received
        Intent startServiceIntent = new Intent(context, RegisterStepsService.class);
        startWakefulService(context, startServiceIntent);
    }

}