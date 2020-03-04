package com.easj.capservice.entities;

public class SessionData {

    private int driverId;

    private String token;

    private String url;

    public SessionData() {
        super();
    }

    public SessionData(int driverId, String token, String url) {
        this.driverId = driverId;
        this.token = token;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
