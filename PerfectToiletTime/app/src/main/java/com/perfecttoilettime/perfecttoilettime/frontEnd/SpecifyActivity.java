package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.perfecttoilettime.perfecttoilettime.R;

public class SpecifyActivity extends AppCompatActivity {

    private Button closest;
    private Button best;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify);
        closest = (Button) findViewById(R.id.buttonClosest);
        closest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo for next sprint
            }
        });

        best = (Button) findViewById(R.id.buttonBest);
        best.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo for next sprint
            }
        });
    }
}
