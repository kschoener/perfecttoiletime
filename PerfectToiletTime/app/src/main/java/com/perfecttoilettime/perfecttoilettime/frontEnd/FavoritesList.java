package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.perfecttoilettime.perfecttoilettime.R;
import com.perfecttoilettime.perfecttoilettime.backEnd.FavoritesDBHandler;

import java.util.List;

public class FavoritesList extends AppCompatActivity {

    FavoritesDBHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_list);

        handler = new FavoritesDBHandler(this, null, null, 1);
        List<String> bathrooms = handler.getAllBathrooms();
        if(bathrooms.size()>0) {
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bathrooms);
            ListView favList = (ListView) findViewById(R.id.favoritesListView);
            favList.setAdapter(adapter);
        }
        else{
            Toast.makeText(FavoritesList.this, "No favorite bathrooms!",Toast.LENGTH_SHORT).show();
        }
    }
}
