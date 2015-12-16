package com.example.linuxmag.helloworld;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 *  Activity principale
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context myContext = this.getApplicationContext();

        // Champ numero telephone
        final EditText editTextPhone = (EditText)findViewById(R.id.editTextPhone);

        // Boutton de test
        Button buttonTest= (Button) findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Sms mySms = new Sms();
                mySms.sendSMS(editTextPhone.getText().toString(), "test SMS", myContext);
            }
        });

        // Recepteur de SMS
        SmsReceiver myReceiver = new  SmsReceiver();

        this.registerReceiver(myReceiver, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));

        // Logcat
        Log.i("TAG", "MESSAGE");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
