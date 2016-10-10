package com.perfecttoilettime.perfecttoilettime.backEnd;

import android.provider.Settings;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import com.firebase.client.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kschoener on 9/28/16.
 */
public class BackendShit {
    //don't actually code here -- just a holder for the package
    //String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(),
      //      Settings.Secure.ANDROID_ID);// get unique device ID;
    //Firebase.setAndroidContext(this);
//    Firebase ref = new Firebase("https://perfecttoilettime.firebaseio.com/Bathrooms");
    //Value event listener for realtime data update
//    ref.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot snapshot) {
//            //this for loop should be used to add all the bathrooms to the map
//            for (DataSnapshot child : snapshot.getChildren()) {//iterate through all bathrooms and get bathroom name, latitude, and longitude
//                String BathroomName = child.child("Name").getValue().toString();    //stores the bathroom name as string variable
//                Double Latitude = Double.parseDouble((child.child("Latitude").getValue().toString()));//stores the latitude as Double
//                Double Longitude = Double.parseDouble((child.child("Longitude").getValue().toString()));//stores the longitude as Double
//                    /*Log.d("tag", BathroomName);           //for output testing
//                    Log.d("tag", Latitude.toString());      //for output testing
//                    Log.d("tag", Longitude.toString());*/   //for output testing
//            }
//        }
//        @Override
//        public void onCancelled(FirebaseError firebaseError) {
//            Log.d("tag", firebaseError.getMessage());
//        }
//    });
}
