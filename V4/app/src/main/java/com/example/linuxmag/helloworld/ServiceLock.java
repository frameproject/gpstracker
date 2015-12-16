package com.example.linuxmag.helloworld;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
 *
 */
public class ServiceLock extends Service {

    private static final String TAG = "ServiceLock";

    //SMS
    SmsReceiver myReceiver;
    BroadcastReceiver msgCom;

    // GPS
    LocationManager locationManager=null;
    private String provider;
    private MyLocationListener mylistener;

    // Data
    String phoneNumber;


    @Override
    public void onCreate() {

        initData();
        initSMS();
        initBroadcast();

        Toast.makeText(this, "ServiceLock --> Service started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Service started");

        super.onCreate();
    }


    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
           // Redemarrage en cas de d'arret
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


    private void initGPS()
    {
        // le gestionnaire de position
        if(locationManager==null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Definition du critere pour selectionner le fournisseur de position le plus precis
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            // renvoie le fournisseur disponible
            provider = locationManager.getBestProvider(criteria, true);

            Log.d(TAG, "provider = " + provider);
        }
        if(provider!=null) {

            // Derniere position connue par le provider
            Location location = locationManager.getLastKnownLocation(provider);
            mylistener = new MyLocationListener(this, phoneNumber);

            if (location != null) {
                mylistener.onLocationChanged(location);
            }

            // conditions de mise a jour de la position : au moins 10 metres et 5000 millsecs
            locationManager.requestLocationUpdates(provider, 15000, 10, mylistener);
        }
    }


    private void stopGPS()
    {
        if(locationManager!=null && mylistener!=null)
             locationManager.removeUpdates(mylistener);

        mylistener=null;
    }


    private void initData()
    {
        // chargement des parametres
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String, String> data = new HashMap<String, String>();
        loadMyData.restoreData(this.getApplicationContext(), data);

        ManageSettings.isRecord = Boolean.valueOf(data.get("RECORD"));
        phoneNumber = data.get("TEL");
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
                    stopGPS();
                    ServiceLock.this.stopSelf();
                    Log.d(TAG, "Service stopped");
                }
                else if(message.equals("startGPS"))
                {
                    initGPS();
                    Log.d(TAG, "GSP tracking started");
                }
                else if(message.equals("stopGPS")) {
                    stopGPS();
                    Log.d(TAG, "GSP tracking stopped");
                }
                else if(message.equals("startLOG"))
                {
                    ManageSettings.isRecord=true;
                    Log.d(TAG, "Start log");
                }
                else if(message.equals("stopLOG"))
                {
                    ManageSettings.isRecord=false;
                    Log.d(TAG, "Stop log");
                }
            }
        }
    }
}
