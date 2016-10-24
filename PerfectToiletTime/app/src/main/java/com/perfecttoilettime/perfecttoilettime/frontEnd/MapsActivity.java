package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;

import com.perfecttoilettime.perfecttoilettime.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//created by Kyle
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        OnCameraMoveStartedListener, OnCameraMoveListener,
        OnInfoWindowLongClickListener,
        InfoWindowAdapter{
    private GoogleMap mMap;
    private LatLng mLocation;
    private LocationManager mLocationManager;
    private final long LOCATION_REFRESH_TIME = 30000; //5 seconds -> 5000
    private final float LOCATION_REFRESH_DISTANCE = 20; //5 meters -> 5
    private final int LOCATION_REQUEST_CODE = 8;
    private final float zoomlevel = 16.0f;

    //todo delete : for demo
    private boolean madeBathrooms = false;

    private ImageButton jumpToMe;
    private ImageButton prefLauncher;
    private Button findBathroom;
    private ArrayList<Marker> testingBathrooms;

    private HashMap<Integer, JSONObject> bathroomIdtoJSONInfo;


    private int[] prefValues;
    private int gender = genderActivity.maleValue;

    private View infoWindowView;

    private BitmapDescriptor genderColor;

    private final LocationListener userLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(mLocation).title("MY LOCATION"));
            if(!madeBathrooms){
                fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
//                madeBathrooms = true;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext());

        infoWindowView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

        genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);

        bathroomIdtoJSONInfo = new HashMap<Integer, JSONObject>();

        Intent startIntent = getIntent();

        if(startIntent.getExtras().containsKey(preferencesActivity.preferenceExtraKey)){
            prefValues = startIntent.getExtras().getIntArray(preferencesActivity.preferenceExtraKey);
        }

        testingBathrooms = new ArrayList<Marker>();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

        setContentView(R.layout.activity_maps);

        jumpToMe = (ImageButton) findViewById(R.id.jumpToMeButton);
        jumpToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
//                fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
            }
        });


        prefLauncher = (ImageButton) findViewById(R.id.mapsSettingButton);
        prefLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), preferencesActivity.class);
                if(prefValues != null){
                    i.putExtra(genderActivity.genderExtraKey, gender);
                    i.putExtra(preferencesActivity.preferenceExtraKey, prefValues);
                }
                startActivity(i);
            }
        });


        findBathroom = (Button) findViewById(R.id.mapsFindBathroom);
        findBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation = new LatLng(43.002341, -78.788195);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
                fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
            }
        });

        if(startIntent.getExtras().containsKey(genderActivity.genderExtraKey)){
            gender = startIntent.getExtras().getInt(genderActivity.genderExtraKey);
            switch (gender){
                case genderActivity.maleValue:
                    genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    jumpToMe.setBackgroundColor(getResources().getColor(R.color.maleBackgroundColor));
                    prefLauncher.setBackgroundColor(getResources().getColor(R.color.maleBackgroundColor));
                    findBathroom.setBackgroundColor(getResources().getColor(R.color.maleBackgroundColor));
                    infoWindowView.setBackgroundColor(getResources().getColor(R.color.maleBackgroundColor));
                    break;
                case genderActivity.femaleValue:
                    genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                    jumpToMe.setBackgroundColor(getResources().getColor(R.color.femaleBackgroundColor));
                    prefLauncher.setBackgroundColor(getResources().getColor(R.color.femaleBackgroundColor));
                    findBathroom.setBackgroundColor(getResources().getColor(R.color.femaleBackgroundColor));
                    infoWindowView.setBackgroundColor(getResources().getColor(R.color.femaleBackgroundColor));
                    break;
            }
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, userLocationListener);

        //set InfoWindow
//        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setInfoWindowAdapter(this);

        //set info window click listener
//        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
//        mMap.setOnInfoWindowCloseListener(this);


        //will load testingBathrooms based on camera bounds
//        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
//        mMap.setOnCameraMoveCanceledListener(this);



//        String locationProvider = LocationManager.NETWORK_PROVIDER;
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
        String firstMarker;
        if(lastKnownLocation == null){
            mLocation = new LatLng(43.002341, -78.788195);
            firstMarker = "Davis Hall";
        }else{
            mLocation= new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            firstMarker = "My Location";
        }
        mMap.addMarker(new MarkerOptions().position(mLocation).title(firstMarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
        // Add a marker in Sydney and move the camera
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

    }

    private void fetchData(LatLngBounds bounds) {
        //remove markers no longer in the camera's view
//        updateMarkers(bounds);
        //or this to clear all markers
//        mMap.clear();
        LatLng center = bounds.getCenter();
        JSONArray db = getBathrooms(center.latitude, center.longitude, 10);

        //todo look into cluster manager
        for(int i = 0; i < db.length(); i++) {
            try {
                JSONObject temp = db.getJSONObject(i);
                if (!bathroomIdtoJSONInfo.containsKey(temp.getInt("id"))) {
                    bathroomIdtoJSONInfo.put(temp.getInt("id"), temp);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getDouble("Latitude"), temp.getDouble("Longitude")))
                            .icon(genderColor)
                            .title("" + temp.getInt("id"))
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private JSONArray getBathrooms(double lat, double lon, int distanceInMiles){
        //each degree of latitude is approx. 69 miles
        //each degree of longitude is approx. 55 miles
        JSONArray db = new JSONArray();
        //this gives you all bathrooms within 12 miles of the given coordinates
        //http://socialgainz.com/Bumpr/PerfectToiletTime/getLocation.php?Latitude=12&Longitude=77&Distance=12
        //"Latitude", "Longitude", "name"


        return db;
    }

    private JSONObject getRatings(int bathroomId){
        JSONObject ratings = new JSONObject();
        // http://socialgainz.com/Bumpr/PerfectToiletTime/getRatings.php?bathroomID=1&rand=145
        // return getJSONObject("average"); -> :{"Wifi":"3.333", "Clean":"4.555", "Busy":"5.000"}

        return ratings;
    }

    //for InfoWindowAdapter
    @Override
    public View getInfoWindow(Marker marker) {
        //set up infoWindowView and return it
        try {
            //get name from id
            ((TextView)infoWindowView.findViewById(R.id.bathroomName))
                    .setText(bathroomIdtoJSONInfo.get(Integer.parseInt(marker.getTitle())).getString("name"));
            //get average
            JSONObject ratings = getRatings(Integer.parseInt(marker.getTitle()));
            float totalAverage = 0f;
            JSONObject averages = ratings.getJSONObject("average");
            double wifiAvg = averages.getDouble("Wifi");
            double cleanAvg = averages.getDouble("Clean");
            double busyAvg = averages.getDouble("Busy");
            totalAverage = (float)((wifiAvg+cleanAvg+busyAvg)/3);
            ((RatingBar)infoWindowView.findViewById(R.id.bathroomRating))
                    .setRating(totalAverage);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return infoWindowView;
    }
    //for InfoWindowAdapter
    @Override
    public View getInfoContents(Marker marker) {

//            if (MapsActivity.this.marker != null
//                    && MapsActivity.this.marker.isInfoWindowShown()) {
//                MapsActivity.this.marker.hideInfoWindow();
//                MapsActivity.this.marker.showInfoWindow();
//            }
        return null;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        //TODO: start new activity for all bathroom info (Juno's task)
    }


    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == OnCameraMoveStartedListener.REASON_GESTURE) {
            //this works
//            fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);

//            Toast.makeText(this, "The user gestured on the map.", Toast.LENGTH_SHORT).show();

        } else if (reason == OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            //this works
//            fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
            //todo bring up bathroom information
//            Toast.makeText(this, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();

        } else if (reason == OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(this, "The app moved the camera.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMove() {
//        Toast.makeText(this, "The camera is moving.", Toast.LENGTH_SHORT).show();
        //this works
        //TODO: use this to load more testingBathrooms as the user moves the map
//        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
    }











    //exit if the app was not given location permissions
    @Override
    public void onRequestPermissionsResult
    (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("PerfectToiletTime", "Not granted location permissions");
                    finish();
                    System.exit(1);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
