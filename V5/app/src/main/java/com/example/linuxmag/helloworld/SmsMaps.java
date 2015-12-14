package com.example.linuxmag.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;


/**
 *  <h4>Classe reception des SMS et maj de la carte</h4>
 *
 * @author fredericamps@gmail.com
 *
 */
public class SmsMaps extends BroadcastReceiver
{
    MapsActivity myMap;

    SmsMaps(MapsActivity myMap)
    {
        this.myMap = myMap;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Bundle bundle = intent.getExtras();

            String info = intent.getStringExtra("format");

            if (bundle != null) {

                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], info);
                }

                if (messages.length > -1) {

                    Toast.makeText(context, messages[0].getMessageBody(), Toast.LENGTH_LONG).show();

                    // decoupage du sms
                    // Lat:x.xxxxx  Long:x.xxxxx
                    String[] separated = messages[0].getMessageBody().split(" ");

                    if(separated.length <2) { return ;}

                    String[] separated_info_1 = separated[0].split(":");
                    String[] separated_info_2 = separated[1].split(":");

                    if(separated_info_1.length <2 || separated_info_2.length <2) { return ;}

                    if(separated_info_1[0].equals("Lat") && separated_info_2[0].equals("Long")) {
                        // MAJ de la carte
                        myMap.update(separated_info_1[1], separated_info_2[1]);
                    }
                }
            }
        }
    }
}
