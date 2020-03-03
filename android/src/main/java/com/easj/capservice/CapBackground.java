package com.easj.capservice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.easj.capservice.services.SignalRService;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.ui.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

@NativePlugin()
public class CapBackground extends Plugin implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Context context;
    private Activity activity;

    // Location API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;

    // Códigos de petición
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private static final long UPDATE_INTERVAL = 1000;
    private static final long UPDATE_FASTEST_INTERVAL = UPDATE_INTERVAL / 2;

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
        requestChangeBatteryOptimizations();
        if (networkStatus()) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
        }
//        Intent intent = new Intent(context, SignalRService.class);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            activity.startForegroundService(intent);
//        } else {
//            activity.startService(intent);
//        }
        JSObject ret = new JSObject();
        ret.put("value", "Start Service");
        call.resolve(ret);
    }

    /*
     * Incluye la App en la lista blanca para ignorar el consumo de bateria cuando el teléfono este
     * inactivo
     * */
    private void requestChangeBatteryOptimizations ()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName)) {
                // intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            } else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            activity.startActivity(intent);
        }
    }

    /*
     * Valida el estado de la conexión a internet
     * */
    private boolean networkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ( networkInfo != null && networkInfo.isConnected() ) {
            return true;
        } else {
            AlertDialog dialog = new AlertDialog.Builder(activity).create();
            // dialog.setTitle("Trip 2");
            dialog.setMessage("Please activate an internet connection!");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                }
            });
            dialog.show();
            return false;
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage((FragmentActivity) activity, this)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(UPDATE_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings(){
        LocationServices.getSettingsClient(context).checkLocationSettings(mLocationSettingsRequest)
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                            if (isLocationPermissionGranted()) {
                                startLocationUpdates();
                            }
                        } catch (ApiException ex) {
                            switch (ex.getStatusCode()) {
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException resolvable = (ResolvableApiException) ex;
                                        resolvable.startResolutionForResult(
                                                activity,
                                                REQUEST_CHECK_SETTINGS
                                        );
                                    }
                                    catch (IntentSender.SendIntentException e) {}
                                    catch (ClassCastException e){}
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    startLocationUpdates();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }



    private void startLocationUpdates() {
        Intent intent = new Intent(context, SignalRService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(
                mLocationRequest, pendingIntent
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        //activity.startService(intent);
        // getLastLocation();
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void manageDeniedPermission() {
        ActivityCompat.requestPermissions(
                activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isLocationPermissionGranted()) {
            // Obtenemos la última ubicación al ser la primera vez
            //processLastLocation();
            // Iniciamos las actualizaciones de ubicación
            startLocationUpdates();
        } else {
            manageDeniedPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.show(context,"Code connection error:" + connectionResult.getErrorCode());
    }
}
