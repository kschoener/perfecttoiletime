package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.whinc.widget.ratingbar.RatingBar;

import com.perfecttoilettime.perfecttoilettime.R;

public class RatingActivity extends AppCompatActivity {

    public static final String ratingExtraKey = "rates";
    private int gender = genderActivity.maleValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(genderActivity.genderExtraKey)){
            gender = getIntent().getExtras().getInt(genderActivity.genderExtraKey);
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

        final com.whinc.widget.ratingbar.RatingBar ratingBar = (com.whinc.widget.ratingbar.RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(RatingBar view, int preCount, int curCount) {
                Log.i("TAG", String.format("previous count:%d, current count:%d", preCount, curCount));
            }
        });
        final RatingBar ratingBar2 = new RatingBar(this);
        ratingBar2.setMaxCount(5);
        ratingBar2.setCount(2);
        ratingBar2.setFillDrawableRes(R.drawable.empty);
        ratingBar2.setEmptyDrawableRes(R.drawable.fill);
        ratingBar2.setSpace(0);
        ratingBar2.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(RatingBar view, int preCount, int curCount) {
                Log.i("TAG", String.format("previous count:%d, current count:%d", preCount, curCount));
            }
        });
//        int[] prevRate = null;

//        if(startIntent.getExtras().containsKey(ratingExtraKey)){
//            prevRate = startIntent.getExtras().getIntArray(ratingExtraKey);
//        }
//        ratingBar = new ArrayList<RatingBar>();
//        rateText = new ArrayList<TextView>();
//        setContentView(R.layout.activity_rating);
//        for(int i =1; i <= 4; i++){
//            String barID = "ratingBar";
//            String textID = "text";
//            barID += i;
//            textID += i;
//            int barResID = getResources().getIdentifier(barID, "id", getPackageName());
//            int textResID = getResources().getIdentifier(textID, "id", getPackageName());
//
//            RatingBar tempBar = (RatingBar) findViewById(barResID);
//            if(prevRate != null){
//                tempBar.setProgress(prevRate[i-1]);
//
//            }
//            TextView tempText = (TextView) findViewById(textResID);
//            tempText.setText(tempBar.getProgress() + "/" + tempBar.getMax());
//            tempBar.setOnRatingBarChangeListener(MyRateListener);
//
//            ratingBar.add(tempBar);
//            rateText.add(tempText);
//
//        }
        Button saveButton = (Button) findViewById(R.id.rateSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMap = new Intent(v.getContext(), MapsActivity.class);
                int[] extras = new int[RatingBar.getDefaultSize(2,2)];
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
    }

//    private RatingBar.OnRatingBarChangeListener MyRateListener = new RatingBar.OnRatingBarChangeListener(){
//        public void onRatingChanged(RatingBar ratingBar, float rating,
//                                    boolean fromUser) {
//
//            // TODO: 11/12/16 to finish up the rating bar listener
//        }
//
//    };
}
