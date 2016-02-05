/*
 * Copyright (C) 2010-2012 Paul Watts (paulcwatts@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.gatech.ppl.cycleatlanta.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;

import edu.gatech.ppl.cycleatlanta.BuildConfig;

public class ObaProvider extends ContentProvider {

    /**
     * The database name cannot be changed.  It needs to remain the same to support backwards
     * compatibility with existing installed apps
     */
    private static final String DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db";

    private class OpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;

        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            bootstrapDatabase(db);
            onUpgrade(db, 12, DATABASE_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(
                    "CREATE TABLE " +
                            ObaContract.Regions.PATH + " (" +
                            ObaContract.Regions._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            ObaContract.Regions.NAME + " VARCHAR NOT NULL, " +
                            ObaContract.Regions.OBA_BASE_URL + " VARCHAR NOT NULL, " +
                            ObaContract.Regions.SIRI_BASE_URL + " VARCHAR NOT NULL, " +
                            ObaContract.Regions.LANGUAGE + " VARCHAR NOT NULL, " +
                            ObaContract.Regions.CONTACT_EMAIL + " VARCHAR NOT NULL, " +
                            ObaContract.Regions.SUPPORTS_OBA_DISCOVERY + " INTEGER NOT NULL, " +
                            ObaContract.Regions.SUPPORTS_OBA_REALTIME + " INTEGER NOT NULL, " +
                            ObaContract.Regions.SUPPORTS_SIRI_REALTIME + " INTEGER NOT NULL, " +
                            ObaContract.Regions.TWITTER_URL + " INTEGER NOT NULL, " +
                            ObaContract.Regions.EXPERIMENTAL + " INTEGER NOT NULL, " +
                            ObaContract.Regions.TUTORIAL_URL + " INTEGER NOT NULL " +
                            ");");
            db.execSQL(
                    "CREATE TABLE " +
                            ObaContract.RegionBounds.PATH + " (" +
                            ObaContract.RegionBounds._ID
                            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            ObaContract.RegionBounds.REGION_ID + " INTEGER NOT NULL, " +
                            ObaContract.RegionBounds.LATITUDE + " REAL NOT NULL, " +
                            ObaContract.RegionBounds.LONGITUDE + " REAL NOT NULL, " +
                            ObaContract.RegionBounds.LAT_SPAN + " REAL NOT NULL, " +
                            ObaContract.RegionBounds.LON_SPAN + " REAL NOT NULL " +
                            ");");
            db.execSQL("DROP TRIGGER IF EXISTS region_bounds_cleanup");
            db.execSQL(
                    "CREATE TRIGGER region_bounds_cleanup DELETE ON " + ObaContract.Regions.PATH
                            +
                            " BEGIN " +
                            "DELETE FROM " + ObaContract.RegionBounds.PATH +
                            " WHERE " + ObaContract.RegionBounds.REGION_ID + " = old."
                            + ObaContract.Regions._ID +
                            ";" +
                            "END");
        }

        private void dropTables(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + ObaContract.Regions.PATH);
            db.execSQL("DROP TABLE IF EXISTS " + ObaContract.RegionBounds.PATH);
        }
    }

    private static final int REGIONS = 12;

    private static final int REGIONS_ID = 13;

    private static final int REGION_BOUNDS = 14;

    private static final int REGION_BOUNDS_ID = 15;

    private static final UriMatcher sUriMatcher;

    private static final HashMap<String, String> sRegionsProjectionMap;

    private static final HashMap<String, String> sRegionBoundsProjectionMap;

    // Insert helpers are useful.
    private DatabaseUtils.InsertHelper mRegionsInserter;

    private DatabaseUtils.InsertHelper mRegionBoundsInserter;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ObaContract.AUTHORITY, ObaContract.Regions.PATH, REGIONS);
        sUriMatcher.addURI(ObaContract.AUTHORITY, ObaContract.Regions.PATH + "/#", REGIONS_ID);
        sUriMatcher.addURI(ObaContract.AUTHORITY, ObaContract.RegionBounds.PATH, REGION_BOUNDS);
        sUriMatcher.addURI(ObaContract.AUTHORITY, ObaContract.RegionBounds.PATH + "/#",
                REGION_BOUNDS_ID);


        sRegionsProjectionMap = new HashMap<String, String>();
        sRegionsProjectionMap.put(ObaContract.Regions._ID, ObaContract.Regions._ID);
        sRegionsProjectionMap.put(ObaContract.Regions.NAME, ObaContract.Regions.NAME);
        sRegionsProjectionMap
                .put(ObaContract.Regions.OBA_BASE_URL, ObaContract.Regions.OBA_BASE_URL);
        sRegionsProjectionMap
                .put(ObaContract.Regions.SIRI_BASE_URL, ObaContract.Regions.SIRI_BASE_URL);
        sRegionsProjectionMap.put(ObaContract.Regions.LANGUAGE, ObaContract.Regions.LANGUAGE);
        sRegionsProjectionMap
                .put(ObaContract.Regions.CONTACT_EMAIL, ObaContract.Regions.CONTACT_EMAIL);
        sRegionsProjectionMap.put(ObaContract.Regions.SUPPORTS_OBA_DISCOVERY,
                ObaContract.Regions.SUPPORTS_OBA_DISCOVERY);
        sRegionsProjectionMap.put(ObaContract.Regions.SUPPORTS_OBA_REALTIME,
                ObaContract.Regions.SUPPORTS_OBA_REALTIME);
        sRegionsProjectionMap.put(ObaContract.Regions.SUPPORTS_SIRI_REALTIME,
                ObaContract.Regions.SUPPORTS_SIRI_REALTIME);
        sRegionsProjectionMap.put(ObaContract.Regions.TWITTER_URL, ObaContract.Regions.TWITTER_URL);
        sRegionsProjectionMap.put(ObaContract.Regions.TUTORIAL_URL, ObaContract.Regions.TUTORIAL_URL);
        sRegionsProjectionMap
                .put(ObaContract.Regions.EXPERIMENTAL, ObaContract.Regions.EXPERIMENTAL);

        sRegionBoundsProjectionMap = new HashMap<String, String>();
        sRegionBoundsProjectionMap.put(ObaContract.RegionBounds._ID, ObaContract.RegionBounds._ID);
        sRegionBoundsProjectionMap
                .put(ObaContract.RegionBounds.REGION_ID, ObaContract.RegionBounds.REGION_ID);
        sRegionBoundsProjectionMap
                .put(ObaContract.RegionBounds.LATITUDE, ObaContract.RegionBounds.LATITUDE);
        sRegionBoundsProjectionMap
                .put(ObaContract.RegionBounds.LONGITUDE, ObaContract.RegionBounds.LONGITUDE);
        sRegionBoundsProjectionMap
                .put(ObaContract.RegionBounds.LAT_SPAN, ObaContract.RegionBounds.LAT_SPAN);
        sRegionBoundsProjectionMap
                .put(ObaContract.RegionBounds.LON_SPAN, ObaContract.RegionBounds.LON_SPAN);

    }

    private SQLiteDatabase mDb;

    private OpenHelper mOpenHelper;

    public static File getDatabasePath(Context context) {
        return context.getDatabasePath(DATABASE_NAME);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new OpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case REGIONS:
                return ObaContract.Regions.CONTENT_DIR_TYPE;
            case REGIONS_ID:
                return ObaContract.Regions.CONTENT_TYPE;
            case REGION_BOUNDS:
                return ObaContract.RegionBounds.CONTENT_DIR_TYPE;
            case REGION_BOUNDS_ID:
                return ObaContract.RegionBounds.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            Uri result = insertInternal(db, uri, values);
            getContext().getContentResolver().notifyChange(uri, null);
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = getDatabase();
        return queryInternal(db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            int result = updateInternal(db, uri, values, selection, selectionArgs);
            if (result > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            int result = deleteInternal(db, uri, selection, selectionArgs);
            if (result > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    private Uri insertInternal(SQLiteDatabase db, Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        String id;
        Uri result;
        long longId;

        switch (match) {

            case REGIONS:
                longId = mRegionsInserter.insert(values);
                result = ContentUris.withAppendedId(ObaContract.Regions.CONTENT_URI, longId);
                return result;

            case REGION_BOUNDS:
                longId = mRegionBoundsInserter.insert(values);
                result = ContentUris.withAppendedId(ObaContract.RegionBounds.CONTENT_URI, longId);
                return result;

            // What would these mean, anyway??
            case REGIONS_ID:
            case REGION_BOUNDS_ID:
                throw new UnsupportedOperationException("Cannot insert to this URI: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private Cursor queryInternal(SQLiteDatabase db,
                                 Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        final String limit = uri.getQueryParameter("limit");

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (match) {

            case REGIONS:
                qb.setTables(ObaContract.Regions.PATH);
                qb.setProjectionMap(sRegionsProjectionMap);
                return qb.query(mDb, projection, selection, selectionArgs,
                        null, null, sortOrder, limit);

            case REGIONS_ID:
                qb.setTables(ObaContract.Regions.PATH);
                qb.setProjectionMap(sRegionsProjectionMap);
                qb.appendWhere(ObaContract.Regions._ID);
                qb.appendWhere("=");
                qb.appendWhere(String.valueOf(ContentUris.parseId(uri)));
                return qb.query(mDb, projection, selection, selectionArgs,
                        null, null, sortOrder, limit);

            case REGION_BOUNDS:
                qb.setTables(ObaContract.RegionBounds.PATH);
                qb.setProjectionMap(sRegionBoundsProjectionMap);
                return qb.query(mDb, projection, selection, selectionArgs,
                        null, null, sortOrder, limit);

            case REGION_BOUNDS_ID:
                qb.setTables(ObaContract.RegionBounds.PATH);
                qb.setProjectionMap(sRegionBoundsProjectionMap);
                qb.appendWhere(ObaContract.RegionBounds._ID);
                qb.appendWhere("=");
                qb.appendWhere(String.valueOf(ContentUris.parseId(uri)));
                return qb.query(mDb, projection, selection, selectionArgs,
                        null, null, sortOrder, limit);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private int updateInternal(SQLiteDatabase db,
                               Uri uri, ContentValues values, String selection,
                               String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REGIONS:
                return db.update(ObaContract.Regions.PATH, values, selection, selectionArgs);

            case REGIONS_ID:
                return db.update(ObaContract.Regions.PATH, values,
                        whereLong(ObaContract.Regions._ID, uri), selectionArgs);

            case REGION_BOUNDS:
                return db.update(ObaContract.RegionBounds.PATH, values, selection, selectionArgs);

            case REGION_BOUNDS_ID:
                return db.update(ObaContract.RegionBounds.PATH, values,
                        whereLong(ObaContract.RegionBounds._ID, uri), selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private int deleteInternal(SQLiteDatabase db,
                               Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case REGIONS:
                return db.delete(ObaContract.Regions.PATH, selection, selectionArgs);

            case REGIONS_ID:
                return db.delete(ObaContract.Regions.PATH,
                        whereLong(ObaContract.Regions._ID, uri), selectionArgs);

            case REGION_BOUNDS:
                return db.delete(ObaContract.RegionBounds.PATH, selection, selectionArgs);

            case REGION_BOUNDS_ID:
                return db.delete(ObaContract.RegionBounds.PATH,
                        whereLong(ObaContract.RegionBounds._ID, uri), selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private String where(String column, Uri uri) {
        StringBuilder sb = new StringBuilder();
        sb.append(column);
        sb.append('=');
        DatabaseUtils.appendValueToSql(sb, uri.getLastPathSegment());
        return sb.toString();
    }

    private String whereLong(String column, Uri uri) {
        StringBuilder sb = new StringBuilder();
        sb.append(column);
        sb.append('=');
        sb.append(String.valueOf(ContentUris.parseId(uri)));
        return sb.toString();
    }

    private SQLiteDatabase getDatabase() {
        if (mDb == null) {
            mDb = mOpenHelper.getWritableDatabase();
            // Initialize the insert helpers
            mRegionsInserter = new DatabaseUtils.InsertHelper(mDb, ObaContract.Regions.PATH);
            mRegionBoundsInserter = new DatabaseUtils.InsertHelper(mDb,
                    ObaContract.RegionBounds.PATH);
        }
        return mDb;
    }

    //
    // Closes the database
    //
    public void closeDB() {
        mOpenHelper.close();
        mDb = null;
    }
}
