package com.easj.capservice;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.ui.Toast;

@NativePlugin()
public class CapBackground extends Plugin {

    private Context context;

    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");

        context = this.getContext();

        JSObject ret = new JSObject();
        ret.put("key", "This is a test for Android");
        ret.put("value", value);
        Toast.show(context, value + " - " + ret.getString("key"));
        call.success(ret);
    }
}
