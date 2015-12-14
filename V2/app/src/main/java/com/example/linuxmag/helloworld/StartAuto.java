package com.example.linuxmag.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * <h4>Start Auto de l'application</h4>
 *
 * @author fredericamps@gmail.com
 */
public class StartAuto extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ServiceLock.class);
        context.startService(i);
    }
}
