package com.distributedsystems.recommendationsystemclient.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {

    @SuppressLint("MissingPermission")
    public static Location GetLastKnownLocation(Context context){
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        if(locationManager == null) return null;
        if (!PermissionUtils.ArePermissionsGranted(context)) return null;

        /* PermissionUtils just checked for permissions, but compiler does not know it */
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
}
