package com.dolan.dominic.dublinbikes.data;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dolan.dominic.dublinbikes.OnSearchResultsReturnedListener;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by domin on 8 Sep 2017.
 */

public class StationSearch {
    //This is the class for searching through the stations and matching them to a text screen
    //This is described in quite a bit of detail in the report.
    private final Activity activity;
    private AutocompleteFilter placeFilter;
    private GeoDataClient geoDataClient;
//    private Activity activity;
    private Context context;
    private AutoCompleteListener autoCompleteListener;
    private PlaceOnCompleteListener placeOnCompleteListener;
    private OnSearchResultsReturnedListener listener;

    private HashMap<String, BikeStand> bikeStands;
    private ArrayList<StationSort> latitudeSorted;
    private ArrayList<StationSort> longitudeSorted;

    private String searchString;

    public StationSearch(Activity activity, GeoDataClient geoDataClient, HashMap<String, BikeStand> bikeStands) {
        this.activity = activity;
        context = geoDataClient.getApplicationContext();
        this.geoDataClient = geoDataClient;
        this.bikeStands = bikeStands;

        autoCompleteListener = new AutoCompleteListener();
        placeOnCompleteListener = new PlaceOnCompleteListener();

        placeFilter = new AutocompleteFilter.Builder()
                .setCountry("IE")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                .build();

        latitudeSorted = new ArrayList<>();
        longitudeSorted = new ArrayList<>();

        for (Map.Entry<String, BikeStand> entry : bikeStands.entrySet()) {
            String id = entry.getKey();
            BikeStand bike = entry.getValue();

            latitudeSorted.add(new StationSort(id, bike.getPosition().latitude));
            longitudeSorted.add(new StationSort(id, bike.getPosition().longitude));

        }

        Collections.sort(latitudeSorted);
        Collections.sort(longitudeSorted);

    }

    public void getLocation(String text, OnSearchResultsReturnedListener listener) {
        this.listener = listener;
        searchString = text;
        Task<AutocompletePredictionBufferResponse> results =
                geoDataClient.getAutocompletePredictions(text, MainActivity.DUBLIN, placeFilter);
        results.addOnCompleteListener(activity, autoCompleteListener);
    }

    public void getLocation(LatLng latLng, OnSearchResultsReturnedListener listener) {
        listener.onResultsReturned(getCloseStations(latLng, "\0"));
    }

    private void getNameMatchedStations(String text, ArrayList<BikeStand> closeStations){

        for(Map.Entry<String, BikeStand> entry : bikeStands.entrySet()) {
            String name = entry.getValue().getName();
            String id = entry.getKey();

            if (name.toLowerCase(Locale.ENGLISH).contains(text)
                    && !checkAlreadyListed(id, closeStations)){
                closeStations.add(entry.getValue());
            }
        }
    }

    private ArrayList<BikeStand> getCloseStations(LatLng latLng, String text){
        ArrayList<StationSort> closeStationsLat = new ArrayList<>();
        ArrayList<StationSort> closeStationsLng = new ArrayList<>();

        searchSortedList(latLng.latitude, latitudeSorted, closeStationsLat);
        searchSortedList(latLng.longitude, longitudeSorted, closeStationsLng);

        return getMatches(closeStationsLat, closeStationsLng, text);
    }

    private boolean checkAlreadyListed(String markerID, ArrayList<BikeStand> closeStations){
        for (BikeStand stand : closeStations) {
            if (stand.getID().equals(markerID)){
                return true;
            }
        }
        return false;
    }

    private ArrayList<BikeStand> getMatches(ArrayList<StationSort> closeStationsLat,
                                            ArrayList<StationSort> closeStationsLng,
                                            String text){
        ArrayList<BikeStand> closeStations = new ArrayList<>();
        getNameMatchedStations(text, closeStations);

        //Cross reference the two close stations list
        for (StationSort stationLat : closeStationsLat) {
            String idLat = stationLat.markerID;
            for (StationSort stationLng : closeStationsLng) {
                if (idLat.equals(stationLng.markerID) && !checkAlreadyListed(idLat, closeStations)) {
                    closeStations.add(bikeStands.get(idLat));
                }
            }
        }

        return closeStations;
    }

    //Method for getting the closest stations in terms of either latitude or longitude
    private void searchSortedList(double value, ArrayList<StationSort> stationList, ArrayList<StationSort> closeStations){
        int index = search(value, stationList);
        closeStations.add(stationList.get(index));

        for (int i = 1; i <= 10; i++) {
            int indexLo = index - i;
            int indexHi = index + i;

            if (indexLo > 0){
                StationSort station = stationList.get(indexLo);
                closeStations.add(station);
            }
            if (indexHi < stationList.size()-1){
                StationSort station = stationList.get(indexHi);
                closeStations.add(station);
            }
        }

    }

    //A modified form of the Arrays.binarSearch() algorithm
    private int search(double value, ArrayList<StationSort> list) {

        if(value < list.get(0).location) {
            return 0;
        }
        if(value > list.get(list.size()-1).location) {
            return list.size()-1;
        }

        int lo = 0;
        int hi = list.size() - 1;

        while (lo <= hi) {
            int mid = (hi + lo) / 2;
            double location = list.get(mid).location;
            if (value < location) {
                hi = mid - 1;
            } else if (value > location) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        // lo == hi + 1
        return (list.get(lo).location - value) < (value - list.get(lo).location) ? lo : hi;
    }

    private class AutoCompleteListener implements OnCompleteListener<AutocompletePredictionBufferResponse> {

        @Override
        public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
            AutocompletePrediction result = task.getResult().get(0);
            Task<PlaceBufferResponse> results = geoDataClient.getPlaceById(result.getPlaceId());
            results.addOnCompleteListener(activity, placeOnCompleteListener);

        }
    }

    private class PlaceOnCompleteListener implements OnCompleteListener<PlaceBufferResponse> {

        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            Place result = task.getResult().get(0);
            listener.onResultsReturned(getCloseStations(result.getLatLng(), searchString));
        }
    }

    private class StationSort implements Comparable<StationSort>{
        String markerID;
        double location;

        public StationSort(String markerID, double location){
            this.markerID = markerID;
            this.location = location;
        }

        @Override
        public int compareTo(@NonNull StationSort otherStation) {
            int latitudeCompare = (int) (location*10000);
            int otherLatitudeCompare = (int) (otherStation.location*10000);
            return latitudeCompare - otherLatitudeCompare;
        }
    }

}
