package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.Manifest;
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
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;

import com.perfecttoilettime.perfecttoilettime.R;

import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback{

    private GoogleMap mMap;

    private LatLng mLocation;
    private LocationManager mLocationManager;
    private final long LOCATION_REFRESH_TIME = 5000; //5 seconds
    private final float LOCATION_REFRESH_DISTANCE = 5; //5 meters
    private final int LOCATION_REQUEST_CODE = 8;
    private final LocationListener userLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            //possibly update visible bathrooms as well
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    private ArrayList<Marker> bathrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bathrooms = new ArrayList<Marker>();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, userLocationListener);

        setContentView(R.layout.activity_maps);
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
            mMap.setMyLocationEnabled(true);
        }
        //will load bathrooms based on camera bounds
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);




        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


    }

    private void fetchData(LatLngBounds bounds){
        //remove markers no longer in the camera's view
        updateMarkers(bounds);
        //or this to clear all markers
        //mMap.clear();

        //randomly add markers to the map
        double highLat = bounds.northeast.latitude;
        double highLon = bounds.northeast.longitude;
        double lowLat = bounds.southwest.latitude;
        double lowLon = bounds.southwest.longitude;
        Random rand = new Random();
        for(int i = 0; i < 30; i++){
            double tempLat = lowLat + (highLat - lowLat) * rand.nextDouble();
            double tempLon = lowLon + (highLon - lowLon) * rand.nextDouble();
            bathrooms.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tempLat, tempLon))
                    .title(""+i)));

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

    private void updateMarkers(LatLngBounds bounds){
        for(int i = 0; i < bathrooms.size(); i++) {
            //if marker not in bounds, remove it from the map and bathroom list
            if (!bounds.contains(bathrooms.get(i).getPosition())) {
                bathrooms.remove(i).remove();
            }
        }
    }





    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == OnCameraMoveStartedListener.REASON_GESTURE) {
            fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);

            Toast.makeText(this, "The user gestured on the map.", Toast.LENGTH_SHORT).show();

        } else if (reason == OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
            //todo bring up bathroom information
            Toast.makeText(this, "The user tapped something on the map.",
                    Toast.LENGTH_SHORT).show();

        } else if (reason == OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            Toast.makeText(this, "The app moved the camera.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMove() {
        Toast.makeText(this, "The camera is moving.",
                Toast.LENGTH_SHORT).show();
        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
    }

    @Override
    public void onCameraMoveCanceled() {
        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);

        Toast.makeText(this, "Camera movement canceled.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraIdle() {
        //do nothing for now
        fetchData(mMap.getProjection().getVisibleRegion().latLngBounds);
        Toast.makeText(this, "The camera has stopped moving.",
                Toast.LENGTH_SHORT).show();
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
}
