package com.dolan.dominic.dublinbikes.activities.main.infoPanel;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.OnMarkersReadyListener;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.data.LoadStationData;
import com.dolan.dominic.dublinbikes.data.StationSearch;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.dolan.dominic.dublinbikes.LoginRequestBox;
import com.dolan.dominic.dublinbikes.OnSearchResultsReturnedListener;
import com.dolan.dominic.dublinbikes.R;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.dolan.dominic.dublinbikes.Units.dp;
import static com.dolan.dominic.dublinbikes.Units.height;

/**
 * Created by domin on 7 Sep 2017.
 */

public class InfoPanel extends RelativeLayout implements View.OnTouchListener, View.OnClickListener,
        SearchView.OnQueryTextListener, RadioGroup.OnCheckedChangeListener, OnSearchResultsReturnedListener, OnMarkersReadyListener {

    //InfoPanel is the view which is contained within the info fragment

    private int touchSlop;
    private int minFlingVelocity;
    private int maxFlingVelocity;

    private GestureDetectorCompat detector;

    private int containerHeight;
    private InfoListAdapter adapter;

    private int headerHeight;

    private int limitTop = (int)(0.75f*height);
    private int limitBottom = 100;

    private int lowerPosition = limitBottom;
    private int upperPosition = limitTop;
    private GoogleMap map;
    private StationSearch stationSearch;
    private MainActivity activity;


    private void init(){
        ViewConfiguration vc = ViewConfiguration.get(this.getContext());
        touchSlop = vc.getScaledTouchSlop();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity();
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();

        this.setOnTouchListener(this);
        detector = new GestureDetectorCompat(getContext(), new MyGestureListener());

    }

    //This onStart is not an override, it is a method that needs to be called when there is a
    //configuration change
    public void onStart(InfoListAdapter adapter, ViewGroup container, MainActivity activity){
        this.activity = activity;
        this.adapter = adapter;

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(this);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(R.id.radioButton);

        ImageView myLocation = (ImageView) findViewById(R.id.my_location);
        myLocation.setOnClickListener(this);

        ImageView favorites = (ImageView) findViewById(R.id.favorite);
        favorites.setOnClickListener(this);

        // the variable dp is defined in the Units class and it has the same effect as dp in XML
        headerHeight = (int) ((48 + 36)*dp);
        limitTop = headerHeight;

        containerHeight = container.getHeight();

        headerHeight = (int) ((48 + 36)*dp);
        setY( container.getHeight() - headerHeight);

    }


    //Since this class is instantiated from XML it is good practice to implement all the constructors
    public InfoPanel(Context context) {
        super(context);
        init();
    }

    public InfoPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public InfoPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void moveView(float newY){
        updateLimitTop();

        boolean tooHigh = newY < containerHeight - limitTop;
        boolean tooLow = newY > containerHeight - limitBottom;

        if (tooHigh){
            newY = containerHeight - limitTop + 1;

        }else if (tooLow){
            newY = containerHeight - limitBottom;
        }
        setY(newY);
    }

    private void animateView(float position){
        updateLimitTop();
        float oldY = getY();
        moveView(position);

        TranslateAnimation anim = new TranslateAnimation(
                Animation.ABSOLUTE, 0f,
                Animation.ABSOLUTE, 0f,
                Animation.ABSOLUTE, -getY() + oldY,
                Animation.ABSOLUTE, 0 );

        anim.setDuration(500);
        anim.setFillAfter( true );
        this.startAnimation(anim);
    }

    public void animateListIntoView(){
        animateView(containerHeight - adapter.getListHeight() - headerHeight - 10*dp);
    }

    public void animateViewUp(){
        animateView(containerHeight - upperPosition);
    }

    public void animateViewDown(){
        animateView(containerHeight - limitBottom);
    }

    public void updateLimitTop(){
        int listLimit = adapter.getListHeight() + headerHeight + (int)(10*dp);

        limitTop = Math.max(listLimit, upperPosition);
    }

    public void setContainer(ViewGroup container) {
        containerHeight = container.getHeight();
    }

    public void resetViewPosition(ViewGroup container){
        moveView( container.getHeight() - headerHeight);
    }

    //onInterceptTouchEvent is for ensuring that the user can scroll the current view up and down
    //while also being able to interact properly with the controls within this view.
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            return false; // Do not intercept touch event, let the child handle it
        }
        //Only return true when the SimpleGestureListener returns true
        return detector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.my_location){
            LatLng deviceLocation = new LatLng(
                    Global.lastKnownLocation.getLatitude(),
                    Global.lastKnownLocation.getLongitude());
            stationSearch.getLocation(deviceLocation, this);
            map.animateCamera(CameraUpdateFactory.newLatLng(deviceLocation));
        }
        else if (v.getId() == R.id.favorite){
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                adapter.clear();
                for (BikeStand bikeStand : Global.favourites) {
                    adapter.add(bikeStand);
                }
            } else {
                new LoginRequestBox().show();
            }
        }
        else {
            animateViewUp();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String searchString) {
        if (stationSearch != null){
            stationSearch.getLocation(searchString, this);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchString) {
        if (searchString.length() > 3 && stationSearch != null){
            stationSearch.getLocation(searchString, this);
        }
        return false;
    }

    @Override
    public void onResultsReturned(ArrayList<BikeStand> closeStations) {
        adapter.clear();
        for (BikeStand bikeStand : closeStations) {
            adapter.add(bikeStand);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        Global.showBikes = checkedId == R.id.radioButton;
        refreshMarkers();
    }

    private void refreshMarkers(){
        if (map != null && Global.bikeStands != null) {
            map.clear();
            Global.bikeStands.clear();
            LoadStationData loader = new LoadStationData(map, activity, this);
            loader.execute();
        }
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void setStationSearch(StationSearch stationSearch) {
        this.stationSearch = stationSearch;
    }

    @Override
    public void onResultsReturned(Activity activity) {
        this.stationSearch = new StationSearch(activity, Places.getGeoDataClient(getContext(), null), Global.bikeStands);
    }

    //SimpleOnGestureListener is an easy way to make use of the most common gestures in Android
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            int startY = (int) e1.getRawY();
            int currentY = (int) e2.getRawY();
            int difference = currentY - startY;
            int viewStart = (int)(e1.getRawY() - e1.getY());
            moveView(viewStart + difference);

            return Math.abs(difference) > touchSlop;
        }


        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            //fling the whole view up or down based on the fling animation
            if (velocityY > minFlingVelocity && velocityY < maxFlingVelocity){
                animateViewDown();
            } else if (-velocityY > minFlingVelocity && -velocityY < maxFlingVelocity){
                animateViewUp();
            }
            return true;
        }
    }
}
