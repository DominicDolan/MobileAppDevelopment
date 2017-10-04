package com.dolan.dominic.firebaseregister;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavouriteActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private static String HERO = "favourite_hero";
    private static String VILLAIN = "favourite_villain";

    private EditText heroEditTxt;
    private EditText villainEditTxt;

    private TextView favoriteHeroTxt;
    private TextView favoriteVillainTxt;

    private Button submitButton;
    private FirebaseUser user;
    private DatabaseReference firebasedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebasedata = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        firebasedata.addValueEventListener(this);

        heroEditTxt = (EditText) findViewById(R.id.heroEditTxt);
        villainEditTxt = (EditText) findViewById(R.id.villainEditTxt);

        favoriteHeroTxt = (TextView) findViewById(R.id.favouriteHeroTxt);
        favoriteVillainTxt = (TextView) findViewById(R.id.favouriteVillainTxt);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String hero = heroEditTxt.getText().toString();
        String villain = villainEditTxt.getText().toString();
        if (v == submitButton && user != null){
            //Save the values both in firebase and in shared preferences
            firebasedata.child(HERO).setValue(hero);
            firebasedata.child(VILLAIN).setValue(villain);

            SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit()
                    .putString(HERO, hero)
                    .putString(VILLAIN, villain);

            editor.apply();
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //When the firebase data changes get the values from firebase, if that doesn't work
        //get the values from shared preferences. This method is always called when the activity
        //starts
        String favouriteHero = getString(R.string.favourite_hero);
        String name = dataSnapshot.child(HERO).getValue(String.class);

        if (TextUtils.isEmpty(name)){
            favouriteHero += getPreferences(MODE_PRIVATE).getString(HERO, "");
        }else {
            favouriteHero += name;
        }
        favoriteHeroTxt.setText(favouriteHero);

        String favouriteVillain = getString(R.string.favourite_villain);
        name = dataSnapshot.child(VILLAIN).getValue(String.class);

        if (TextUtils.isEmpty(name)){
            favouriteVillain += getPreferences(MODE_PRIVATE).getString(VILLAIN, "");
        }else {
            favouriteVillain += name;
        }

        favoriteVillainTxt.setText(favouriteVillain);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
