package com.distributedsystems.recommendationsystemclient.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME;

public class SuggestedPoisContract {
    public static final String CONTENT_AUTHORITY = "com.distributedsystems.recommendationsystemclient";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TASKS = "pois_suggested";

    public static final class SuggestedPoisEntry implements BaseColumns {

        public static final Uri SUGGESTED_POIS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "suggested_poi";

        public static final String POI_ID = "poi_id";
        public static final String NAME = "name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String CATEGORY = "category";
        public static final String PHOTO = "photo";
    }

    // create cursor of base type directory for multiple entries
    public static final String CONTENT_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
}