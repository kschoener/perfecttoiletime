package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.perfecttoilettime.perfecttoilettime.R;

import java.util.ArrayList;

public class RatingActivity extends AppCompatActivity {

    private ArrayList<RatingBar> ratingBar;
    private ArrayList<TextView> rateText;
    public static final String ratingExtraKey = "rates";
    private int gender = genderActivity.maleValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int[] prevRate = null;
        Intent startIntent = getIntent();

        if(startIntent.getExtras().containsKey(ratingExtraKey)){
            prevRate = startIntent.getExtras().getIntArray(ratingExtraKey);
        }
        ratingBar = new ArrayList<RatingBar>();
        rateText = new ArrayList<TextView>();
        setContentView(R.layout.activity_rating);
        for(int i =1; i <= 4; i++){
            String barID = "ratingBar";
            String textID = "text";
            barID += i;
            textID += i;
            int barResID = getResources().getIdentifier(barID, "id", getPackageName());
            int textResID = getResources().getIdentifier(textID, "id", getPackageName());

            RatingBar tempBar = (RatingBar) findViewById(barResID);
            if(prevRate != null){
                tempBar.setProgress(prevRate[i-1]);
                
            }
            TextView tempText = (TextView) findViewById(textResID);
            tempText.setText(tempBar.getProgress() + "/" + tempBar.getMax());
            tempBar.setOnRatingBarChangeListener(MyRateListener);

            ratingBar.add(tempBar);
            rateText.add(tempText);

        }
        Button saveButton = (Button) findViewById(R.id.rateSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMap = new Intent(v.getContext(), MapsActivity.class);
//                int[] extras = new int[RatingBar.resolveSize()];
//                for(int i = 0; i < RatingBar.size(); i += 1){
                    //todo add save functionality for Rating
//                    extras[i] = RatingBar.get(i).getProgress();
//                    startMap.putExtra((String) prefNames.get(i).getText(), bars.get(i).getProgress());
//               }
                startMap.putExtra(ratingExtraKey, extras);
                startMap.putExtra(genderActivity.genderExtraKey, gender);
                startActivity(startMap);
            }
        });
        if(startIntent.getExtras().containsKey(genderActivity.genderExtraKey)){
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
    }

    private RatingBar.OnRatingBarChangeListener MyRateListener = new RatingBar.OnRatingBarChangeListener(){
        public void onRatingChanged(RatingBar ratingBar, float rating,
                                    boolean fromUser) {

            // TODO: 11/12/16 to finish up the rating bar listener
        }

    };
}
