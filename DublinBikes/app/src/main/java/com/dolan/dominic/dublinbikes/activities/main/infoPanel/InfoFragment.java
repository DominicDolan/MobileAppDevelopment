package com.dolan.dominic.dublinbikes.activities.main.infoPanel;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.OnMarkersReadyListener;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.dolan.dominic.dublinbikes.R;
import com.dolan.dominic.dublinbikes.data.StationSearch;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by domin on 30 Aug 2017.
 */

public class InfoFragment extends ListFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnMarkersReadyListener {
    private InfoPanel infoPanel;

    private InfoListAdapter adapter;
    private ViewGroup container;
    private GoogleMap map;
    public BikeStand activeBikeStand;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        infoPanel = (InfoPanel) inflater.inflate(R.layout.info_layout, container, false);
        setRetainInstance(true);
        return infoPanel;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new InfoListAdapter(getContext());

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
        getActivity().registerForContextMenu(getListView());

        infoPanel.onStart(adapter, container, (MainActivity) getActivity());
    }

    public void setMap(GoogleMap map){
        this.map = map;
        infoPanel.setMap(map);
    }

    public void addStationInfo(BikeStand bikeStand){

        adapter.add(bikeStand);
        infoPanel.animateListIntoView();
    }

    public void removeLastStationInfo(){

        if (adapter.getCount() != 0) {
            adapter.remove((BikeStand) adapter.getItem(adapter.getCount() - 1));
            infoPanel.updateLimitTop();
        }
    }

    public void clearStations(){
        adapter.clear();
    }

    public void setContainer(ViewGroup container) {
        this.container = container;
        if (infoPanel != null) {
            infoPanel.setContainer(container);
        }
    }


    public void resetViewPosition(ViewGroup container){
        if (infoPanel != null) {
            infoPanel.resetViewPosition(container);
        }
    }

    public float getY(){
        return infoPanel.getY();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activeBikeStand = adapter.getBikeStand(position);
        setCamera(activeBikeStand.getPosition());

        if (adapter.menuTouched) {
            adapter.menuTouched = false;
            getActivity().openContextMenu(getListView());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        activeBikeStand = adapter.getBikeStand(position);
        return false;
    }

    public void setCamera(LatLng location){
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        }
    }


    @Override
    public void onResultsReturned(Activity activity) {
        infoPanel.setStationSearch(new StationSearch(activity, Places.getGeoDataClient(getActivity(), null), Global.bikeStands));
    }
}
