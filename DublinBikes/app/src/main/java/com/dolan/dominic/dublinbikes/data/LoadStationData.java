package com.dolan.dominic.dublinbikes.data;

import android.content.Context;
import android.os.AsyncTask;

import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.OnMarkersReadyListener;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by domin on 7 Sep 2017.
 */

//Load station data is an Async Task for loading all the BikeStand objects and putting the
//information into a SQLite database
public class LoadStationData extends AsyncTask<Void, Void, ArrayList<BikeStand>> {
    private final OnMarkersReadyListener markersReadyListener;
    private MainActivity context;
    private ArrayList<BikeStand> bikeStands = new ArrayList<>();
    private volatile boolean failed;
    private GoogleMap map;

    public LoadStationData(GoogleMap map, MainActivity context, OnMarkersReadyListener markersReadyListener) {
        super();
        this.markersReadyListener = markersReadyListener;
        this.context = context;
        this.map = map;
    }

    @Override
    protected ArrayList<BikeStand> doInBackground(Void... params) {
        populateList();
        updateOrInsertSQLStations();
        return bikeStands;
    }


    @Override
    protected void onPostExecute(ArrayList<BikeStand> list) {
        super.onPostExecute(list);
        if (!isFailed()) {
            Global.bikeStandsList = list;

            for(BikeStand bikeStand : Global.bikeStandsList) {
                bikeStand.addMarker(map);
                Global.bikeStands.put(bikeStand.getID(), bikeStand);
            }

            markersReadyListener.onResultsReturned(context);
        }

    }

    private void populateList() {
        try {
            if (Global.jsonObjects == null){
                throw new JSONException("JSON is Null");
            }
            for (int i = 0; i < Global.jsonObjects.length(); i++) {
                BikeStand stand = new BikeStand(Global.jsonObjects.getJSONObject(i));
                bikeStands.add(stand);
            }
        } catch (JSONException e) {
            setFailed(true);
            e.printStackTrace();
        }
    }

    public void updateOrInsertSQLStations(){
        boolean hasData = context.dbHelper.stationTableHasData();
        for(BikeStand bikeStand : Global.bikeStandsList) {
            if (hasData){
                context.dbHelper.updateStation(bikeStand);
            } else {
                context.dbHelper.insertStation(bikeStand);
            }
        }
    }

    private synchronized void setFailed(boolean failed){
        this.failed = failed;
    }

    private synchronized boolean isFailed() {
        return failed;
    }
}
