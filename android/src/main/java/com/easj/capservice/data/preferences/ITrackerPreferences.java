package com.easj.capservice.data.preferences;

import com.easj.capservice.entities.SessionData;

import org.json.JSONObject;

public interface ITrackerPreferences {

    void save(SessionData data);

    void setDriverStatus(JSONObject driverStatus);

    SessionData.DriverStatus getDriverStatus();

    void clearData();

    SessionData getSessionData();
}
