package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.perfecttoilettime.perfecttoilettime.R;
import com.perfecttoilettime.perfecttoilettime.backEnd.FavoritesDBHandler;

public class FullInfoPage extends AppCompatActivity {

    ImageButton favorite;
    ImageButton favoriteYellow;
    FavoritesDBHandler handler;
    TextView bathroomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info_page);

        bathroomName = (TextView) findViewById(R.id.bathroomName);
        Intent intent = getIntent();
        final String name = intent.getExtras().getString("name");
        final double latitude = intent.getExtras().getDouble("latitude");
        final double longitude = intent.getExtras().getDouble("longitude");
        bathroomName.setText(name);
        favorite = (ImageButton) findViewById(R.id.favButton);
        final Drawable onStar = getResources().getDrawable(android.R.drawable.btn_star_big_on);
        final Drawable offStar = getResources().getDrawable(android.R.drawable.btn_star_big_off);
        handler = new FavoritesDBHandler(this, null, null, 1);
        final boolean isAdded = handler.checkIfBathroomInDatabase(name);
        if(isAdded){
            favorite.setImageDrawable(onStar);
        }
        else {
            //do nothing
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInDB = handler.checkIfBathroomInDatabase(name);
                Bathroom bathroom = new Bathroom();
                String stringLat = String.valueOf(latitude);
                String stringLong = String.valueOf(longitude);
                bathroom.setBathroomName(name);
                bathroom.setLatitude(stringLat);
                bathroom.setLongitude(stringLong);
                if(isInDB){
                    handler.deleteBathroom(name);
                    favorite.setImageDrawable(offStar);
                    Toast.makeText(FullInfoPage.this, name + " has been removed from favorites!",Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.addBathroom(bathroom);
                    favorite.setImageDrawable(onStar);
                    Toast.makeText(FullInfoPage.this, name + " added to favorites!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
