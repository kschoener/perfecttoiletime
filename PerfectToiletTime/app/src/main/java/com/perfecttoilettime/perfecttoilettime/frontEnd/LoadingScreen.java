package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.util.Log;

import com.perfecttoilettime.perfecttoilettime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.math.BigInteger;

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
        try{
            SecureRandom random = new SecureRandom();
            String rand = new BigInteger(130, random).toString(32);
            Log.d("myTag",rand);
            String urlString ="http://ec2-54-71-248-37.us-west-2.compute.amazonaws.com/home/PerfectToiletTime/isUserEntered.php?userID=" + androidID + "&rand=" + rand;
            final String urlString1 = urlString.replaceAll(" ", "%20");
            URL url = new URL(urlString1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String resp = readStream(in);
            //check database for ID, it will echo "false" if there is not ID in the database
            if(resp.equals("false"))
                returningUser = false;
            else
                returningUser = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        //now check the database for androidID

//        Intent startNextScreen;
        if(returningUser){
            loadUserData();
            progressStatus = 100;
//            startNextScreen = new Intent(this, MapsActivity.class);
//            startNextScreen.putExtra("userInfo", new String[]{androidID,"male", "clean", "empty", "wifi"});
//            startActivity(startNextScreen);
        }else {
            progressStatus = 100;
            Intent startNextScreen = new Intent(this, genderActivity.class);
            startActivity(startNextScreen);
        }
    }

    private void loadUserData(){
        //Here we will load gender and preferences
        try{
            SecureRandom random = new SecureRandom();
            String rand = new BigInteger(130, random).toString(32);
            //URL needs random string to prevent output caching
            String urlString ="http://ec2-54-71-248-37.us-west-2.compute.amazonaws.com/home/PerfectToiletTime/isUserEntered.php?userID=" + androidID + "&rand=" + rand;
            final String urlString1 = urlString.replaceAll(" ", "%20");
            URL url = new URL(urlString1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String resp = readStream(in);
            JSONArray array = new JSONArray(resp);
            //data from URL is saved as JSONArray and looped through
            for(int i = 0; i< array.length(); i++) {
                //TODO sort through this
                JSONObject obj = array.getJSONObject(i);
                final String cleaness = obj.getString("Cleaness");
                final String wifi = obj.getString("Wifi");
                final String flush = obj.getString("Flush");
                final String space = obj.getString("Space");
                final String mirror = obj.getString("Mirror");
                final String faucet = obj.getString("Faucet");
                final String gender = obj.getString("gender");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //this is for testing
//        Runnable incrementProgress = new Runnable() {
//            @Override
//            public void run() {
//                progressStatus += 10;
//                if(progressStatus < 90){
//                    mHandler.postDelayed(this, 1000);
//                }
//            }
//        };
//        mHandler.post(incrementProgress);

    }
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}

