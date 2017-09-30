package com.dolan.dominic.dublinbikes.activities.journeys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dolan.dominic.dublinbikes.R;

/**
 * Created by domin on 25 Sep 2017.
 */

public class JourneysActivity  extends AppCompatActivity {
    //This activity works in a very similar way to favourites activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journeys);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
