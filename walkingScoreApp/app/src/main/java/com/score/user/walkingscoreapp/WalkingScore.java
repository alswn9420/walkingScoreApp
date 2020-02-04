package com.score.user.walkingscoreapp;

import java.io.Serializable;

public class WalkingScore extends Location implements Serializable
{
    private static final long serialVersionUID = 1L;

    private double walkingScore;

    // constructor
    public WalkingScore() {

    }
    int a;

    // constructor
    public WalkingScore(double walkingScore)
    {
        this.walkingScore=walkingScore;
    }

    // constructor
    public WalkingScore(double latitude, double longitude)
    {
        super.setLatitude(latitude);
        super.setLongitude(longitude);
    }
    public WalkingScore(double latitude, double longitude ,double walkingScore, String address)
    {
        super.setLatitude(latitude);
        super.setLongitude(longitude);
        this.walkingScore = walkingScore;
        super.setAddress(address);
    }

    // constructor
    public WalkingScore(double latitude, double longitude, double walkingScore)
    {
        super.setLatitude(latitude);
        super.setLongitude(longitude);
        this.walkingScore = walkingScore;
    }

    // to return walkingScore
    public double getWalkingScore()
    {
        return this.walkingScore;
    }

    // to set walkingScore
    public void setWalkingScore(double walkingScore)
    {
        this.walkingScore = walkingScore;
    }

    // to return string for this object. The format is as follows.
    // latitude = latitude value, longitude = longitude value, walking score = walking score value
    public String toString()
    {
        return super.toString()+walkingScore;
    }
}