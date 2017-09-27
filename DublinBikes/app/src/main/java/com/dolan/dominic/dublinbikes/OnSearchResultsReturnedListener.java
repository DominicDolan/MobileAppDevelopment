package com.dolan.dominic.dublinbikes;

import com.dolan.dominic.dublinbikes.objects.BikeStand;

import java.util.ArrayList;

/**
 * Created by domin on 8 Sep 2017.
 */

public interface OnSearchResultsReturnedListener {
    void onResultsReturned(ArrayList<BikeStand> closeStations);
}
