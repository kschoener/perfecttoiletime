package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.perfecttoilettime.perfecttoilettime.R;
import com.perfecttoilettime.perfecttoilettime.backEnd.FavoritesDBHandler;

public class FullInfoPage extends AppCompatActivity {

    ImageButton favorite;
    FavoritesDBHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info_page);

        favorite = (ImageButton) findViewById(R.id.favButton);
        handler = new FavoritesDBHandler(this, null, null, 1);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
