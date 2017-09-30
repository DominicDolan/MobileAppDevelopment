package com.dolan.dominic.dublinbikes;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;

import com.dolan.dominic.dublinbikes.data.FirebaseDataHelper;
import com.dolan.dominic.dublinbikes.objects.BikeStand;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by domin on 25 Sep 2017.
 */

//List of variables that need to be accessed globally
public class Global {
    //Any object that holds the context cannot be placed here because it has the potential to cause a
    //memory leak
    public static HashMap<String, BikeStand> bikeStands = new HashMap<>();
    public static ArrayList<BikeStand> bikeStandsList = new ArrayList<>();
    public static ArrayList<BikeStand> favourites;
    public static Location lastKnownLocation;
    public static FirebaseDataHelper firebaseHelper;
    public static SharedPreferences sharedPref;
    public static boolean showBikes = true;
    public static JSONArray jsonObjects;
    public static Resources resources;
}
