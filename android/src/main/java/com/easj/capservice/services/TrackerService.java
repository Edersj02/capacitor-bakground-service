package com.easj.capservice.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.easj.capservice.data.cloud.CloudDataSource;
import com.easj.capservice.data.cloud.ICloudDataSource;
import com.easj.capservice.data.preferences.TrackerPreferences;
import com.easj.capservice.entities.SendLocation;
import com.easj.capservice.entities.SessionData;
import com.easj.capservice.constans.Constans;
import com.getcapacitor.ui.Toast;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TrackerService extends Service {

    private static final String SERVICE_NAME = TrackerService.class.getName();
    private static final String CHANEL_ID = "com.easj.capservice";

    private TrackerPreferences preferences;
    private ICloudDataSource dataSource;
    private SendLocation sendLocation;
    private SessionData sessionData;

    private Location location;

    private Context context;

    private Timer timer;
    private TimerTask task;
    final Handler handler = new Handler();

    private boolean swToast = false;

    public Socket mSocket;
    {
        try{
            mSocket = IO.socket("https://trackingnode.herokuapp.com/");
        } catch (URISyntaxException e) {
            Log.d(SERVICE_NAME, "Error socket: " + e.getMessage());
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        Toast.show(this, "Service starting in background");
        context = this;
        swToast = true;
        preferences = TrackerPreferences.getInstance(getApplicationContext());
        if (preferences != null) {
            sessionData = preferences.getSessionData();
            if (sessionData.isSocketActive()) {
                mSocket.connect();
                mSocket.io().reconnection(true);
            }
            dataSource = CloudDataSource.getInstance(sessionData.getUrl(), sessionData.getTenant());
        }
        if (Build.VERSION.SDK_INT >= 26) {
            createChanelIdNotifications();
            Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                    .setContentTitle("Service in background")
                    .setContentText("Running...").build();

            startForeground(1, notification);
        }
        if (timer == null) {
            timer = new Timer();
            sentLocationTracker();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(SERVICE_NAME, "Service -----");
        try {
            if (intent != null) {
                if (intent.getAction().equals(Constans.START_FOREGROUND_ACTION)) {
                    Log.d(SERVICE_NAME, "Service ----- START_FOREGROUND_ACTION");
                    if (intent.getExtras() != null) {
                        Bundle bundle = intent.getExtras();
                        if (bundle.getParcelable("com.google.android.location.LOCATION") != null) {
                            location = bundle.getParcelable("com.google.android.location.LOCATION");
                            if (location != null) {
                                Log.i(SERVICE_NAME, "onHandleIntent " + location.getLatitude() + ", " + location.getLongitude());
                                //Object[] object = new Object[4];
                                preferences = TrackerPreferences.getInstance(getApplicationContext());
                                sessionData = preferences.getSessionData();
                                if ((!sessionData.getToken().equals("")) && sessionData.isSocketActive()) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("id", sessionData.getDriverId());
                                    obj.put("lat", location.getLatitude());
                                    obj.put("lng", location.getLongitude());
                                    obj.put("speed", location.getSpeed());
                                    obj.put("accuracy", location.getAccuracy());
                                    obj.put("bearing", location.getBearing());
                                    obj.put("altitude", location.getAltitude());
                                    obj.put("time", location.getTime());
                                    JSONObject data = new JSONObject();
                                    Date dateTime = new Date();
                                    data.put("driverid", sessionData.getDriverId());
                                    data.put("name", sessionData.getDriverName());
                                    data.put("vehicle", sessionData.getPin());
                                    data.put("dateTime", dateTime);
                                    data.put("driverstatus", preferences.getDriverStatus());
                                    obj.put("data", data);
                                    if (mSocket.connected()) {
                                        Log.d(SERVICE_NAME, obj.toString());
                                        mSocket.emit("newLocation", obj);
                                        swToast = true;
                                        Log.d(SERVICE_NAME, "Send Location Socket");
                                    } else {
                                        mSocket.connected();
                                    }
                                }
                            }
                        }
                    }
                }
                if (intent.getAction().equals(Constans.STOP_FOREGROUND_ACTION)) {
                    Log.d(SERVICE_NAME, "Service ----- STOP_FOREGROUND_ACTION");
                    stopForeground(true);
                    stopSelf();
                    timer.cancel();
                    task.cancel();
                    return START_NOT_STICKY;
                }
            }
        } catch (Exception ex) {
            Toast.show(this, "Unknown error: "+ex.getMessage());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        preferences = TrackerPreferences.getInstance(getApplicationContext());
        sessionData = preferences.getSessionData();
        if (sessionData.isSocketActive()) {
            mSocket.disconnect();
            mSocket.close();
        }
        Toast.show(this, "Service in background done");
    }

    private void createChanelIdNotifications(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications App";
            String description = "Background Service";
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(CHANEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sentLocationTracker() {
        task = new TimerTask() {
            @Override
            public void run() {
                Log.d(SERVICE_NAME, "Init timer service locations");
                try {
                    if (location != null) {
                        ArrayList<Integer> tripsIds = new ArrayList<>();
                        preferences = TrackerPreferences.getInstance(getApplicationContext());
                        Log.d(SERVICE_NAME, "Init TripId Size: " + tripsIds.size());
                        if (preferences.getTripsIds() != null) {
                            Log.d(SERVICE_NAME, "TripIds: " + preferences.getTripsIds());
                            JSONArray jsonArray = new JSONArray(preferences.getTripsIds());
                            Log.d(SERVICE_NAME, "jsonArray: " + jsonArray.toString());
                            for(int i = 0; i < jsonArray.length(); i++) {
                                tripsIds.add(Integer.parseInt(jsonArray.get(i).toString()));
                            }
                            Log.d(SERVICE_NAME, "TripId Size: " + tripsIds.size());
                        }
                        Log.d(SERVICE_NAME, "End TripId Size: " + tripsIds.size());
                        Log.d(SERVICE_NAME, "Token -----" + sessionData.getToken());
                        if (!sessionData.getToken().equals("")) {
                            sendLocation = new SendLocation();
                            sendLocation.setDriverId(sessionData.getDriverId());
                            sendLocation.setLatitude(location.getLatitude());
                            sendLocation.setLongitude(location.getLongitude());
                            sendLocation.setSpeed(location.getSpeed());
                            sendLocation.setTripsIds(tripsIds);
                            dataSource.sendLocationTracker("", sessionData.getToken(), sendLocation);
                        } else {
                            Log.d(SERVICE_NAME, "No Send Location ----");
                        }
                    }
                } catch (Exception ex) {
                    Log.d(SERVICE_NAME, "Error timer: " + ex.getMessage());
                }
            }
        };
        timer.schedule(task, 4000L, 70000L);
    }

    /**
     * enabledMockLocation
     * @return true if mock location is enabled, false if is not enabled
     */
    private boolean enabledMockLocation(Location location) {
        Log.d("MOCK_LOCATION", Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION));
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION)
                .equals("1");
    }

}
