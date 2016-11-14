package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
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
        handler = new FavoritesDBHandler(this, null, null, 1);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bathroom bathroom = new Bathroom();
                String stringLat = String.valueOf(latitude);
                String stringLong = String.valueOf(longitude);
                bathroom.setBathroomName(name);
                bathroom.setLatitude(stringLat);
                bathroom.setLongitude(stringLong);
                handler.addBathroom(bathroom);
                Toast.makeText(FullInfoPage.this, name + " added to favorites!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
