package com.example.steven.perfecttoilettime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class mapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void launchPref(View view) {
        Intent intent = new Intent(this, preferencesActivity.class);
        startActivity(intent);
    }

    public void launchSpecify(View view) {
        Intent intent = new Intent(this, specifiedActivity.class);
        startActivity(intent);
    }
}
