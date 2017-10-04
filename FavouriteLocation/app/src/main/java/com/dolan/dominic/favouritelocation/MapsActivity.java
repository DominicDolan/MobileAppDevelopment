package com.dolan.dominic.favouritelocation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap map;
    private HashMap<String, String> locations;
    private String selectedLocationName = "";
    Button selectLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        selectLocationButton = (Button) findViewById(R.id.selectLocation);
        selectLocationButton.setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locations = new HashMap<>();
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
        map = googleMap;
        setMarkers();
        // Add a marker in Sydney and move the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.515419f, -0.141099f), 5f));

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedLocationName = locations.get(marker.getId());
        return false;
    }

    private void setMarkers(){
        String name, id;
        LatLng position;

        name = "King's Landing";
        position = new LatLng(51.515419f, -0.141099f);
        id = map.addMarker(new MarkerOptions().position(position).title(name)).getId();
        locations.put(id, name);

        name = "High Garden";
        position = new LatLng(51.3801748f, -2.3995494f);
        id = map.addMarker(new MarkerOptions().position(position).title(name)).getId();
        locations.put(id, name);

        name = "Winterfell";
        position = new LatLng(53.9586419f, -1.115611f);
        id = map.addMarker(new MarkerOptions().position(position).title(name)).getId();
        locations.put(id, name);

        name = "The Wall";
        position = new LatLng(54.9899016, -2.6717341);
        id = map.addMarker(new MarkerOptions().position(position).title(name)).getId();
        locations.put(id, name);

        name = "Riverrun";
        position = new LatLng(52.477564, -2.003715);
        id = map.addMarker(new MarkerOptions().position(position).title(name)).getId();
        locations.put(id, name);
    }

    @Override
    public void onClick(View v) {
        if (v == selectLocationButton){
            if (selectedLocationName.isEmpty()){
                Toast.makeText(this, "Select a location first", Toast.LENGTH_LONG).show();
                return;
            }
            Intent result = new Intent();
            result.putExtra("location", selectedLocationName);
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }


}
