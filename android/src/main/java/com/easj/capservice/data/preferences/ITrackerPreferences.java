package com.easj.capservice.data.preferences;

import com.easj.capservice.entities.SessionData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ITrackerPreferences {

    void save(SessionData data);

    void setDriverStatus(JSONObject driverStatus);

    JSONObject getDriverStatus() throws JSONException;

    void setTripsIds(String tripsIds);

    String getTripsIds();

    void clearData();

    SessionData getSessionData();
}
