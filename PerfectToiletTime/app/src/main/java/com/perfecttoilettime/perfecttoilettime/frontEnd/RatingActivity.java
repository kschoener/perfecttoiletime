package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        int[] prevPref = null;
        Intent startIntent = getIntent();

        if(startIntent.getExtras().containsKey(ratingExtraKey)){
            prevPref = startIntent.getExtras().getIntArray(ratingExtraKey);
        }
        ratingBar = new ArrayList<RatingBar>();
        rateText = new ArrayList<TextView>();
        setContentView(R.layout.activity_rating);
        for(int i =1; i <= 4; i++){
            String barID = "ratingBar";
            String textID = "text";
        }
    }
}
