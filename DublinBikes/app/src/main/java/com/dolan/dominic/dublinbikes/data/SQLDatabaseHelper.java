package com.dolan.dominic.dublinbikes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by domin on 12 Sep 2017.
 */

public class SQLDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "DublinBikes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String STATION_TABLE = "bike_station";
    private static final String NAME_COL = "name";
    private static final String LAT_COL = "latitude";
    private static final String LONG_COL = "longitude";
    private static final String BIKES_AVAIL_COL = "bikes_available";
    private static final String SPACES_AVAIL_COL = "spaces_available";
    private static final String SQL_CREATE_STATIONS_TABLE =
            "create table "+ STATION_TABLE +" (" +
                    NAME_COL         + " TEXT PRIMARY KEY," +
                    LAT_COL          + " REAL," +
                    LONG_COL         + " REAL," +
                    BIKES_AVAIL_COL  + " INT," +
                    SPACES_AVAIL_COL + " INT" +
                    ")";

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MainActivity.TAG, "OnCreate DB");
        db.execSQL("DROP TABLE IF EXISTS " + STATION_TABLE);
        db.execSQL(SQL_CREATE_STATIONS_TABLE);

    }

    public boolean insertStation(String name, LatLng location, int bikes, int spaces) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL, name);
        contentValues.put(LAT_COL, location.latitude);
        contentValues.put(LONG_COL, location.longitude);
        contentValues.put(BIKES_AVAIL_COL, bikes);
        contentValues.put(SPACES_AVAIL_COL, spaces);

        long result = -1;
        try {
            result = db.insertOrThrow(STATION_TABLE, null ,contentValues);

        } catch (SQLException e){

            Log.e(MainActivity.TAG, "Error, Station Name: " + name);
            e.printStackTrace();
        }
        return result != -1;
    }

    public boolean insertStation(BikeStand station){
        return insertStation(
                station.getName(),
                station.getPosition(),
                station.getAvailableBikes(),
                station.getEmptyBikeStands()
        );
    }

    public boolean stationTableHasData(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+STATION_TABLE+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean updateStation(String name, LatLng location, int bikes, int spaces) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(LAT_COL, location.latitude);
        contentValues.put(LONG_COL, location.longitude);
        contentValues.put(BIKES_AVAIL_COL, bikes);
        contentValues.put(SPACES_AVAIL_COL, spaces);

        db.update(STATION_TABLE, contentValues, "NAME = ?",new String[] { name });
        return true;
    }

    public boolean updateStation(BikeStand station){
        return updateStation(
                station.getName(),
                station.getPosition(),
                station.getAvailableBikes(),
                station.getEmptyBikeStands()
        );
    }

    public Cursor getAllStations() {
        SQLiteDatabase db = this.getWritableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, STATION_TABLE);

        return db.rawQuery("select * from "+STATION_TABLE,null);
    }

    public ArrayList<BikeStand> getStationList(){
        ArrayList<BikeStand> bikeStands = new ArrayList<>();
        Cursor cursor = getAllStations();
        cursor.moveToFirst();
        try {
            while (cursor.moveToNext()) {
                int latCol = cursor.getColumnIndex(LAT_COL);
                int longCol = cursor.getColumnIndex(LONG_COL);
                int nameCol = cursor.getColumnIndex(NAME_COL);
                int bikesCol = cursor.getColumnIndex(BIKES_AVAIL_COL);
                int spacesCol = cursor.getColumnIndex(SPACES_AVAIL_COL);

                LatLng location = new LatLng(cursor.getDouble(latCol), cursor.getDouble(longCol));
                int bikes = cursor.getInt(bikesCol);
                int spaces = cursor.getInt(spacesCol);

                BikeStand stand = new BikeStand(cursor.getString(nameCol), location, bikes, spaces);
                bikeStands.add(stand);
            }
        } finally {
            cursor.close();
        }
        return bikeStands;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STATION_TABLE);
        onCreate(db);
    }

    public void close(){
        super.close();
        getWritableDatabase().close();
    }

}
