package com.dolan.dominic.dublinbikes.data;

import android.util.Log;

import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.dolan.dominic.dublinbikes.objects.Journey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by domin on 24 Sep 2017.
 */

public class FirebaseDataHelper  implements ValueEventListener {
    private final String FAVOURITES = "favourites";
    private final String JOURNEYS = "journeys";
    private HashMap<String, BikeStand> bikeStands = new HashMap<>();

    private DatabaseReference journeydata;
    private DatabaseReference favouritesdata;
    private FirebaseUser user;

    private ArrayList<Journey> journeys = new ArrayList<>();
    private ArrayList<String> favourites = new ArrayList<>();

    private Journey currentJourney;

    public FirebaseDataHelper() {

        this.bikeStands = Global.bikeStands;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference firebasedata = FirebaseDatabase.getInstance().getReference().child(user.getUid());
            firebasedata.addValueEventListener(this);

            journeydata = firebasedata.child(JOURNEYS);
            favouritesdata = firebasedata.child(FAVOURITES);

        }

        Global.favourites = getFavourites();

    }

    public void startJourney(String startStation, Date startTime){
        currentJourney = new Journey(startStation, startTime);
    }

    public void finishJourney(String endStation, Date endTime){
        currentJourney.finishJourney(endStation, endTime);
        journeydata.child(endTime.toString()).setValue(currentJourney.endStation);
        currentJourney = null;
    }

    public boolean isJourneyInProgress(){
        return currentJourney != null && currentJourney.isInProgress();
    }

    public void addToFavourites(BikeStand station){
        if (user != null){
            favouritesdata.child(station.getName()).setValue(station.getID());
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> journeys = dataSnapshot.child(JOURNEYS).getChildren();
        Iterable<DataSnapshot> favourites = dataSnapshot.child(FAVOURITES).getChildren();

        for (DataSnapshot journey : journeys){
            this.journeys.add(journey.getValue(Journey.class));
        }

        for (DataSnapshot favourite: favourites) {
            this.favourites.add(favourite.getValue(String.class));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public Journey getJourney(int index) {
        return journeys.get(index);
    }

    public List<Journey> getJourneys(){
        return journeys;
    }

    public BikeStand getFavourite(int index) {
        return bikeStands.get(favourites.get(index));
    }

    public void removeFavourite(String id){
        favouritesdata.child(id).removeValue();
    }

    public int getFavouriteCount(){
        return favourites.size();
    }

    public ArrayList<BikeStand> getFavourites() {
        ArrayList<BikeStand> favourites = new ArrayList<>();
        for (String favourite : this.favourites) {
            favourites.add(bikeStands.get(favourite));
        }
        return favourites;
    }

    public boolean isLoggedIn(){
        return user != null;
    }

    public String getEmail(){
        return user.getEmail();
    }

    public String getName(){
        if (user.getDisplayName()==null || user.getDisplayName().isEmpty()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getEmail()).build();
            user.updateProfile(profileUpdates);
        }
        return user.getDisplayName();
    }
}
