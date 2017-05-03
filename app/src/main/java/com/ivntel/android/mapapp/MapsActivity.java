package com.ivntel.android.mapapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.ivntel.android.mapapp.DatabaseHandler.ADDRESS_STRING;
import static com.ivntel.android.mapapp.DatabaseHandler.LATITUDE_VALUE;
import static com.ivntel.android.mapapp.DatabaseHandler.LOCATION_DESCRIPTION;
import static com.ivntel.android.mapapp.DatabaseHandler.LONGITUDE_VALUE;
import static com.ivntel.android.mapapp.R.id.fab;
import static com.ivntel.android.mapapp.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLatLng;
    private Marker currentMarker;
    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    Bitmap screenShot;
    MarshmallowPermission marshMallowPermission;
    private static final String KEY_TEXT_VALUE = "textValue";
    //This is an example of how you would bring in string variables from the strings.xml folder
    //String mystring = getResources().getString(R.string.mystring);

    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Add Location", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MapsActivity.this, AddActivity.class);
                startActivity(i);
            }
        });
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

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        currentMarker = marker;
                        currentLatLng = marker.getPosition(); //
                        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.share);
                        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
                        delete.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);

                        /*int orientation = getApplicationContext().getResources().getConfiguration().orientation;
                        if(orientation == 0){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);*/
                        //return true;
                    }
                });
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.share);
                        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
                        delete.setVisibility(View.INVISIBLE);
                        share.setVisibility(View.INVISIBLE);

                        /*int orientation = getApplicationContext().getResources().getConfiguration().orientation;
                        if(orientation == 0 || orientation == 2){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);*/
                        return false;
                    }
                });
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
            mMap.addMarker(new MarkerOptions().position(location).title("Click For Delete/Share Prompts").snippet("Address: " + address + "\n" + "Description: " + description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 2));//zoom level = 16 goes up to 21*/
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng){
                FloatingActionButton share = (FloatingActionButton) findViewById(R.id.share);
                FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
                delete.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                /*int orientation = getApplicationContext().getResources().getConfiguration().orientation;
                if(orientation == 1 || orientation == 0){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }*/
            }
        });
    }

    public void buttonOnClickDelete(View v) {
        Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show();
        double currentLatitude = currentLatLng.latitude;
        double currentLongitude = currentLatLng.longitude;
        boolean returnValue = dbHandler.deleteLocation(currentLatitude, currentLongitude);
        if (returnValue) {
            currentMarker.remove();
        } else {
            Toast.makeText(this, "Not Deleted", Toast.LENGTH_LONG).show();
        }
    }

    public void buttonOnClickShare(View v) {
        Toast.makeText(this, "Share", Toast.LENGTH_LONG).show();
        marshMallowPermission = new MarshmallowPermission(this);

        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        } else {
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    try {
                        View mView = findViewById(android.R.id.content).getRootView();
                        mView.setDrawingCacheEnabled(true);
                        Bitmap backBitmap = mView.getDrawingCache();
                        Bitmap bmOverlay = Bitmap.createBitmap(
                                backBitmap.getWidth(), backBitmap.getHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmOverlay);
                        canvas.drawBitmap(backBitmap, 0, 0, null);
                        canvas.drawBitmap(snapshot, 0, 160, null);

                        File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/MapScreenShots");

                        Log.i("MyFile", "" + sampleDir);

                        // Created directory if doesn't exist
                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }
                        Date d = new Date();
                        File fn = new File(sampleDir + "/" + "Map" + d.getTime() + ".png");
                        FileOutputStream out = new FileOutputStream(fn);
                        bmOverlay.compress(Bitmap.CompressFormat.PNG, 100, out);

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");

                        File imageFileToShare = new File(fn.getAbsolutePath());

                        Uri uri = Uri.fromFile(imageFileToShare);
                        share.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                        share.putExtra(android.content.Intent.EXTRA_TEXT, "This is the Address and Description pertaining to this pin's location");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Screenshot Using:"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mMap.snapshot(callback);
        }
    }
}
        //sharing of just text
        //String address = "address";
        //String description = "description";

        /*Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Address: " + address + "\n" + "Description: " + description);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);*/


