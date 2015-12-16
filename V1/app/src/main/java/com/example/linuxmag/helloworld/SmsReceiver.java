package com.example.linuxmag.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Reception des SMS
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Intent recieved: " + intent.getAction());

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
                    Log.i(TAG, "Message recieved: " + messages[0].getMessageBody());
                    Toast.makeText(context, messages[0].getMessageBody(), Toast.LENGTH_LONG).show();
                    checkSMS(messages[0].getMessageBody(), context);
                }
            }
        }
    }


    void checkSMS(String SMS, Context context)
    {
        // Stop la reception des SMS : ! Attention le systeme n'est plus pilotable !
        if(SMS.equals("STOP")) {

            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }
    }

}
