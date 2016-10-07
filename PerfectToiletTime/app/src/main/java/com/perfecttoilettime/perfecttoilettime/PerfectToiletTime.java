package com.perfecttoilettime.perfecttoilettime;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Mark on 9/30/16.
 */

public class PerfectToiletTime extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
