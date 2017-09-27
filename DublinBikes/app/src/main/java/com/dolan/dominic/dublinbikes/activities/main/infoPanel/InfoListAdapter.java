package com.dolan.dominic.dublinbikes.activities.main.infoPanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolan.dominic.dublinbikes.objects.BikeStand;
import com.dolan.dominic.dublinbikes.R;

import java.util.ArrayList;
import java.util.List;

import static com.dolan.dominic.dublinbikes.Units.dp;

/**
 * Created by domin on 1 Sep 2017.
 */

public class InfoListAdapter extends BaseAdapter implements View.OnTouchListener {

    private final List<BikeStand> items = new ArrayList<BikeStand>();
    private final Context context;
    private int listHeight;

    public boolean menuTouched = false;
    public ImageView activeView;


    private int listItemHeight = (int) ((24 + 32)*dp);


    public InfoListAdapter(Context context){
        this.context = context;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        listHeight = listItemHeight*items.size();
    }

    public void add(BikeStand item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void remove(BikeStand item){
        items.remove(item);
        notifyDataSetChanged();
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    public BikeStand getBikeStand(int position){
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getListHeight() {
        return listHeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BikeStand bikeStand = (BikeStand) getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.station_list_item, null);

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.station_title);
        titleView.setText(bikeStand.getName());

        final TextView statusView = (TextView) itemLayout.findViewById(R.id.station_status);
        statusView.setText(bikeStand.status()? "OPEN" : "CLOSED");

        final TextView bikesView = (TextView) itemLayout.findViewById(R.id.station_bikes);
        bikesView.setText(bikeStand.getAvailableBikes() + " bikes available");

        final TextView spacesView = (TextView) itemLayout.findViewById(R.id.station_spaces);
        spacesView.setText(bikeStand.getEmptyBikeStands() + " spaces available");

        final ImageView menu = (ImageView) itemLayout.findViewById(R.id.menuView);
        menu.setOnTouchListener(this);

        return itemLayout;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        menuTouched = true;
        activeView = (ImageView) v;
        return false;
    }
}
