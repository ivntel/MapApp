package com.ivntel.android.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        address = (EditText) findViewById(R.id.address);
        description = (EditText) findViewById(R.id.description);
        addressText = (TextView) findViewById(R.id.addressText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
    }

    public void buttonOnClickEnterItem(View v) {
        address = (EditText) findViewById(R.id.address);
        addressText = (TextView) findViewById(R.id.addressText);
        addressText.setText(address.getText());
        mAddress = addressText.getText().toString();
    }

    public void buttonOnClickEnterDate(View v) {
        description = (EditText) findViewById(R.id.description);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        descriptionText.setText(description.getText());
        mDescription = descriptionText.getText().toString();
    }

    public void buttonOnClickLocation(View v) {
        if(mAddress != null && mDescription != null) {
            Toast.makeText(this, "All good", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Missing Information!", Toast.LENGTH_LONG).show();
        }
    }
}
