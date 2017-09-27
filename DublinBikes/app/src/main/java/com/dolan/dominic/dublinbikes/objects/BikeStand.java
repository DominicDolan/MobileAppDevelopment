package com.dolan.dominic.dublinbikes.objects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.R;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by domin on 30 Aug 2017.
 */

public class BikeStand {
    private static Paint paint;
    private static Bitmap dbIcon;

    private LatLng position;
    private String name;
    private boolean status;
    private int bikeStands;
    private int availableBikes;
    private int emptyBikeStands;
    private String markerID = "N/A";
    BikeStandMarker marker;

    public BikeStand(){

    }

    public BikeStand(JSONObject jsonObject) throws JSONException {
        if (paint == null || dbIcon == null){
            prepareIcon();
        }

        name = jsonObject.getString("name");

        double lat = jsonObject.getJSONObject("position").getDouble("lat");
        double lng = jsonObject.getJSONObject("position").getDouble("lng");
        position = new LatLng(lat, lng);

        String statusStr = jsonObject.getString("status");
        status = statusStr.contains("OPEN");

        availableBikes = jsonObject.getInt("available_bikes");
        emptyBikeStands = jsonObject.getInt("available_bike_stands");
        bikeStands = jsonObject.getInt("bike_stands");


        marker = new BikeStandMarker();

    }

    public BikeStand(String name, LatLng position, int availableBikes, int emptyBikeStands) {
        if (paint == null || dbIcon == null){
            prepareIcon();
        }

        this.position = position;
        this.name = name;
        this.availableBikes = availableBikes;
        this.emptyBikeStands = emptyBikeStands;

        status = true;
        bikeStands = availableBikes + emptyBikeStands;

        marker = new BikeStandMarker();
    }


    private void prepareIcon(){
        Bitmap fromResource = BitmapFactory.decodeResource(Global.resources, R.drawable.db_marker);
        float scale = 0.7f;
        int width = (int)(70*scale);
        int height = (int)(100*scale);
        dbIcon = Bitmap.createScaledBitmap(fromResource, width, height, false);

        // Create paint object and set up settings for it
        paint = new Paint();
        paint.setColor(Color.WHITE); // Text Color
        paint.setTextSize(30); // Text Size
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        fromResource.recycle();
    }

    public String addMarker(GoogleMap map){
        markerID = map.addMarker(marker.getMarkerOptions()).getId();
        return markerID;
    }

    public String getID() {
        return markerID;
    }

    public void setID(String id) {
        this.markerID = id;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public boolean status() {
        return status;
    }

    public int getBikeStands() {
        return bikeStands;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public int getEmptyBikeStands() {
        return emptyBikeStands;
    }

    public BikeStandMarker getMarker() {
        return marker;
    }

    public MarkerOptions getMarkerOptions() {
        return marker.getMarkerOptions();
    }

    private class BikeStandMarker {
        private Bitmap image;
        private MarkerOptions markerOptions;

        BikeStandMarker(){
            int width = dbIcon.getWidth();
            int height = dbIcon.getHeight();
            Bitmap icon = dbIcon.copy(Bitmap.Config.ARGB_8888, true);
            // Create a new canvas and set it to the new muteable bitmap
            Canvas canvas = new Canvas(icon);

            // Using the canvas draw on the mutable bitmap
            String iconText = "" + emptyBikeStands;;
            if (Global.showBikes){
                iconText = "" + availableBikes;
            }
            canvas.drawText(iconText, width/2, height/2, paint);
            markerOptions = new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(icon));
        }


        public Bitmap getImage() {
            return image;
        }

        public MarkerOptions getMarkerOptions() {
            return markerOptions;
        }


    }

}
