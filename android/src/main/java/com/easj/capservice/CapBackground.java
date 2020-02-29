package com.easj.capservice;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        activity = (Activity) this.getContext();

        JSObject ret = new JSObject();
        ret.put("key", "This is a test for Android");
        ret.put("value", value);
        Toast.show(context, value + " - " + ret.getString("key"));
        call.success(ret);
    }

    @PluginMethod()
    public void startBackgroundService() {
        context = this.getContext();
        Intent intent = new Intent(context, SignalRService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent);
        } else {
            activity.startService(intent);
        }
    }
}
