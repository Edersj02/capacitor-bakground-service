package com.easj.capservice.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.easj.capservice.entities.SessionData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TrackerPreferences implements ITrackerPreferences {

    private static final String CLASS_NAME = TrackerPreferences.class.getName();

    private static final String TRACKER_PREFS_NAME = CLASS_NAME + ".preferences";
    private static final String DRIVER_ID = CLASS_NAME + ".driverId";
    private static final String DRIVER_NAME = CLASS_NAME + ".driverName";
    private static final String PIN = CLASS_NAME + ".pin";
    private static final String TOKEN = CLASS_NAME + ".token";
    private static final String TENANT = CLASS_NAME + ".tenant";
    private static final String URL = CLASS_NAME + ".url";
    private static final String SOCKET_URL = CLASS_NAME + ".socketUrl";
    private static final String SOCKET_ACTIVE = CLASS_NAME + ".socketActive";
    private static final String DRIVER_STATUS = CLASS_NAME + ".driverStatus";
    private static final String TRIPS_IDS = CLASS_NAME + ".tripsIds";

    private static TrackerPreferences INSTANCE;
    private SharedPreferences preferences;

    public TrackerPreferences(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(TRACKER_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public static TrackerPreferences getInstance(Context context){
        if (INSTANCE == null) {
            INSTANCE = new TrackerPreferences(context);
        }
        return INSTANCE;
    }

    @Override
    public void save(SessionData data) {
        if (data != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putInt(DRIVER_ID, data.getDriverId());
            editor.putString(DRIVER_NAME, data.getDriverName());
            editor.putInt(PIN, data.getPin());
            editor.putString(TOKEN, data.getToken());
            editor.putString(TENANT, data.getTenant());
            editor.putString(URL, data.getUrl());
            editor.putString(SOCKET_URL, data.getSocketUrl());
            editor.putBoolean(SOCKET_ACTIVE, data.isSocketActive());
            editor.apply();
        }
    }

    @Override
    public void clearData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, "");
        editor.apply();
    }

    @Override
    public void setDriverStatus(JSONObject driverStatus) {
        if (driverStatus != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(DRIVER_STATUS, driverStatus.toString());
            editor.apply();
        }
    }

    @Override
    public JSONObject getDriverStatus() throws JSONException {
        if (!Objects.equals(preferences.getString(DRIVER_STATUS, ""), "")) {
            return new JSONObject(preferences.getString(DRIVER_STATUS, ""));
        } else {
            return new JSONObject();
        }
    }

    @Override
    public void setTripsIds(String tripsIds) {
        if (tripsIds != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(TRIPS_IDS, tripsIds);
            editor.apply();
        }
    }

    @Override
    public String getTripsIds(){
        return preferences.getString(TRIPS_IDS, "");
    }

    @Override
    public SessionData getSessionData() {
        return new SessionData(
                preferences.getInt(DRIVER_ID, 0),
                preferences.getString(DRIVER_NAME, ""),
                preferences.getInt(PIN, 0),
                preferences.getString(TOKEN, ""),
                preferences.getString(TENANT, ""),
                preferences.getString(URL, ""),
                preferences.getString(SOCKET_URL, ""),
                preferences.getBoolean(SOCKET_ACTIVE, true)
        );
    }
}
