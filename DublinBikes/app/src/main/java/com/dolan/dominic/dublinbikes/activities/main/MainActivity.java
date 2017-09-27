package com.dolan.dominic.dublinbikes.activities.main;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.activities.favourites.FavouritesActivity;
import com.dolan.dominic.dublinbikes.activities.journeys.JourneysActivity;
import com.dolan.dominic.dublinbikes.activities.LoginActivity;
import com.dolan.dominic.dublinbikes.activities.SettingsActivity;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.dolan.dominic.dublinbikes.data.FirebaseDataHelper;
import com.dolan.dominic.dublinbikes.activities.main.infoPanel.InfoFragment;
import com.dolan.dominic.dublinbikes.data.LoadStationData;
import com.dolan.dominic.dublinbikes.LoginRequestBox;
import com.dolan.dominic.dublinbikes.R;
import com.dolan.dominic.dublinbikes.data.SQLDatabaseHelper;
import com.dolan.dominic.dublinbikes.data.StationSearch;
import com.dolan.dominic.dublinbikes.Units;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ViewTreeObserver.OnGlobalLayoutListener, OnConnectionFailedListener, View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String INFO_FRAGMENT_TAG = "infofragment";

    public static final String TAG = "DBikes";
    public static final int PERMISSION_REQUEST = 1;
    public GoogleMap map;

    // Create a LatLngBounds that includes Dublin.
    public static final LatLngBounds DUBLIN = new LatLngBounds(
            new LatLng(53.299863, -6.363746), new LatLng(53.392299, -6.170626));


    public boolean isFinishedLoadingJson = false;
    public boolean isFinishedLoadingMap = false;

    private InfoFragment infoFragment;
    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private TextView emailView;

    private boolean configurationChanged = true;
    boolean isLandscape = false;
    private boolean isKeyboardOpen;
    public FusedLocationProviderClient locationProviderClient;

    public SQLDatabaseHelper dbHelper;
    private boolean journeyIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Global.resources = getResources();
        Global.activity = this;

        Units.init(getWindowManager());

        setUpViews();

        requestDublinBikesAPI();
        startMapFragment();

        infoFragment = (InfoFragment) getFragmentManager().findFragmentByTag(INFO_FRAGMENT_TAG);

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        dbHelper = new SQLDatabaseHelper(this);
        Global.firebaseHelper = new FirebaseDataHelper();
        if (!Global.firebaseHelper.isLoggedIn()){
            sendToLoginOnFirstStart();
            emailView.setText(Global.sharedPref.getString("display_name", getString(R.string.not_logged_in)));
        } else {
            emailView.setText(Global.firebaseHelper.getName());
        }

        if (infoFragment == null) {
            infoFragment = new InfoFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.main_layout, infoFragment, INFO_FRAGMENT_TAG);
            transaction.commit();
        } else {
            infoFragment.setContainer((ViewGroup) findViewById(android.R.id.content));
        }
    }

    public List<BikeStand> getFavourites(){
        return Global.firebaseHelper.getFavourites();
    }

    private void setUpViews(){
        setContentView(R.layout.activity_main);
        boolean showFab = Global.sharedPref.getBoolean("show_menu", true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        if (!showFab){
            fab.hide();
        }


        emailView = (TextView) findViewById(R.id.email_address);

        String[] listActions = {"Favourites", "Journeys", "Settings"};
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, listActions));
        // Set the list's click listener
        drawerList.setOnItemClickListener(this);
    }

    private void sendToLoginOnFirstStart(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstStart = sharedPref.getBoolean("firstStart", true);
        if (firstStart){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstStart", false).apply();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void init(){
        LoadStationData dbTask = new LoadStationData(map, this, infoFragment);
        dbTask.execute();

        map.setOnMarkerClickListener(new OnMarkerClicked());
        map.getUiSettings().setMyLocationButtonEnabled(false);
        infoFragment.setMap(map);

        setCamera();

    }

    public void setCamera(){
        LatLng dublin = new LatLng(53.349722, -6.260278);
        float defaultZoom = 14f;

        map.moveCamera(CameraUpdateFactory.newLatLng(dublin));
        map.moveCamera(CameraUpdateFactory.zoomTo(defaultZoom));
    }

    public void setCamera(LatLng location){
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
        map.moveCamera(CameraUpdateFactory.zoomTo(16));
    }

    public boolean markerClicked(BikeStand bikeStand){

        infoFragment.clearStations();
        infoFragment.addStationInfo(bikeStand);
        return false;
    }

    public void startWhenReady(){
        if (isFinishedLoadingMap && isFinishedLoadingJson){
            enableLocation();
            init();
        }
    }


    private void requestDublinBikesAPI(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=01815f8823f3fa14a39fdaa976614f8279e6b97c";
        StringRequest request = getStringRequest(url);
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private StringRequest getStringRequest(String url){
        // Request a string response from the provided URL.
        return new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);
                        isFinishedLoadingJson = true;
                        startWhenReady();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dbAPIError();
            }
        });
    }

    private void parseJson(String response){
        response = "{array:" + response + "}";
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            Global.jsonObjects = obj.getJSONArray("array");
        } catch (JSONException e) {
            jsonError();
            e.printStackTrace();
        }
    }

    private void enableLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, PERMISSION_REQUEST);

        } else {
            map.setMyLocationEnabled(true);

            Task locationResult = locationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Global.lastKnownLocation = (Location) task.getResult();
                    }
                }
            });
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startWhenReady();
        }
    }

    public void jsonError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_parsing)
               .setTitle(R.string.error)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User clicked OK button
                   }
               });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dbAPIError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.disconnected_bikes)
               .setTitle(R.string.no_internet)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       loadStationsFromSQL();
                   }
               })
               .setNegativeButton(R.string.refresh, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       finish();
                       startActivity(getIntent());
                   }
               });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loadStationsFromSQL(){
        try {
            Global.bikeStandsList = dbHelper.getStationList();
        } catch (SQLException e){
            e.printStackTrace();
        }
        isFinishedLoadingJson = true;
        startWhenReady();
    }


    private void startMapFragment(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        isFinishedLoadingMap = true;
        startWhenReady();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isFinishing()) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().remove(infoFragment).commit();
        }
    }

    @Override
    public void onGlobalLayout() {

        ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        int contentHeight = contentView.getHeight();
        int screenHeight = isLandscape ? Units.width : Units.height;

        boolean keyboardChanged;
        boolean keyboardOld = isKeyboardOpen;

        isKeyboardOpen = contentHeight < screenHeight*0.75;
        keyboardChanged = keyboardOld != isKeyboardOpen;

        if ((keyboardChanged || configurationChanged) && infoFragment != null) {

            infoFragment.setContainer(contentView);
            boolean fragmentOutOfView = infoFragment.getY() > contentHeight - 70;
            if (!keyboardChanged || fragmentOutOfView){
                infoFragment.resetViewPosition(contentView);
            }
            configurationChanged = false;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.disconnected_maps)
               .setTitle(R.string.no_internet)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User clicked OK button
                   }
               });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == fab){
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    //Item click event for the list view in the naavigation drawer
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                if (Global.firebaseHelper.isLoggedIn()) {
                    startActivity(new Intent(this, FavouritesActivity.class));
                    return;
                }
                new LoginRequestBox().show();
                return;
            case 1:
                if (Global.firebaseHelper.isLoggedIn()) {
                    startActivity(new Intent(this, JourneysActivity.class));
                    return;
                }
                new LoginRequestBox().show();
                return;
            case 2:
                startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    private class OnMarkerClicked implements GoogleMap.OnMarkerClickListener{

        @Override
        public boolean onMarkerClick(Marker marker) {
            BikeStand stand = Global.bikeStands.get(marker.getId());
            return markerClicked(stand);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configurationChanged = true;
        isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (infoFragment.activeBikeStand != null) {
            menu.setHeaderTitle(infoFragment.activeBikeStand.getName());
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BikeStand activeStand = infoFragment.activeBikeStand;
        switch (item.getItemId()) {
            case R.id.favorite:
                addToFavourites(activeStand);
                return true;
            case R.id.take:
                takeBike(activeStand);
                return true;
            case R.id.put:
                putBike(activeStand);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void takeBike(BikeStand stand){
        if (Global.firebaseHelper.isLoggedIn()) {
            if (!journeyIsActive) {
                Global.firebaseHelper.startJourney(stand.getName(), new Date());
                Toast.makeText(getApplicationContext(), "Journey Started", Toast.LENGTH_LONG);
            } else {
                Toast.makeText(getApplicationContext(), "A Journey is already in progress", Toast.LENGTH_LONG);
            }
        } else {
            new LoginRequestBox().show();
        }
    }

    private void putBike(BikeStand stand){
        if (Global.firebaseHelper.isLoggedIn()) {
            if (journeyIsActive) {
                Global.firebaseHelper.finishJourney(stand.getName(), new Date());
                Toast.makeText(getApplicationContext(), "Journey Finished", Toast.LENGTH_LONG);
            } else {
                Toast.makeText(getApplicationContext(), "A Journey must be started before leaving back a bike", Toast.LENGTH_LONG);
            }
        } else {
            new LoginRequestBox().show();
        }
    }

    private void addToFavourites(BikeStand stand){
        if (Global.firebaseHelper.isLoggedIn()) {
            Global.firebaseHelper.addToFavourites(stand);
        } else {
            new LoginRequestBox().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
