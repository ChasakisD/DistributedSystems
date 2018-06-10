package com.distributedsystems.recommendationsystemclient.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Locale;

public class OpenPoiLocation {
    private static final String BASE_URL = "https://www.google.com";
    private static final String MAPS_PATH = "maps";
    private static final String KEY_PARAM = "q";

    /*
     * This method will fire off an implicit Intent to view a location
     * with given latitude and longitude on a map.
     *
     * @param geoLocation The Uri representing the location that will be opened in the map
     */
    public static void showMap(Context context, double latitude, double longitude) {
        /*
         * Again, we create an Intent with the action, ACTION_VIEW because we want to VIEW the
         * contents of this Uri.
         */
        String openAppUri = String.format(Locale.ENGLISH,
                "geo:%f,%f?q=%f,%f",
                latitude, longitude,
                latitude, longitude);

        Uri webBrowserUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(MAPS_PATH)
                .appendQueryParameter(KEY_PARAM, latitude + "%" + longitude)
                .build();

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(openAppUri));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webBrowserUri);

        try {
            context.startActivity(appIntent);
        }
        catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
