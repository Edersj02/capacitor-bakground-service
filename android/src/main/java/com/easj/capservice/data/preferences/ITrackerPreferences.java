package com.easj.capservice.data.preferences;

import com.easj.capservice.entities.SessionData;

public interface ITrackerPreferences {

    void save(SessionData data);

    void clearData();

    SessionData getSessionData();
}
