package com.example.linuxmag.helloworld;

import android.content.ContentValues;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.linuxmag.helloworld.dataprovider.LogsProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <h4>Location listener</h4>
 *
 * @author fredericamps@gmail.com
 */
class MyLocationListener implements LocationListener {

    private static final String TAG = "LocationListener";
    Geocoder geocoder;
    ServiceLock main;

    String phoneNumber;

    MyLocationListener(ServiceLock main, String myPhoneNumber)
    {
        this.main = main;
        phoneNumber = myPhoneNumber;
        geocoder = new Geocoder(main);
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged with location " + location.toString());

        String text = String.format("Latitude:\t %f\nLongitude:\t %f\n", location.getLatitude(),location.getLongitude());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            for (Address address : addresses) {
                text += "  " + address.getAddressLine(0);
            }
            Log.d(TAG, "address with location " + text);

            SmsSender mySms = new SmsSender();
            mySms.sendSMS(phoneNumber, "Lat:"+location.getLatitude() + " " +"Long:"+location.getLongitude(), main.getApplicationContext());

            if(ManageSettings.isRecord)
            {
                // ajoute un log dans la table logs
                ContentValues values = new ContentValues();
                values.put(LogsProvider.DATE, getDateTime());
                values.put(LogsProvider.APP, TAG);
                values.put(LogsProvider.INFO, "Long:"+location.getLatitude() +" " +"Lat:"+location.getLongitude());

                Uri uri = main.getContentResolver().insert(
                        LogsProvider.CONTENT_URI, values);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not get Geocoder data", e);
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(main, provider + "'Status changed to " + status + "!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(main, "Provider " + provider + " enabled!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(main, "Provider " + provider + " disabled!",
                Toast.LENGTH_SHORT).show();
    }
}


