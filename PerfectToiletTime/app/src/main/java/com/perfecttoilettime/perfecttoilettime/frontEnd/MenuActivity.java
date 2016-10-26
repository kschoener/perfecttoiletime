package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.perfecttoilettime.perfecttoilettime.R;


public class MenuActivity extends AppCompatActivity {

    private int[] prefValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        setContentView(R.layout.activity_menu);
        if (startIntent.getExtras().containsKey(preferencesActivity.extraKey)) {
            prefValues = startIntent.getExtras().getIntArray(preferencesActivity.extraKey);
        }
    }

    public void menuPrefLauncher(View view) {
        Intent intent = new Intent(this, preferencesActivity.class);
        if (prefValues != null) {
            intent.putExtra(preferencesActivity.extraKey, prefValues);
        }
        startActivity(intent);
    }

    public void menuGenderLauncher(View view) {
        Intent intent = new Intent(this, genderActivity.class);
        startActivity(intent);
    }


}
