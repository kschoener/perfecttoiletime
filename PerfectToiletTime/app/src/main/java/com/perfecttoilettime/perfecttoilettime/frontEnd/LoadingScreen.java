package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.perfecttoilettime.perfecttoilettime.R;

public class LoadingScreen extends AppCompatActivity {

    private ProgressBar progress;
    private int progressStatus = 0;
    private Handler mHandler;

    private String androidID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.activity_loading_screen);
        progress = (ProgressBar) findViewById(R.id.loadingScreenProgressBar);

        //set a spinning wheel or something while connecting to server
        androidID = Settings.Secure.getString(LoadingScreen.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                checkDb();
                while (progressStatus < 100) {
                    //progressStatus = checkDb();

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            progress.setProgress(progressStatus);
                        }
                    });
                }
            }
        }).start();

    }

    private void checkDb(){
        //returningUser is true if androidID is in the database
        boolean returningUser = false;
        //now check the database for androidID

        Intent startNextScreen;
        if(returningUser){
            loadUserData();
            progressStatus = 100;
            startNextScreen = new Intent(this, MapsActivity.class);
            startNextScreen.putExtra("userInfo", new String[]{androidID,"male", "clean", "empty", "wifi"});
            startActivity(startNextScreen);
        }else {
            progressStatus = 100;
            startNextScreen = new Intent(this, genderActivity.class);
            startActivity(startNextScreen);
        }
    }

    private void loadUserData(){
        //TODO load the user's data
        //Here we will load gender and preferences

        //this is for testing
        Runnable incrementProgress = new Runnable() {
            @Override
            public void run() {
                progressStatus += 10;
                if(progressStatus < 90){
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
//        while(progressStatus<90){
//            mHandler.postDelayed(incrementProgress, 1000);
//        }
        mHandler.post(incrementProgress);
    }
}
