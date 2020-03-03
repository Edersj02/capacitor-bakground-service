package com.easj.capservice;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.easj.capservice.services.SignalRService;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.ui.Toast;

@NativePlugin()
public class CapBackground extends Plugin {

    private Context context;
    private Activity activity;

    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");

        context = this.getContext();
        activity = getActivity();

        JSObject ret = new JSObject();
        ret.put("key", "This is a test for Android");
        ret.put("value", value);
        Toast.show(context, value + " - " + ret.getString("key"));
        call.success(ret);
    }

    @PluginMethod()
    public void startBackgroundService(PluginCall call) {
        context = this.getContext();
        activity = getActivity();
        // activity = (Activity) this.getContext();
        // createChanelIdNotifications();
        Intent intent = new Intent(context, SignalRService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent);
        } else {
            activity.startService(intent);
        }
        JSObject ret = new JSObject();
        ret.put("value", "Start Service");
        call.resolve(ret);
    }

    private void createChanelIdNotifications(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String CHANNEL_ID = "com.easj.capservice";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications App";
            String description = "Background Service";
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
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
