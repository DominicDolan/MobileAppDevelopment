package com.dolan.dominic.dublinbikes.objects;

import java.util.Date;

/**
 * Created by domin on 24 Sep 2017.
 */

public class Journey {
    public String startStation;
    public String endStation;
    public Date startTime;
    public Date endTime;
    private boolean inProgress = false;

    public Journey() {

    }

    public Journey(String startStation, String endStation, Date startTime, Date endTime) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Journey(String startStation, Date startTime) {
        this.startStation = startStation;
        this.startTime = startTime;
        inProgress = true;
    }

    public void finishJourney(String endStation, Date endTime){
        inProgress = false;
        this.endStation = endStation;
        this.endTime = endTime;
    }

    public boolean isInProgress() {
        return inProgress;
    }
}
