package com.dolan.dominic.favouritelocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    int PLACE_PICKER_REQUEST = 1;
    private static final String TAG = "MapLocation";
    private Button mapButton;
    private TextView favouriteLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favouriteLocation = (TextView) findViewById(R.id.favouriteLocation);
        mapButton = (Button) findViewById(R.id.map_button);
        mapButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mapButton){
            startActivityForResult(new Intent(this, MapsActivity.class), PLACE_PICKER_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Result");
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
            String name = data.getStringExtra("location");
            Log.d(TAG, "Name: " + name);
            String favouriteLocationString = getResources().getString(R.string.favourite_location) + " " + name;
            favouriteLocation.setText(favouriteLocationString);
            Toast.makeText(this, name + " is your favourite location in Westeros", Toast.LENGTH_LONG).show();
        }
    }
}
