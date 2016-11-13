package com.perfecttoilettime.perfecttoilettime.frontEnd;

/**
 * Created by Mark on 11/12/16.
 */

public class Bathroom {

    private String bathroomName;
    private String latitude;
    private String longitude;

    public Bathroom(){

    }

    public Bathroom(String bathroomName, String latitude, String longitude){
        this.bathroomName = bathroomName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBathroomName(){
        return bathroomName;
    }

    public void setBathroomName(String bathroomName){
        this.bathroomName = bathroomName;
    }

    public String getLatitude(){
        return latitude;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }
}
