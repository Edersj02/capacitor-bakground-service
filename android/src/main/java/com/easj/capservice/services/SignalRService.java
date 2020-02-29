package com.easj.capservice.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.getcapacitor.ui.Toast;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class SignalRService extends Service {

    private static final String SERVICE_NAME = SignalRService.class.getName();

    private NotificationManager mNM;
    private int NOTIFICATION = 1000;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message message) {}
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                THREAD_PRIORITY_BACKGROUND);

        Toast.show(this, "Service Start");

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (intent != null) {
                Log.d(SERVICE_NAME, "This is a Background service for Android");
            }
        } catch (Exception ex) {
            Toast.show(this, "Unknown error: " + ex.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        Toast.show(this, "Service Done");
    }
}
