package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.perfecttoilettime.perfecttoilettime.R;

import java.util.ArrayList;

public class preferencesActivity extends AppCompatActivity {
    private ArrayList<SeekBar> bars;
    private ArrayList<TextView> valueKeepers;
    private ArrayList<TextView> prefNames;
    public static final String preferenceExtraKey = "prefs";

    private int gender = genderActivity.maleValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] prevPref = null;
        Intent startIntent = getIntent();

        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(preferenceExtraKey)){
            prevPref = startIntent.getExtras().getIntArray(preferenceExtraKey);
        }
        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(genderActivity.genderExtraKey)){
            gender = startIntent.getExtras().getInt(genderActivity.genderExtraKey);
            switch (gender){
                case genderActivity.maleValue:
                    (findViewById(R.id.activity_preferences)).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.maleBackgroundColor, null));
                    break;
                case genderActivity.femaleValue:
                    (findViewById(R.id.activity_preferences)).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.femaleBackgroundColor, null));
                    break;
            }
        }

        bars = new ArrayList<SeekBar>();
        valueKeepers = new ArrayList<TextView>();
        prefNames = new ArrayList<TextView>();
        setContentView(R.layout.activity_preferences);

        for(int i =1; i <= 3; i++){
            String barID = "seekBar";
            String textID = "result";
            String prefID = "pref";
            barID += i;
            textID += i;
            prefID += i;

            int barResID = getResources().getIdentifier(barID, "id", getPackageName());
            int textResID = getResources().getIdentifier(textID, "id", getPackageName());
            int prefResID = getResources().getIdentifier(prefID, "id", getPackageName());

            SeekBar tempBar = (SeekBar) findViewById(barResID);
            if(prevPref != null){
                tempBar.setProgress(prevPref[i-1]);
            }

            TextView tempText = (TextView) findViewById(textResID);
            tempText.setText(tempBar.getProgress() + "/" + tempBar.getMax());
            tempBar.setOnSeekBarChangeListener(mySeekListener);

            bars.add(tempBar);
            valueKeepers.add(tempText);
            prefNames.add((TextView)findViewById(prefResID));
        }

        Button emailButton = (Button) findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(preferencesActivity.this, MailSenderActivity.class));
            }
        });

//        Button addBathroomButton = (Button) findViewById(R.id.addBathroomButton);
//        addBathroomButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(preferencesActivity.this, AddBathroomActivity.class));
//            }
//        });

        Button saveButton = (Button) findViewById(R.id.prefSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMap = new Intent(v.getContext(), MapsActivity.class);
                int[] extras = new int[bars.size()];
                for(int i = 0; i < bars.size(); i += 1){
                    //todo add save functionality for preferences
                    extras[i] = bars.get(i).getProgress();
//                    startMap.putExtra((String) prefNames.get(i).getText(), bars.get(i).getProgress());
                }
                startMap.putExtra(preferenceExtraKey, extras);
                startMap.putExtra(genderActivity.genderExtraKey, gender);
                startActivity(startMap);
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener mySeekListener = new SeekBar.OnSeekBarChangeListener(){
        int progress = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progress = progresValue;
            int index= bars.indexOf(seekBar);
            valueKeepers.get(index).setText("" + progress + "/" + seekBar.getMax());
//            Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int index= bars.indexOf(seekBar);
            valueKeepers.get(index).setText("" + progress + "/" + seekBar.getMax());
            //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();

        }
    };



}
