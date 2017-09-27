package com.dolan.dominic.dublinbikes.activities.journeys;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolan.dominic.dublinbikes.R;
import com.dolan.dominic.dublinbikes.activities.favourites.FavouritesList;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.data.FirebaseDataHelper;
import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.dolan.dominic.dublinbikes.objects.Journey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by domin on 25 Sep 2017.
 */

public class JourneysList extends ListFragment {

    private JourneysListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ListView listView = new ListView(getContext());
        return listView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseDataHelper firebase = new FirebaseDataHelper();
        if (getListView().getAdapter() == null) {
            adapter = new JourneysListAdapter(firebase.getJourneys());
            getListView().setAdapter(adapter);
        }
        else {
            adapter = ((JourneysListAdapter)getListView().getAdapter());
            adapter.setList(firebase.getJourneys());
        }

    }


    class JourneysListAdapter extends BaseAdapter {

        List<Journey> journeys;

        public JourneysListAdapter(List<Journey> journeys) {
            this.journeys = journeys;
        }

        public void remove(BikeStand stand){
            journeys.remove(stand);
            notifyDataSetChanged();
        }

        public void remove(int position){
            journeys.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return journeys.size();
        }

        @Override
        public Object getItem(int position) {
            return journeys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setList(List<Journey> journey){
            this.journeys = journey;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Journey journey = (Journey) getItem(position);

            String dateFormat = new SimpleDateFormat("EE - dd MMMM", Locale.ENGLISH).format(journey.startTime);
            String startTime = new SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(journey.startTime);
            String endTime = new SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(journey.endTime);

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.journey_list_item, null);

            final TextView titleView = (TextView) itemLayout.findViewById(R.id.txt_date);
            titleView.setText(dateFormat);

            final TextView startStationView = (TextView) itemLayout.findViewById(R.id.start_station);
            startStationView.setText("Start: " + journey.startStation + " at " + startTime );

            final TextView endStationView = (TextView) itemLayout.findViewById(R.id.end_station);
            endStationView.setText("End: " + journey.endStation + " at " + endTime );


            return itemLayout;
        }

    }
}
