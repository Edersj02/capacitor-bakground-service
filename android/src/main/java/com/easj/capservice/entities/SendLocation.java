package com.easj.capservice.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SendLocation {

    @SerializedName("driverid")
    int DriverId;
    @SerializedName("latitude")
    Double Latitude;
    @SerializedName("longitude")
    Double Longitude;
    @SerializedName("speed")
    float Speed;
    @SerializedName("tripsids")
    ArrayList<Integer> tripsIds;

    public SendLocation() {
        super();
    }

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    public ArrayList<Integer> getTripsIds() {
        return tripsIds;
    }

    public void setTripsIds(ArrayList<Integer> tripsIds) {
        this.tripsIds = tripsIds;
    }
}
