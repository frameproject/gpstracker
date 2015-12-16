package com.example.linuxmag.helloworld;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 *  <h4>Service de gestion du systeme</h4>
 *
 * @author fredericamps@gmail.com
 */
public class ServiceLock extends Service {

    private static final String TAG = "ServiceLock";

    //SMS
    SmsReceiver myReceiver;
    BroadcastReceiver msgCom;


    // Data
    String phoneNumber;

    @Override
    public void onCreate() {

        initSMS();
        initBroadcast();

        Log.d(TAG, "Service started");
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        super.onCreate();
    }


    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

         // Redemarrage en cas d'arret
         return START_STICKY;
        }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(msgCom);
        unregisterReceiver(myReceiver);
        Log.d(TAG, "service done");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initBroadcast()
    {
        msgCom = new MsgCom();

        // Recepteur local pour les intents
        LocalBroadcastManager.getInstance(this).registerReceiver(msgCom,
                new IntentFilter("lockMyPhone"));
    }


    private void initSMS()
    {
        // Reception des SMS
        myReceiver = new  SmsReceiver();

        this.registerReceiver(myReceiver, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));
    }


     private class MsgCom extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), "lockMyPhone"))
            {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "Got message: " + message);

                if(message.equals("stopService"))
                {
                    ServiceLock.this.stopSelf();
                    Log.d(TAG, "Service stopped");
                }
            }
        }
    }


}
