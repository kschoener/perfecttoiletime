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
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;

import com.perfecttoilettime.perfecttoilettime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

//created by Kyle
public class MapsActivity extends FragmentActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback{
    private GoogleMap mMap;
    private LatLng mLocation;
    private LocationManager mLocationManager;
    private final long LOCATION_REFRESH_TIME = 30000; //5 seconds -> 5000
    private final float LOCATION_REFRESH_DISTANCE = 20; //5 meters -> 5
    private final int LOCATION_REQUEST_CODE = 8;
    private final float zoomlevel = 16.0f;

    //todo delete : for demo
    private boolean madeBathrooms = false;

    private Button jumpToMe;
    private ImageButton prefLauncher;
    private Button findBathroom;
    private ArrayList<Marker> bathrooms;

    private int[] prefValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        if(startIntent.getExtras().containsKey(preferencesActivity.extraKey)){
            prefValues = startIntent.getExtras().getIntArray(preferencesActivity.extraKey);
        }

        bathrooms = new ArrayList<Marker>();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        setContentView(R.layout.activity_maps);
        jumpToMe = (Button) findViewById(R.id.jumpToMeButton);
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
                    i.putExtra(preferencesActivity.extraKey, prefValues);
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
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, userLocationListener);
        //will load bathrooms based on camera bounds
        //todo smooth these out
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);


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
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);

    }

    private void fetchData(LatLngBounds bounds){
        //remove markers no longer in the camera's view
        updateMarkers(bounds);
        //or this to clear all markers
        mMap.clear();

        //randomly add markers to the map
        double highLat = bounds.northeast.latitude;
        double highLon = bounds.northeast.longitude;
        double lowLat = bounds.southwest.latitude;
        double lowLon = bounds.southwest.longitude;
        Random rand = new Random();
        /*for(int i = 0; i < 30; i++){
            double tempLat = lowLat + (highLat - lowLat) * rand.nextDouble();
            double tempLon = lowLon + (highLon - lowLon) * rand.nextDouble();
            bathrooms.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tempLat, tempLon))
                    .title(""+i)));

        }*/
        String urlString ="http://socialgainz.com/Bumpr/PerfectToiletTime/getLocation.php";
        final String urlString1 = urlString.replaceAll(" ", "%20");
        new Thread(new Runnable() {
            public void run() {
                try{
                    //get bathrooms
                    URL url = new URL(urlString1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String resp = readStream(in);
                    //Log.d("myTag", "Response:" + resp);
                    JSONArray array = new JSONArray(resp);
                    for(int i = 0; i< array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        final String name = obj.getString("name");
                        final double latitude = Double.parseDouble(obj.getString("Latitude"));
                        final double longitude = Double.parseDouble(obj.getString("Longitude"));
                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //add bathroom mark (runs on main thread)
                                bathrooms.add(mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(name)));
                                //hMap.put(mMap.addMarker(new MarkerOptions().position(location).title(first)),ID);
                            }
                        });
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
//        //todo query the database
//        ArrayList<Pair<LatLng, String>> locationsFromDb = new ArrayList<>();
//        //todo look into cluster manager
        /*for(int i = 0; i < locationsFromDb.size(); i++) {
            LatLng pos = locationsFromDb.get(i).first;
            String bathroomName = locationsFromDb.get(i).second;
            Log.d("myTag", "Response2:" + bathroomName);
            bathrooms.add(mMap.addMarker(new MarkerOptions().position(pos).title(bathroomName)));
        }*/
    }

    private void updateMarkers(LatLngBounds bounds){
        for(int i = 0; i < bathrooms.size(); i++) {
            //if marker not in bounds, remove it from the map and bathroom list
            if (!bounds.contains(bathrooms.get(i).getPosition())) {
                bathrooms.remove(i).remove();
            }
        }
    }














































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
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };




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
//        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
    }

    @Override
    public void onCameraMoveCanceled() {
//        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
//        Toast.makeText(this, "Camera movement canceled.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraIdle() {
        //this works
//        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
//        Toast.makeText(this, "The camera has stopped moving.", Toast.LENGTH_SHORT).show();
    }




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
    //parse stream
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
