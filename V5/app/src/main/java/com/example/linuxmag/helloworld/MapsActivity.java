package com.example.linuxmag.helloworld;

import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.linuxmag.helloworld.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * <h4>Classe Maps - Activity</h4>
 *
 * @author fredericamps@gmail.com
 *
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SmsMaps smsMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  Gestion des SMS entrants
      smsMaps = new SmsMaps(MapsActivity.this);

      // Recepteur local pour les intents
        this.registerReceiver(smsMaps, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setBuildingsEnabled(true);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng paris = new LatLng(48.844771, 2.317992);

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(paris).
                tilt(45).
                zoom(18).
                bearing(0).
                build();

        mMap.addMarker(new MarkerOptions().position(paris).title("myPhone"));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    /**
     *  Mise a jour de la carte suite a la reception d'un SMS
     * @param latitude
     * @param longitude
     */
    public void update(String latitude, String longitude)
    {
        LatLng myPos = new LatLng(Float.valueOf(latitude), Float.valueOf(longitude));

        // effecer les precedents marker
        mMap.clear();

        // afficher le marker
        mMap.addMarker(new MarkerOptions().position(myPos).title("my phone"));

        // positionnement et zoom
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myPos, 15);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    protected void onDestroy() {

        // on enleve le broadcast receiver
        if(smsMaps!=null)
            this.unregisterReceiver(smsMaps);

        super.onDestroy();
    }
}
