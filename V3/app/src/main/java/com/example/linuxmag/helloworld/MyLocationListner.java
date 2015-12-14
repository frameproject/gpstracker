package com.example.linuxmag.helloworld;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

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

    MyLocationListener(ServiceLock _main, String myPhoneNumber)
    {
        main = _main;
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
            mySms.sendSMS(phoneNumber, "Long:"+location.getLongitude() + " " +"Lat:"+location.getLatitude(), main.getApplicationContext());

        } catch (IOException e) {
            Log.e(TAG, "Could not get Geocoder data", e);
        }
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


