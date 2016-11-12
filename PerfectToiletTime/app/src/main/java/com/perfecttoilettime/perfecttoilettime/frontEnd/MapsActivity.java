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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import java.lang.*;

//created by Kyle
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, OnMapLongClickListener,
        OnCameraMoveStartedListener, OnCameraMoveListener, OnCameraMoveCanceledListener,
        OnInfoWindowLongClickListener, OnInfoWindowClickListener,
        InfoWindowAdapter{

    private GoogleMap mMap;
    private LatLng mLocation;
    private LocationManager mLocationManager;
    private final long LOCATION_REFRESH_TIME = 30000; //5 seconds -> 5000
    private final float LOCATION_REFRESH_DISTANCE = 20; //5 meters -> 5
    private final int LOCATION_REQUEST_CODE = 8;
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

    private int searchDistanceMiles = 100000;
    private LatLngBounds bathroomListContains;

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
        //allows us to use the color makers
        MapsInitializer.initialize(getApplicationContext());

        infoWindowView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        genderColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        bathroomIdtoJSONInfo = new HashMap<>();

        Intent startIntent = getIntent();

        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(preferencesActivity.preferenceExtraKey)) {
            prefValues = startIntent.getExtras().getIntArray(preferencesActivity.preferenceExtraKey);
        }

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
//                specifyBathroom(v);
                findClosest(v);
//                updateBounds();
            }
        });

        //if the user's gender was passed, set the color scheme accordingly
        if(startIntent.getExtras() != null && startIntent.getExtras().containsKey(genderActivity.genderExtraKey)){
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
        bathroomListContains = mMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        startBathroomRetrieval(center.latitude, center.longitude);
        // Add a marker in Sydney and move the camera
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

    }

    private void addBathroomMarkers(String jsonArrayString) {
        //add markers to the hash map
        try {
            JSONArray db = new JSONArray(jsonArrayString);
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
//        mMap.clear();
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

    }

    public void menuLauncher(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    // Created by Steven
    public void findClosest(View view) {
//        mLocation = new LatLng(43.002341, -78.788195);
        Intent intent = new Intent(this, MapsActivity.class);
//        mMap.clear();
//        LinkedHashMap<Double, Double> latLongLinkedHashMap = new LinkedHashMap();
        HashMap<Integer, Double> distancesHashMap = new HashMap();
        HashMap<Integer, Double> sortedDistancesHashMap = new HashMap();
        int id = 0;
        Double latitude = 0.0;
        Double longitude = 0.0;
        String name = "";
        Double myLat = mLocation.latitude;
        Double myLong = mLocation.longitude;

        //Double myLat = 42.0;
        //Double myLong = 42.0;

        for(int currentId : bathroomIdtoJSONInfo.keySet()){
            try {
                double latDouble = bathroomIdtoJSONInfo.get(currentId).getDouble("Latitude");
                double longDouble = bathroomIdtoJSONInfo.get(currentId).getDouble("Longitude");
                double distance = Math.sqrt(Math.pow(myLat-latDouble,2)+Math.pow(myLong-longDouble,2));
                distancesHashMap.put(id, distance);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        sortedDistancesHashMap = sortHashMapByValueLeastToGreatest(distancesHashMap);
        Iterator<HashMap.Entry<Integer, Double>> it = sortedDistancesHashMap.entrySet().iterator();
        while(it.hasNext()) {
            HashMap.Entry<Integer, Double> entry = it.next();
            int closestBathroom = entry.getKey();
            try {
                latitude = bathroomIdtoJSONInfo.get(closestBathroom).getDouble("Latitude");
                longitude = bathroomIdtoJSONInfo.get(closestBathroom).getDouble("Longitude");
                name = bathroomIdtoJSONInfo.get(closestBathroom).getString("name");

//            startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
//        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomlevel));

    }



    // Created by Steven
    public void findBest(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
//        mMap.clear();
        JSONArray locationArray = new JSONArray();
        int i;
        HashMap sortedBathroomsAvgRatingSumHashMap = new HashMap();
        HashMap bathroomsAvgRatingSumHashMap = new HashMap();
        int bestBathroom;
        double longDouble = 0;
        double latDouble = 0;
        String name = "";

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
            name = ratingsObject.getJSONObject("info").getString("name");
            latDouble = Double.parseDouble(latValue);
            longDouble = Double.parseDouble(longValue);

        }catch(Exception e){
            e.printStackTrace();
        }
        //mMap.addMarker(new MarkerOptions().position(new LatLng(latDouble, longDouble)).title("Bathroom_ID:"+bestBathroom));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latDouble, longDouble)).title(name));
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


    private void startBathroomRetrieval(double lat, double lon){
        //this gives you all bathrooms within 12 miles of the given coordinates
        //http://socialgainz.com/Bumpr/PerfectToiletTime/getLocation.php?Latitude=12&Longitude=77&Distance=12
        //"Latitude", "Longitude", "name"
        if(!gettingBathrooms) {
            gettingBathrooms = true;
            JSONArray db = new JSONArray();
            JSONAsyncTask jsonBathrooms = new JSONAsyncTask(
                    "http://socialgainz.com/Bumpr/PerfectToiletTime/getLocation.php?Latitude=" + lat +
                            "&Longitude=" + lon + "&Distance=" + (searchDistanceMiles*100),
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
                "http://socialgainz.com/Bumpr/PerfectToiletTime/getRatings.php?bathroomID="+bathroomId+
                        "&rand="+(new Random()).nextInt(),
                this
        );
        //can't run this in parallel, must wait
        try {
            String s = jsonRatings.execute().get();
            ratings = new JSONObject(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratings;
    }

    //for InfoWindowAdapter
    @Override
    public View getInfoWindow(Marker marker) {
        //set up infoWindowView and return it
        try {
            //get name from id
            ((TextView)infoWindowView.findViewById(R.id.bathroomName))
                    .setText(bathroomIdtoJSONInfo.get(
                            Integer.parseInt(marker.getTitle())).getString("name")
                    )
            ;
            //get average
            JSONObject ratings = getRatings(Integer.parseInt(marker.getTitle()));
            JSONObject averages = ratings.getJSONObject("average");
            double wifiAvg = averages.getDouble("Wifi");
            double cleanAvg = averages.getDouble("Clean");
            double busyAvg = averages.getDouble("Busy");
            float totalAverage = (float)((wifiAvg+cleanAvg+busyAvg)/3);
            ((RatingBar)infoWindowView.findViewById(R.id.bathroomRating)).setRating(totalAverage);
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
        //TODO: create full bathroom info page
        //int bathroomID = Integer.parseInt(marker.getTitle()); //
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //for purpose of favorites button but can also be used for info page instead of long click
        Intent intent = new Intent(this, FullInfoPage.class);
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
        boolean needToLoad = false;
        LatLng newNortheast = bathroomListContains.northeast;
        LatLng newSouthwest = bathroomListContains.southwest;
        if(bounds.northeast.longitude > newNortheast.longitude){
            newNortheast = new LatLng(newNortheast.latitude, bounds.northeast.longitude);
            needToLoad = true;
        }
        if(bounds.northeast.latitude > newNortheast.latitude){
            newNortheast = new LatLng(bounds.northeast.latitude, newNortheast.longitude);
            needToLoad = true;
        }
        if(bounds.southwest.longitude < newSouthwest.longitude){
            newSouthwest = new LatLng(newSouthwest.latitude, bounds.southwest.longitude);
            needToLoad = true;
        }
        if(bounds.southwest.latitude > newSouthwest.latitude){
            newSouthwest = new LatLng(bounds.southwest.latitude, newSouthwest.longitude);
            needToLoad = true;
        }

        if(needToLoad) {
            bathroomListContains = new LatLngBounds(newSouthwest, newNortheast);
            updateSearchDistance(bounds);
            LatLng center = bounds.getCenter();
            startBathroomRetrieval(center.latitude, center.longitude);
        }else{
            addBathroomMarkers("");
        }
    }

    private void updateSearchDistance(LatLngBounds bounds){
        //each degree of latitude is approx. 69 miles
        //each degree of longitude is approx. 55 miles
        int latD = (int)Math.abs((bounds.northeast.latitude-bounds.southwest.latitude));
        int lonD = (int)Math.abs((bounds.northeast.longitude-bounds.southwest.longitude));
        searchDistanceMiles = latD > lonD ? latD : lonD;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        /*
        if (reason == OnCameraMoveStartedListener.REASON_GESTURE) {
            //this works
            updateBounds();
//            Toast.makeText(this, "The user gestured on the map.", Toast.LENGTH_SHORT).show();
        } else if (reason == OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            //this works
            updateBounds();
            //todo bring up bathroom information
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
        i.putExtra(AddBathroomActivity.addBathroomExtra, put);
        startActivity(i);
    }

    @Override
    public void onCameraMove() {
        //updateBounds();
    }

    @Override
    public void onCameraMoveCanceled() {
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
            mRef.addBathroomMarkers(strings);

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
