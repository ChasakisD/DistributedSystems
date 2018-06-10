package com.distributedsystems.recommendationsystemclient.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SuggestedPoisDbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "FavoriteMovie.db";
        private static final int DATABASE_VERSION = 1;

        SuggestedPoisDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String SQL_CREATE_TABLE = "CREATE TABLE " +
                    SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME + " (" +
                    SuggestedPoisContract.SuggestedPoisEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SuggestedPoisContract.SuggestedPoisEntry.POI_ID + " TEXT, " +
                    SuggestedPoisContract.SuggestedPoisEntry.NAME + " TEXT, " +
                    SuggestedPoisContract.SuggestedPoisEntry.LATITUDE + " TEXT, " +
                    SuggestedPoisContract.SuggestedPoisEntry.LONGITUDE + " TEXT, " +
                    SuggestedPoisContract.SuggestedPoisEntry.CATEGORY + " TEXT, " +
                    SuggestedPoisContract.SuggestedPoisEntry.PHOTO + " TEXT " +
                    ");";

            sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");

            final String SQL_DROP_FAVORITE_MOVIE_TABLE = "DROP TABLE IF EXISTS " +
                    SuggestedPoisContract.SuggestedPoisEntry.TABLE_NAME;

            sqLiteDatabase.execSQL(SQL_DROP_FAVORITE_MOVIE_TABLE);

            onCreate(sqLiteDatabase);
        }
    }