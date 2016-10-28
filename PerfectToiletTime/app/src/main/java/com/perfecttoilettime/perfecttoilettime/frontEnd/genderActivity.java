package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.perfecttoilettime.perfecttoilettime.R;

public class genderActivity extends AppCompatActivity {

    public static final String genderExtraKey = "gender";
    public static final int maleValue = 0;
    public static final int femaleValue = 1;

    private int gender = maleValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        Intent startIntent = getIntent();

        ImageButton maleButton = (ImageButton) findViewById(R.id.imageButton);
        maleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent i = new Intent(genderActivity.this, preferencesActivity.class);
                Intent i = new Intent(v.getContext(), preferencesActivity.class);
                i.putExtra(genderExtraKey,maleValue);
                startActivity(i);
            }
        });
        ImageButton femaleButton = (ImageButton) findViewById(R.id.imageButton2);
        femaleButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                Intent i = new Intent(genderActivity.this, preferencesActivity.class);
                Intent i = new Intent(v.getContext(), preferencesActivity.class);
                i.putExtra(genderExtraKey, femaleValue);
                startActivity(i);
            }
        });

        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(genderActivity.genderExtraKey)){
            gender = startIntent.getExtras().getInt(genderActivity.genderExtraKey);
            switch (gender){
                case genderActivity.maleValue:
                    (findViewById(R.id.activity_gender)).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.maleBackgroundColor, null));
                    break;
                case genderActivity.femaleValue:
                    (findViewById(R.id.activity_gender)).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.femaleBackgroundColor, null));
                    break;
            }
        }
    }
}
