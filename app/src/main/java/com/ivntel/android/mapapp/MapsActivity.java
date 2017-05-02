package com.ivntel.android.mapapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ivntel.android.mapapp.DatabaseHandler.ADDRESS_STRING;
import static com.ivntel.android.mapapp.DatabaseHandler.LATITUDE_VALUE;
import static com.ivntel.android.mapapp.DatabaseHandler.LOCATION_DESCRIPTION;
import static com.ivntel.android.mapapp.DatabaseHandler.LONGITUDE_VALUE;
import static com.ivntel.android.mapapp.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button;
    private Button delete;
    private Button share;
    private LatLng currentLatLng;
    private Marker currentMarker;

    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        button = (Button) findViewById(R.id.button);
        delete = (Button) findViewById(R.id.delete);
        share = (Button) findViewById(R.id.share);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
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

        // Add a marker in Sydney and move the camera
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context mContext = getApplicationContext();

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        ArrayList<HashMap<String, Object>> locationList = dbHandler.getLocationInfo();
        //array brought in from Database
        for (HashMap myLocation : locationList) {
            Double latitudeVal = (Double) myLocation.get(LATITUDE_VALUE);
            Double longitudeVal = (Double) myLocation.get(LONGITUDE_VALUE);
            String address = (String) myLocation.get(ADDRESS_STRING);
            String description = (String) myLocation.get(LOCATION_DESCRIPTION);


            LatLng location = new LatLng(latitudeVal, longitudeVal);
            mMap.addMarker(new MarkerOptions().position(location).title("Location Description").snippet("Address: " + address + "\n" + "Description: " + description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 2));//zoom level = 16 goes up to 21*/
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                currentMarker = arg0;
                currentLatLng = arg0.getPosition(); //
                delete.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                return true;
            }

        });
    }

    public void buttonOnClickLocation(View v) {
        Intent i = new Intent(MapsActivity.this, AddActivity.class);
        startActivity(i);
        }
    public void buttonOnClickDelete(View v) {
        Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show();
        double currentLatitude = currentLatLng.latitude;
        double currentLongitude = currentLatLng.longitude;
        boolean returnValue = dbHandler.deleteLocation(currentLatitude, currentLongitude);
        if(returnValue){
            currentMarker.remove();
        }
        else{
            Toast.makeText(this, "Not Deleted", Toast.LENGTH_LONG).show();
        }
    }
    public void buttonOnClickShare(View v) {
        Toast.makeText(this, "Share", Toast.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //sendIntent.putExtra(Intent.EXTRA_TEXT, "Address: " + currentMarker.getTitle() + "\n" + "Description: " + currentLatLng.);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
