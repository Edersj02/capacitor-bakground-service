package com.easj.capservice.services;

import android.app.Activity;
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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.getcapacitor.ui.Toast;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class SignalRService extends Service {

    private static final String SERVICE_NAME = SignalRService.class.getName();
    private static final String CHANEL_ID = "com.easj.capservice";

    private Location location;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private Context context;
    private Activity activity;

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
        if (Build.VERSION.SDK_INT >= 26) {
            createChanelIdNotifications();
            Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                    .setContentTitle("Service in background")
                    .setContentText("Running...").build();

            startForeground(1, notification);
        }
//        HandlerThread thread = new HandlerThread("ServiceStartArguments",
//                Process.THREAD_PRIORITY_BACKGROUND);
//        thread.start();
//
//        // Get the HandlerThread's Looper and use it for our Handler
//        serviceLooper = thread.getLooper();
//        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.show(this, "service starting");
        Log.d(SERVICE_NAME, "Service -----");
        try {
            if (intent != null) {
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

}
