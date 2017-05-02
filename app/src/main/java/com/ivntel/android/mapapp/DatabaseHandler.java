package com.ivntel.android.mapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ivnte on 2017-04-30.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "locationDB";
    static final int DATABASE_VERSION = 1;
    static final String LOCATION_TABLE_NAME = "locationTable";

    static final String KEY_ID = "id";
    static final String LATITUDE_VALUE = "lat";
    static final String LONGITUDE_VALUE = "lng";
    static final String ADDRESS_STRING = "address";
    static final String LOCATION_DESCRIPTION = "desc";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DATABASE", "Inside on create method of DatabaseHandler");

        String CREATE_LOCATION_INFO_TABLE = "CREATE TABLE " + LOCATION_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + LATITUDE_VALUE + " DOUBLE,"
                + LONGITUDE_VALUE + " DOUBLE,"
                + ADDRESS_STRING + " TEXT,"
                + LOCATION_DESCRIPTION + " TEXT" + ")";


        db.execSQL(CREATE_LOCATION_INFO_TABLE);
        Log.d("DATABASE", "Successfully created table location info");


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing location and address in database
     *
     * @param
     *
     */
    public int addLocationInfo(Double latitude, Double longitude, String address, String description) {
        /*First check if same latitude, longitude combination already exists*/
        ArrayList<HashMap<String,Object>> locationList = getLocationInfo();

        for(HashMap<String, Object> locationInfo : locationList){
            Double latitudeVal = (Double) locationInfo.get(LATITUDE_VALUE);
            Double longitudeVal = (Double) locationInfo.get(LONGITUDE_VALUE);

            Log.d("DB", "Checking db value of latitude : " + latitude + " with new value : " + latitude);
            Log.d("DB", "Checking db value of longitude : " + longitude + " with new value : " + longitude);

            if((latitude.equals(latitudeVal) && (longitude.equals(longitudeVal)))){
                return 1;
            }
        }
        //if no duplicates found added to DB here
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DATABASE", "Called getWritableDatabase in addLocation Data Method ");

        ContentValues values = new ContentValues();

        values.put(LATITUDE_VALUE, latitude);
        values.put(LONGITUDE_VALUE, longitude);
        values.put(ADDRESS_STRING, address);
        values.put(LOCATION_DESCRIPTION, description);

        // Inserting Row
        db.insert(LOCATION_TABLE_NAME, null, values);
        db.close(); // Closing database connection

        return 2;
    }

    //Grabs all components of DB so that they can be displayed
    public ArrayList<HashMap<String,Object>> getLocationInfo() {
        ArrayList<HashMap<String,Object>> locationList = new ArrayList<HashMap<String,Object>>();
        String selectQuery = "SELECT * FROM " + LOCATION_TABLE_NAME + " ORDER BY ROWID";

        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("DATABASE", "Called getReadableDatabase in getLocationInfo method");
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                HashMap<String, Object> locationInfo = new HashMap<String, Object>();
                locationInfo.put(LATITUDE_VALUE, cursor.getDouble(cursor.getColumnIndex(LATITUDE_VALUE)));
                locationInfo.put(LONGITUDE_VALUE, cursor.getDouble(cursor.getColumnIndex(LONGITUDE_VALUE)));
                locationInfo.put(ADDRESS_STRING, cursor.getString(cursor.getColumnIndex(ADDRESS_STRING)));
                locationInfo.put(LOCATION_DESCRIPTION, cursor.getString(cursor.getColumnIndex(LOCATION_DESCRIPTION)));
                locationList.add(locationInfo);
            }
        } finally{
            cursor.close();
            cursor.close();
            db.close();
        }
        // return location info
        return locationList;
    }

    public boolean deleteLocation(double lat, double lng){
        SQLiteDatabase data = this.getWritableDatabase();
        String table = LOCATION_TABLE_NAME;
        String whereClause = LATITUDE_VALUE + "=? AND " + LONGITUDE_VALUE + "=?";
        String whereArgs[] = new String[] {String.valueOf(lat), String.valueOf(lng)};

        int count = data.delete(table, whereClause, whereArgs);
        data.close();

        if(count == 1){
            return true;
        }

        return false;
    }

    public void resetLocationEntry() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(LOCATION_TABLE_NAME, "", null);
        db.close();
    }
}
