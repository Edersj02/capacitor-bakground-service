package com.easj.capservice.entities;

public class SessionData {

    private int driverId;

    private String token;

    public SessionData() {
        super();
    }

    public SessionData(int driverId, String token) {
        this.driverId = driverId;
        this.token = token;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
