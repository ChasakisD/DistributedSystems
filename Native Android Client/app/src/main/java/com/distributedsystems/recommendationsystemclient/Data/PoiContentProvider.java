package com.distributedsystems.recommendationsystemclient.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PoiContentProvider extends ContentProvider{

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private SuggestedPoisDbHelper dbHelper;

    /*
        Codes for the UriMatcher
     */
    private static final int POIS_TABLE = 100;

    @Override
    public boolean onCreate(){
        dbHelper = new SuggestedPoisDbHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // add a code for each type of URI
        matcher.addURI(SuggestedPoisContract.CONTENT_AUTHORITY,
                SuggestedPoisContract.PATH_TASKS, POIS_TABLE);

        return matcher;
    }

    @Override
    public String getType(Uri uri){
        final int match = uriMatcher.match(uri);

        switch (match){
            case POIS_TABLE:{
                return SuggestedPoisContract.CONTENT_DIR_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }


    /*
        Called when adding a movie to favorites
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the database
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // find the corresponding uri depending the match
        int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POIS_TABLE:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME,
                        null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(SuggestedPoisContract.SuggestedPoisEntry.SUGGESTED_POIS_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " +
                            SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri used for insertion: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        if(getContext() != null) getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        // find the corresponding uri depending the match
        int match = uriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the favorites directory (results fragment)
            case POIS_TABLE:
                retCursor = db.query(SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        if(getContext() != null) retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    /*
        Called when removing a movie from the favorites
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int tasksDeleted;

        // Get access to the database
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // find the corresponding uri depending the match
        int match = uriMatcher.match(uri);

        switch (match) {
            /* for future use, in case adding delete all pois */
            case POIS_TABLE:
                tasksDeleted = db.delete(SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME, "1", null);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    /* ignore this for now, we don't need to update the pois db */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}