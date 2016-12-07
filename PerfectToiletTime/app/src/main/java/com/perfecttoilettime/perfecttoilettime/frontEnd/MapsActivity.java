package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.location.LocationListener;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.perfecttoilettime.perfecttoilettime.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import java.lang.String;

import static java.lang.Thread.sleep;

//created by Kyle
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private LatLng mLocation;
    private LocationManager mLocationManager;
    private final long LOCATION_REFRESH_TIME = 30000; //5 seconds -> 5000
    private final float LOCATION_REFRESH_DISTANCE = 20; //5 meters -> 5
    public static final int LOCATION_REQUEST_CODE = 8;
    private final float zoomlevel = 16.0f;

    private boolean madeBathrooms = false;

    private ImageButton jumpToMe;
    private ImageButton prefLauncher;
    private Button findBathroom;

    private HashMap<Integer, JSONObject> bathroomIdtoJSONInfo;

    private int[] prefValues;
    private int gender = genderActivity.maleValue;
    private View infoWindowView;
    private BitmapDescriptor genderColor;
    private boolean gettingBathrooms = false;
    private Double closestLatitudeReturn, closestLongitudeReturn;

    private int searchDistanceMiles = 100000;
//    private LatLngBounds bathroomListContains;

    private final String baseURL = "http://ec2-54-71-248-37.us-west-2.compute.amazonaws.com/home/PerfectToiletTime/";

    private final LocationListener userLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.clear();
//            mMap.addMarker(new MarkerOptions().position(mLocation).title("MY LOCATION"));
            if(!madeBathrooms){
                madeBathrooms = true;
                LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
                startBathroomRetrieval(center.latitude, center.longitude);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        long startTime = System.currentTimeMillis();
        while((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            Toast.makeText(this, "PerfectToiletTime needs Location Permissions!", Toast.LENGTH_LONG);
            try {
                sleep(2 * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                wait(2 * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(System.currentTimeMillis()-startTime > 60*1000){
                finish();
            }
        }
        //allows us to use the color makers
        MapsInitializer.initialize(getApplicationContext());

        infoWindowView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        bathroomIdtoJSONInfo = new HashMap<>();

        Intent startIntent = getIntent();

        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(preferencesActivity.preferenceExtraKey)) {
            prefValues = startIntent.getExtras().getIntArray(preferencesActivity.preferenceExtraKey);
        }

        setContentView(R.layout.activity_maps);

        jumpToMe = (ImageButton) findViewById(R.id.jumpToMeButton);
        jumpToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
                LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
                startBathroomRetrieval(center.latitude, center.longitude);
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
                findClosest(v);
            }
        });

        //if the user's gender was passed, set the color scheme accordingly
        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(genderActivity.genderExtraKey)){
            gender = startIntent.getExtras().getInt(genderActivity.genderExtraKey);
            switch (gender){
                case genderActivity.maleValue:
                    genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    jumpToMe.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.maleBackgroundColor, null));
                    prefLauncher.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.maleBackgroundColor, null));
                    findBathroom.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.maleBackgroundColor, null));
                    infoWindowView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.maleBackgroundColor, null));
                    break;
                case genderActivity.femaleValue:
                    genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                    jumpToMe.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.femaleBackgroundColor, null));
                    prefLauncher.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.femaleBackgroundColor, null));
                    findBathroom.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.femaleBackgroundColor, null));
                    infoWindowView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.femaleBackgroundColor, null));
                    break;
            }
        }


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
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, userLocationListener);

        //set InfoWindow
        mMap.setInfoWindowAdapter(this);

        //set info window click listener
//        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        //set camera move listeners
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);

        mMap.setOnMapLongClickListener(this);

//        String locationProvider = LocationManager.NETWORK_PROVIDER;
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation == null) {
            mLocation = new LatLng(43.002341, -78.788195);
        }else{
            mLocation= new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
//        mMap.addMarker(new MarkerOptions().position(mLocation).title(firstMarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoomlevel));
//        bathroomListContains = mMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        startBathroomRetrieval(center.latitude, center.longitude);
        // Add a marker in Sydney and move the camera
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

    }

    private void addBathroomMarkers(String jsonArrayString) {
        //add markers to the hash map
        try {
//            bathroomIdtoJSONInfo.clear();
            JSONArray db = new JSONArray(jsonArrayString);
            Log.d("getLocation", "received data and it is: "+jsonArrayString);
            for (int i = 0; i < db.length(); i++) {
                try {
                    JSONObject temp = db.getJSONObject(i);
                    if (!bathroomIdtoJSONInfo.containsKey(temp.getInt("id"))) {
                        bathroomIdtoJSONInfo.put(temp.getInt("id"), temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.e("addbathroom", "The string that failed to load as a JSONArray is: "+jsonArrayString);
        }

        //update markers
        mMap.clear();
        for(int id : bathroomIdtoJSONInfo.keySet()){
            try {
                JSONObject temp = bathroomIdtoJSONInfo.get(id);
                LatLng tempLatLng = new LatLng(temp.getDouble("Latitude"), temp.getDouble("Longitude"));
                if(mMap.getProjection().getVisibleRegion().latLngBounds.contains(tempLatLng)) {
                    mMap.addMarker(new MarkerOptions()
                            .position(tempLatLng)
                            .icon(genderColor)
                            .title("" + temp.getInt("id"))
                    );
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("bathroom", "could not convert from jsonobject to latlng");
            }
        }
        gettingBathrooms = false;
    }

    public void menuLauncher(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    // Created by Steven
    public void findClosest(View view) {
	    /*
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        Location myLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //this will be null if we can't get a last known location
        */
//        Double myLat = myLocation.getLatitude();
//        Double myLong = myLocation.getLongitude();
        Double myLat = mLocation.latitude;
        Double myLong = mLocation.longitude;
        Double closestLatitude = findClosestLatitude(myLat, myLong);
        Double closestLongitude = findClosestLongitude(myLat, myLong);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(closestLatitude,closestLongitude)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(closestLatitude,closestLongitude), zoomlevel));

    }

// Created by Steven
    public Double findClosestLatitude(Double myLat, Double myLong) {
        HashMap<Integer, Double> distancesHashMap = new HashMap();
        HashMap<Integer, Double> sortedDistancesHashMap = new HashMap();
        HashMap<Integer, Double> latHashMap = new HashMap();
        int id = 0;
        Double distance, closestLatitude;

        try {
            JSONAsyncTask JSONLocations = new JSONAsyncTask("http://ec2-54-71-248-37.us-west-2.compute.amazonaws.com/home/PerfectToiletTime/GetAllLocations.php", this);
            JSONArray locArray = new JSONArray(JSONLocations.execute().get());

            for (int i=0; i<locArray.length(); i++) {
                id++;
                JSONObject locObj = locArray.getJSONObject(i);
                String latString = locObj.getString("Latitude");
                String longString = locObj.getString("Longitude");
                Double latDouble = Double.parseDouble(latString);
                Double longDouble = Double.parseDouble(longString);
                latHashMap.put(id, latDouble);
                System.out.println("myLat = "+myLat);
                distance = Math.sqrt(Math.pow(myLat-latDouble,2)+Math.pow(myLong-longDouble,2));
                distancesHashMap.put(id, distance);
            }
            sortedDistancesHashMap = sortHashMapByValueLeastToGreatest(distancesHashMap);
            HashMap.Entry<Integer, Double> entry = (HashMap.Entry<Integer, Double>) sortedDistancesHashMap.entrySet().iterator().next();
            int closestBathroom = entry.getKey();
            closestLatitude = latHashMap.get(closestBathroom);
            closestLatitudeReturn = closestLatitude;
        }catch(Exception e){
            e.printStackTrace();
        }
        return closestLatitudeReturn;
    }


    public Double findClosestLongitude(Double myLat, Double myLong) {
        HashMap<Integer, Double> distancesHashMap = new HashMap();
        HashMap<Integer, Double> sortedDistancesHashMap = new HashMap();
        HashMap<Integer, Double> longHashMap = new HashMap();
        int id = 0;
        Double distance, closestLongitude;

        try {
            JSONAsyncTask JSONLocations = new JSONAsyncTask("http://ec2-54-71-248-37.us-west-2.compute.amazonaws.com/home/PerfectToiletTime/GetAllLocations.php", this);
            JSONArray locArray = new JSONArray(JSONLocations.execute().get());

            for (int i=0; i<locArray.length(); i++) {
                id++;
                JSONObject locObj = locArray.getJSONObject(i);
                String latString = locObj.getString("Latitude");
                String longString = locObj.getString("Longitude");
                Double latDouble = Double.parseDouble(latString);
                Double longDouble = Double.parseDouble(longString);
                longHashMap.put(id, longDouble);
                distance = Math.sqrt(Math.pow(myLat-latDouble,2)+Math.pow(myLong-longDouble,2));
                distancesHashMap.put(id, distance);
            }
            sortedDistancesHashMap = sortHashMapByValueLeastToGreatest(distancesHashMap);
            HashMap.Entry<Integer, Double> entry = (HashMap.Entry<Integer, Double>) sortedDistancesHashMap.entrySet().iterator().next();
            int closestBathroom = entry.getKey();
            closestLongitude = longHashMap.get(closestBathroom);
            closestLongitudeReturn = closestLongitude;
        }catch(Exception e){
            e.printStackTrace();
        }
        return closestLongitudeReturn;
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


    private void startBathroomRetrieval(double lat, double lon){
        //this gives you all bathrooms within the view
        //http://socialgainz.com/Bumpr/PerfectToiletTime/getLocation.php?Latitude=12&Longitude=77&Distance=12
        //"Latitude", "Longitude", "name"
        if(!gettingBathrooms) {
            Log.d("getLocation", ("Getting locations for lat "+lat+", lon "+lon+", and distance, "+searchDistanceMiles));
            gettingBathrooms = true;
            JSONArray db = new JSONArray();
            JSONAsyncTask jsonBathrooms = new JSONAsyncTask(
                    baseURL+"getLocation.php?Latitude=" + lat +
                            "&Longitude=" + lon + "&Distance=" + (searchDistanceMiles),
                    this
            );
            jsonBathrooms.execute();
        }
        return;
    }

    private JSONObject getRatings(int bathroomId){
        // http://socialgainz.com/Bumpr/PerfectToiletTime/getRatings.php?bathroomID=1&rand=145
        // return getJSONObject("average"); -> :{"Wifi":"3.333", "Clean":"4.555", "Busy":"5.000"}
        JSONObject ratings = new JSONObject();
        JSONAsyncTask jsonRatings = new JSONAsyncTask(
                baseURL+"getRatings.php?bathroomID="+bathroomId+
                        "&rand="+(new Random()).nextInt(),
                this
        );
        //can't run this in parallel, must wait
        try {
            String s = jsonRatings.execute().get();
            ratings = new JSONObject(s);
        } catch (Exception e) {
            Log.e("getRatings", "there was an error loading the ratings for bathroomID: "+bathroomId);
            e.printStackTrace();
        }
        return ratings;
    }

    //for InfoWindowAdapter
    String bname;
    double blatitude;
    double blongitude;
    @Override
    public View getInfoWindow(Marker marker) {
        //set up infoWindowView and return it
        try {
            //get name from id
            bname = bathroomIdtoJSONInfo.get(
                    Integer.parseInt(marker.getTitle())).getString("name");
            blatitude = bathroomIdtoJSONInfo.get(
                    Integer.parseInt(marker.getTitle())).getDouble("Latitude");
            blongitude = bathroomIdtoJSONInfo.get(
                    Integer.parseInt(marker.getTitle())).getDouble("Longitude");
            //set info window title
            ((TextView)infoWindowView.findViewById(R.id.bathroomName)).setText(bname);
            //get average
            JSONObject ratings = getRatings(Integer.parseInt(marker.getTitle()));
            Log.d("getInfoWindow", "got the ratings");
            JSONObject averages = ratings.getJSONObject("average");
            Log.d("getInfoWindow", "got the averages");
            double wifiAvg = averages.getDouble("Wifi");
            Log.d("getInfoWindow", "got the wifi average");
            double cleanAvg = averages.getDouble("Clean");
            Log.d("getInfoWindow", "got the clean average");
            double busyAvg = averages.getDouble("Busy");
            Log.d("getInfoWindow", "got the busy average");
            float totalAverage = (float)((wifiAvg+cleanAvg+busyAvg)/3);
            Log.d("getInfoWindow", "got the total average: "+totalAverage);
            ((RatingBar)infoWindowView.findViewById(R.id.bathroomRating)).setRating(totalAverage);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("quickInfoFail", "Quick info failed to load with marker title: "+marker.getTitle()+" and id: "+marker.getId());
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
        //TODO: create full bathroom info page
        //int bathroomID = Integer.parseInt(marker.getTitle()); //
        Intent intent = new Intent(this, FullInfoPage.class);
        intent.putExtra("name", bname);
        intent.putExtra("latitude", blatitude);
        intent.putExtra("longitude", blongitude);
        intent.putExtra(genderActivity.genderExtraKey, gender);
        startActivity(intent);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //for purpose of favorites button but can also be used for info page instead of long click
        Intent intent = new Intent(this, FullInfoPage.class);
        intent.putExtra("name", bname);
        intent.putExtra("latitude", blatitude);
        intent.putExtra("longitude", blongitude);
        startActivity(intent);
    }

    public HashMap<Integer, Double> sortHashMapByValueLeastToGreatest(HashMap<Integer, Double> unsortedHashMap) {
        List<HashMap.Entry<Integer, Double>> list = new LinkedList<HashMap.Entry<Integer, Double>>(unsortedHashMap.entrySet());
        Collections.sort(list, new Comparator<HashMap.Entry<Integer, Double>>() {
            public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
                return(o1.getValue().compareTo(o2.getValue()));
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

    private void updateBounds(){
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        updateSearchDistance(bounds);
        startBathroomRetrieval(bounds.getCenter().latitude, bounds.getCenter().longitude);
//        boolean needToLoad = false;
//        LatLng newNortheast = bathroomListContains.northeast;
//        LatLng newSouthwest = bathroomListContains.southwest;
//        if(bounds.northeast.longitude > newNortheast.longitude){
//            newNortheast = new LatLng(newNortheast.latitude, bounds.northeast.longitude);
//            needToLoad = true;
//        }
//        if(bounds.northeast.latitude > newNortheast.latitude){
//            newNortheast = new LatLng(bounds.northeast.latitude, newNortheast.longitude);
//            needToLoad = true;
//        }
//        if(bounds.southwest.longitude < newSouthwest.longitude){
//            newSouthwest = new LatLng(newSouthwest.latitude, bounds.southwest.longitude);
//            needToLoad = true;
//        }
//        if(bounds.southwest.latitude > newSouthwest.latitude){
//            newSouthwest = new LatLng(bounds.southwest.latitude, newSouthwest.longitude);
//            needToLoad = true;
//        }

//        if(needToLoad) {
//            bathroomListContains = new LatLngBounds(newSouthwest, newNortheast);
//            updateSearchDistance(bounds);
//            LatLng center = bounds.getCenter();
//            startBathroomRetrieval(center.latitude, center.longitude);
//        }else{
//            addBathroomMarkers("");
//        }

    }

    private void updateSearchDistance(LatLngBounds bounds){
        //each degree of latitude is approx. 69 miles
        //each degree of longitude is approx. 55 miles
        int latD = (int)Math.ceil(Math.abs((bounds.northeast.latitude-bounds.southwest.latitude)))*75;
        int lonD = (int)Math.ceil(Math.abs((bounds.northeast.longitude-bounds.southwest.longitude)))*60;
        searchDistanceMiles = latD > lonD ? latD : lonD;
        searchDistanceMiles = searchDistanceMiles < 50 ? 50 : searchDistanceMiles;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Log.d("cameraMove", "update bathrooms because camera move started");
            updateBounds();
//            Toast.makeText(this, "The user gestured on the map.", Toast.LENGTH_SHORT).show();
        } /*else if (reason == OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            updateBounds();
//            Toast.makeText(this, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();

        } else if (reason == OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(this, "The app moved the camera.", Toast.LENGTH_SHORT).show();
        }
        */
    }

    @Override
    public void onMapLongClick(LatLng pos) {
        double[] put = {pos.latitude, pos.longitude};
        Log.d("WTFLongClick", "Click lat: "+pos.latitude+", Click lon: "+pos.longitude);
        Intent i = new Intent(this, AddBathroomActivity.class);
        i.putExtra(genderActivity.genderExtraKey, gender);
        i.putExtra(AddBathroomActivity.addBathroomExtra, put);
        startActivity(i);
    }

    @Override
    public void onCameraMove() {
//        Log.d("cameraMove", "update bathrooms because onCameraMove");
//        updateBounds();
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.d("cameraMove", "update bathrooms because onCameraMoveCancelled");
        updateBounds();
    }


    public class JSONAsyncTask extends AsyncTask<Void, Void, String> {
        private String mUrl;
        private MapsActivity mRef;

        public JSONAsyncTask(String url, MapsActivity ref) {
            mUrl = url;
            mRef = ref;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString = null;
            resultString = getJSON(mUrl);
            return resultString;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            try {
                JSONObject temp = new JSONObject(strings);
            }catch (Exception e) {
                mRef.addBathroomMarkers(strings);
            }
        }

        private String getJSON(String url) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
//                            sb.append(line+"\n");
                            sb.append(line);
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (Exception ex) {
                return ex.toString();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        //disconnect error
                    }
                }
            }
            return null;
        }
    }
    //end of async task class

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
                    Toast.makeText(this, "PerfectToiletTime needs Location Permissions!", Toast.LENGTH_LONG);
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
