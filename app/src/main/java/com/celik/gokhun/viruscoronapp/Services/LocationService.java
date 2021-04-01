package com.celik.gokhun.viruscoronapp.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationListener locationListener;
    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /// getting longitudes ands latitudes
                Intent intent = new Intent("location_update");
                intent.putExtra("LONGITUDE", location.getLongitude());
                intent.putExtra("LATITUDE", location.getLatitude());
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (locationManager != null)
        {
            locationManager.removeUpdates(locationListener);
        }
    }
}


