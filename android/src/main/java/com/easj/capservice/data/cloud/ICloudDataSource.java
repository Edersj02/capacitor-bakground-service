package com.easj.capservice.data.cloud;

import com.easj.capservice.entities.SendLocation;
import com.easj.capservice.external.ResponseMessage;

public interface ICloudDataSource {

    void sendLocationTracker(String url, String token, String tenant, SendLocation sendLocation);

    interface sendLocationTrackerCallback {
        void onSuccess(ResponseMessage message);
        void onFailed(String error);
    }

    void sendLocationTrackerSignalR(String url, String token, String tenant, SendLocation sendLocation);

}
