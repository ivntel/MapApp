package com.ivntel.android.mapapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivnte on 2017-04-30.
 */
public class AddActivity extends AppCompatActivity {
    public static final double REQUEST_CODE = 123;
    public static final String LOCATION = "location";
    public static double location = 0;
    private EditText address;
    private EditText description;
    private TextView addressText;
    private TextView descriptionText;
    private String mAddress;
    private String mDescription;
    private DatabaseHandler dbHandler = new DatabaseHandler(this);
    public double lat;
    public double lng;
    private static final String KEY_TEXT_VALUE1 = "textValue1";
    private static final String KEY_TEXT_VALUE2 = "textValue2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        address = (EditText) findViewById(R.id.address);
        description = (EditText) findViewById(R.id.description);
        addressText = (TextView) findViewById(R.id.addressText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);

        if (savedInstanceState != null) {
            CharSequence savedText1 = savedInstanceState.getCharSequence(KEY_TEXT_VALUE1);
            CharSequence savedText2 = savedInstanceState.getCharSequence(KEY_TEXT_VALUE2);
            addressText.setText(savedText1);
            descriptionText.setText(savedText2);
        }
    }
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE1, addressText.getText());
        outState.putCharSequence(KEY_TEXT_VALUE2, descriptionText.getText());
    }

    public void buttonOnClickEnterAddress(View v) throws IOException {
        address = (EditText) findViewById(R.id.address);
        addressText = (TextView) findViewById(R.id.addressText);
        addressText.setText(address.getText());
        mAddress = addressText.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(mAddress, 1);
        if(list.size() > 0){
            Address add = list.get(0);
            lat = add.getLatitude();
            lng = add.getLongitude();
        }
        else {
            Toast.makeText(this, "Invalid address", Toast.LENGTH_LONG).show();
            addressText.setText("Reinsert Address");
        }
    }

    public void buttonOnClickEnterDescription(View v) {
        description = (EditText) findViewById(R.id.description);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        descriptionText.setText(description.getText());
        mDescription = descriptionText.getText().toString();
    }

    public void buttonOnClickLocation(View v) {

        if(mAddress != null && mDescription != null) {
            Log.d("Value", "mAddress: " + mAddress + " mDescription: " + mDescription + " lat: " + lat + " lng: " + lng);
            int returnValue = dbHandler.addLocationInfo(lat, lng, mAddress, mDescription);

            if(returnValue == 1){
                Toast.makeText(this, "Duplicate Address", Toast.LENGTH_LONG).show();
                addressText.setText("Reinsert Address");
            }
            else if(returnValue == 2){
                Toast.makeText(this, "Address Submitted", Toast.LENGTH_LONG).show();
                Intent i = new Intent(AddActivity.this, MapsActivity.class);
                startActivity(i);
            }
        }
        else{
            Toast.makeText(this, "Missing Information!", Toast.LENGTH_LONG).show();
        }
    }
}
