package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.location.LocationListener;
//import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;

import com.perfecttoilettime.perfecttoilettime.R;

import java.util.ArrayList;
import java.util.Random;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

//created by Kyle
public class MapsActivity extends FragmentActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        if (startIntent.getExtras().containsKey(preferencesActivity.extraKey)) {
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
        /*
        prefLauncher = (ImageButton) findViewById(R.id.mapsSettingButton);
        prefLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), preferencesActivity.class);
                if (prefValues != null) {
                    i.putExtra(preferencesActivity.extraKey, prefValues);
                }
                startActivity(i);
            }
        });
        */
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        if (lastKnownLocation == null) {
            mLocation = new LatLng(43.002341, -78.788195);
            firstMarker = "Davis Hall";
        } else {
            mLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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

    private void fetchData(LatLngBounds bounds) {
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
        for (int i = 0; i < 30; i++) {
            double tempLat = lowLat + (highLat - lowLat) * rand.nextDouble();
            double tempLon = lowLon + (highLon - lowLon) * rand.nextDouble();
            bathrooms.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tempLat, tempLon))
                    .title("" + i)));

        }

//        //todo query the database
//        ArrayList<Pair<LatLng, String>> locationsFromDb = new ArrayList<>();
//        //todo look into cluster manager
//        for(int i = 0; i < locationsFromDb.size(); i++) {
//            LatLng pos = locationsFromDb.get(i).first;
//            String bathroomName = locationsFromDb.get(i).second;
//            bathrooms.add(mMap.addMarker(new MarkerOptions().position(pos).title(bathroomName)));
//        }
    }

    private void updateMarkers(LatLngBounds bounds) {
        for (int i = 0; i < bathrooms.size(); i++) {
            //if marker not in bounds, remove it from the map and bathroom list
            if (!bounds.contains(bathrooms.get(i).getPosition())) {
                bathrooms.remove(i).remove();
            }
        }
    }

    public void menuLauncher(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }


    public void findClosest(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        mMap.clear();

        startActivity(intent);
    }
    // Created by Steven
    public void findBest(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        mMap.clear();
        JSONArray locationArray = new JSONArray();
        int i;
        HashMap sortedBathroomsAvgRatingSumHashMap = new HashMap();
        HashMap bathroomsAvgRatingSumHashMap = new HashMap();
        int bestBathroom;
        double longDouble = 0;
        double latDouble = 0;

        // Makes a JSON Array of all the bathrooms.
        try {
            //String locationUrlString = "http://socialgainz.com/Bumpr/PerfectToiletTime/getLocation.php";
            String locationUrlString = "http://socialgainz.com/Bumpr/PerfectToiletTime/GetAllLocations.php";
            URL url = new URL(locationUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String rs = readStream(in);
            JSONArray locArray = new JSONArray(rs);
            locationArray = locArray;

        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            for (i=1; i<locationArray.length()+1; i++) {
                // Goes through every bathroom's ratings page, sums the average values, and puts thems into a HashMap
                String ratingsUrlString ="http://socialgainz.com/Bumpr/PerfectToiletTime/getRatings.php?bathroomID="+i+"&rand="+8;
                //String ratingsUrlString ="http://socialgainz.com/Bumpr/PerfectToiletTime/getRatings.php?bathroomID="+i+"&rand="+10;
                URL url2 = new URL(ratingsUrlString);
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                InputStream inp = new BufferedInputStream(urlConnection2.getInputStream());
                String rStream = readStream(inp);
                JSONObject ratingsObject = new JSONObject(rStream);
                String avgBusyString = ratingsObject.getJSONObject("average").getString("Busy");
                String avgCleanString = ratingsObject.getJSONObject("average").getString("Clean");
                String avgWifiString = ratingsObject.getJSONObject("average").getString("Wifi");
                Double avgBusyDouble = Double.parseDouble(avgBusyString);
                Double avgCleanDouble = Double.parseDouble(avgCleanString);
                Double avgWifiDouble = Double.parseDouble(avgWifiString);
                Double sumOfAverages = avgBusyDouble + avgCleanDouble + avgWifiDouble;
                bathroomsAvgRatingSumHashMap.put(i,sumOfAverages);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        // HashMap is sorted so that the best rated is on top.
        sortedBathroomsAvgRatingSumHashMap = sortHashMapByValue(bathroomsAvgRatingSumHashMap);
        HashMap.Entry<Integer, Double> entry = (HashMap.Entry<Integer, Double>) sortedBathroomsAvgRatingSumHashMap.entrySet().iterator().next();
        bestBathroom = entry.getKey();
        try {
            // Goes through the ratings page for the best bathroom and retrieves the coordinates.
            String bestUrlString = "http://socialgainz.com/Bumpr/PerfectToiletTime/getRatings.php?bathroomID="+bestBathroom+"&rand="+8;
            URL url3 = new URL(bestUrlString);
            HttpURLConnection urlConnection3 = (HttpURLConnection) url3.openConnection();
            InputStream inpu = new BufferedInputStream(urlConnection3.getInputStream());
            String reaStream = readStream(inpu);
            JSONObject ratingsObject = new JSONObject(reaStream);
            String latValue = ratingsObject.getJSONObject("info").getString("Latitude");
            String longValue = ratingsObject.getJSONObject("info").getString("Longitude");
            latDouble = Double.parseDouble(latValue);
            longDouble = Double.parseDouble(longValue);

        }catch(Exception e){
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(latDouble, longDouble)).title("Bathroom_ID:"+bestBathroom));
        startActivity(intent);
    }

    public void specifyBathroom(View view) {
        Intent intent = new Intent(this, SpecifyActivity.class);
        startActivity(intent);
    }

    // Sorts HashMap by value.
    public HashMap<Integer, Double> sortHashMapByValue(HashMap<Integer, Double> unsortedHashMap) {
        List<HashMap.Entry<Integer, Double>> list = new LinkedList<HashMap.Entry<Integer, Double>>(unsortedHashMap.entrySet());
        Collections.sort(list, new Comparator<HashMap.Entry<Integer, Double>>() {
            public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
                return(o2.getValue().compareTo(o1.getValue()));
            }
        });
        HashMap<Integer, Double> sortedHashMap = new LinkedHashMap<Integer, Double>();
        for (HashMap.Entry<Integer, Double> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    // From Ted's branch
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

    private final LocationListener userLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(mLocation).title("MY LOCATION"));
            if (!madeBathrooms) {
                fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
//                madeBathrooms = true;
            }
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

