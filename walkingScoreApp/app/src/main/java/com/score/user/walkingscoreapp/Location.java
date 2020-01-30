package com.score.user.walkingscoreapp;

import java.io.Serializable;

public class Location implements Serializable {
    private double latitude;
    private double longitude;
    private String address;
    private double nearestScore;
    //Coordinate 클래스에서 에딧트 텍스트 입력값에 요상한 값이 들어올때 처리할 불린타입변수
    private boolean errorCheck=false;

    // constructor
    public Location()
    {

    }

    // constructor
    public Location(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Location(double latitude, double longitude, String address)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    // to return latitude
    public double getLatitude()
    {
        return latitude;
    }

    // to set latitude
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    // to return longitude
    public double getLongitude()
    {
        return longitude;
    }

    // to set longitude
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    // if otherLatitude and otherLongitude is same to latitude and longitude, return true.
    // otherwise, return false.
    public boolean isSameLocation(double otherLatitude, double otherLongitude)
    {
        if(this.latitude ==otherLatitude && this.longitude == otherLongitude)
            return true;
        else
            return false;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    // to calculate the distance from a location(latitude, longitude) to a location(otherLatitude, otherLongitude).
    public double calculateDistance(double otherLatitude, double otherLongitude)
    {
        return Math.sqrt((Math.pow((otherLatitude - this.latitude),2))+(Math.pow((otherLongitude - this.longitude),2)));
    }
    public void setNearestScore(double nearestScore){
        this.nearestScore = nearestScore;
    }
    public double getNearestScore(){
        return nearestScore;
    }
    // to return string for this object. The format is as follows.
    // latitude = latitude value, longitude = longitude value
    public String toString()
    {
        return longitude+"\t"+latitude+"\t";
    }


    public void setErrorCheck(){
        this.errorCheck=true;
    }
    public boolean getErrorCheck(){
        return errorCheck;
    }
}