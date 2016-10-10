package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.perfecttoilettime.perfecttoilettime.R;

public class genderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        ImageButton maleButton = (ImageButton) findViewById(R.id.imageButton);
        maleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent i = new Intent(genderActivity.this, preferencesActivity.class);
                Intent i = new Intent(v.getContext(), preferencesActivity.class);
                i.putExtra("gender","male");
                startActivity(i);
            }
        });
        ImageButton femaleButton = (ImageButton) findViewById(R.id.imageButton2);
        femaleButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                Intent i = new Intent(genderActivity.this, preferencesActivity.class);
                Intent i = new Intent(v.getContext(), preferencesActivity.class);
                i.putExtra("gender", "female");
                startActivity(i);
            }
        });
    }
}
