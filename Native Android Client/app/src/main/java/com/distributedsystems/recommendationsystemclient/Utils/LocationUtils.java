package com.distributedsystems.recommendationsystemclient.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

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

    private static boolean IsConnected(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager == null) return false;

        Network[] networks = connectivityManager.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network mNetwork : networks) {
            networkInfo = connectivityManager.getNetworkInfo(mNetwork);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                return true;
            }
        }
        return false;
    }
}
