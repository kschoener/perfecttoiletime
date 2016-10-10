package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.perfecttoilettime.perfecttoilettime.R;

import java.util.ArrayList;

public class preferencesActivity extends AppCompatActivity {
    private ArrayList<SeekBar> bars;
    private ArrayList<TextView> valueKeepers;
    private ArrayList<TextView> prefNames;
    public static final String extraKey = "prefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] prevPref = null;
        Intent startingIntent =getIntent();
        if(startingIntent.getExtras().containsKey(extraKey)){
            prevPref = startingIntent.getExtras().getIntArray(extraKey);
        }

        bars = new ArrayList<SeekBar>();
        valueKeepers = new ArrayList<TextView>();
        prefNames = new ArrayList<TextView>();
        setContentView(R.layout.activity_preferences);

        for(int i =1; i <= 6; i++){
            String barID = "seekBar";
            String textID = "result";
            String prefID = "pref";
            if(i>1){
                barID += i;
                textID += i;
                prefID += i;
            }
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
                startMap.putExtra(extraKey, extras);
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
