package com.dolan.dominic.dublinbikes.activities.favourites;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolan.dominic.dublinbikes.Global;
import com.dolan.dominic.dublinbikes.R;
import com.dolan.dominic.dublinbikes.activities.LoginActivity;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.dolan.dominic.dublinbikes.data.FirebaseDataHelper;
import com.dolan.dominic.dublinbikes.objects.BikeStand;

import java.util.List;

/**
 * Created by domin on 25 Sep 2017.
 */

public class FavouritesList extends ListFragment implements AdapterView.OnItemClickListener {
    //List adapter fo the favourites list. It uses the same item view as the adapter in the info panel
    private FavouritesListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new ListView(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getListView().getAdapter() == null) {
            adapter = new FavouritesListAdapter(Global.favourites);
            getListView().setAdapter(adapter);
        }
        else {
            adapter = ((FavouritesListAdapter)getListView().getAdapter());
            adapter.setList(Global.favourites);
        }

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int itemPosition = position;
        final String selectedBikeID = ((BikeStand)adapter.getItem(position)).getName();

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.remove_favourite_box)
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(itemPosition);
                        FirebaseDataHelper firebase = new FirebaseDataHelper();
                        firebase.removeFavourite(selectedBikeID);
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {  }
                })
                .create().show();

    }

    private class FavouritesListAdapter extends BaseAdapter{

        List<BikeStand> bikeStands;

        FavouritesListAdapter(List<BikeStand> bikeStands) {
            this.bikeStands = bikeStands;
        }

        public void remove(BikeStand stand){
            bikeStands.remove(stand);
            notifyDataSetChanged();
        }

        void remove(int position){
            bikeStands.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return bikeStands.size();
        }

        @Override
        public Object getItem(int position) {
            return bikeStands.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setList(List<BikeStand> bikeStands){
            this.bikeStands = bikeStands;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final BikeStand bikeStand = (BikeStand) getItem(position);

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.station_list_item, null);

            final TextView titleView = (TextView) itemLayout.findViewById(R.id.station_title);
            titleView.setText(bikeStand.getName());

            final TextView statusView = (TextView) itemLayout.findViewById(R.id.station_status);
            statusView.setText("");

            final TextView bikesView = (TextView) itemLayout.findViewById(R.id.station_bikes);
            bikesView.setText(bikeStand.getAvailableBikes() + " bikes available");

            final TextView spacesView = (TextView) itemLayout.findViewById(R.id.station_spaces);
            spacesView.setText(bikeStand.getEmptyBikeStands() + " spaces available");

            final ImageView menu = (ImageView) itemLayout.findViewById(R.id.menuView);
            menu.invalidate();

            return itemLayout;
        }

    }

}
