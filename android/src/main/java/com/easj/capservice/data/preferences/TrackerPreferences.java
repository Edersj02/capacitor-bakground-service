package com.easj.capservice.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.easj.capservice.entities.SessionData;

public class TrackerPreferences implements ITrackerPreferences {

    private static final String CLASS_NAME = TrackerPreferences.class.getName();

    private static final String TRACKER_PREFS_NAME = CLASS_NAME + ".preferences";
    private static final String DRIVER_ID = CLASS_NAME + ".driverId";
    private static final String TOKEN = CLASS_NAME + ".token";
    private static final String URL = CLASS_NAME + ".url";

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
            editor.putInt(DRIVER_ID, data.getDriverId());
            editor.putString(TOKEN, data.getToken());
            editor.putString(URL, data.getUrl());
            editor.apply();
        }
    }

    @Override
    public SessionData getSessionData() {
        return new SessionData(
                preferences.getInt(DRIVER_ID, 0),
                preferences.getString(TOKEN, ""),
                preferences.getString(URL, "")
        );
    }
}
