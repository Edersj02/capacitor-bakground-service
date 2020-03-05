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

    private Timer timer = new Timer();
    final Handler handler = new Handler();

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        context = this;
        preferences = TrackerPreferences.getInstance(getApplicationContext());
        if (preferences != null) {
            sessionData = preferences.getSessionData();
            dataSource = CloudDataSource.getInstance(sessionData.getUrl());
        }
        if (Build.VERSION.SDK_INT >= 26) {
            createChanelIdNotifications();
            Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                    .setContentTitle("Service in background")
                    .setContentText("Running...").build();

            startForeground(1, notification);
        }
        sentLocationTracker();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.show(this, "service starting");
        Log.d(SERVICE_NAME, "Service -----");
        try {
            if (intent != null) {
                if (intent.getAction().equals(Constans.START_FOREGROUND_ACTION)) {
                    Log.d(SERVICE_NAME, "Service ----- START_FOREGROUND_ACTION");
                }
                if (intent.getAction().equals(Constans.STOP_FOREGROUND_ACTION)) {
                    Log.d(SERVICE_NAME, "Service ----- STOP_FOREGROUND_ACTION");
                    stopForeground(true);
                    stopSelf();
                }
                if (intent.getExtras() != null) {
                    Bundle bundle = intent.getExtras();
                    if (bundle.getParcelable("com.google.android.location.LOCATION") != null) {
                        location = bundle.getParcelable("com.google.android.location.LOCATION");
                        if (location != null) {
                            Log.i(SERVICE_NAME, "onHandleIntent " + location.getLatitude() + ", " + location.getLongitude());
                        }
                    }
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
        Toast.show(this, "service done");
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
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d(SERVICE_NAME, "Init timer locations");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (location != null) {
                                preferences = TrackerPreferences.getInstance(getApplicationContext());
                                sessionData = preferences.getSessionData();
                                sendLocation = new SendLocation();
                                sendLocation.setDriverId(sessionData.getDriverId());
                                sendLocation.setLatitude(location.getLatitude());
                                sendLocation.setLongitude(location.getLongitude());
                                sendLocation.setSpeed(location.getSpeed());
                                Log.d(SERVICE_NAME, "Token -----" + sessionData.getToken());
                                dataSource.sendLocationTracker("", sessionData.getToken(), sendLocation);
                            }
                        } catch (Exception ex) {
                            Log.d(SERVICE_NAME, "Error timer: " + ex.getMessage());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 60000L, 60000L);
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
