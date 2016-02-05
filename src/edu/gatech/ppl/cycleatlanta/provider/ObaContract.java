/*
 * Copyright (C) 2010-2015 Paul Watts (paulcwatts@gmail.com),
 * University of South Florida (sjbarbeau@gmail.com),
 * Benjamin Du (bendu@me.com)
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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import edu.gatech.ppl.cycleatlanta.BuildConfig;
import edu.gatech.ppl.cycleatlanta.region.elements.ObaRegion;
import edu.gatech.ppl.cycleatlanta.region.elements.ObaRegionElement;

/**
 * The contract between clients and the ObaProvider.
 *
 * This really needs to be documented better.
 *
 * NOTE: The AUTHORITY names in this class cannot be changed.  They need to stay under the
 * BuildConfig.DATABASE_AUTHORITY namespace (for the original OBA brand, "com.joulespersecond.oba")
 * namespace to support backwards compatibility with existing installed apps
 *
 * @author paulw
 */
public final class ObaContract {

    public static final String TAG = "ObaContract";

    /** The authority portion of the URI - defined in build.gradle */
    public static final String AUTHORITY = BuildConfig.DATABASE_AUTHORITY;

    /** The base URI for the Oba provider */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface RegionsColumns {

        /**
         * The name of the region.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String NAME = "name";

        /**
         * The base OBA URL.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String OBA_BASE_URL = "oba_base_url";

        /**
         * The base SIRI URL.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String SIRI_BASE_URL = "siri_base_url";

        /**
         * The locale of the API server.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String LANGUAGE = "lang";

        /**
         * The email of the person responsible for this server.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String CONTACT_EMAIL = "contact_email";

        /**
         * Whether or not the server supports OBA discovery APIs.
         * <P>
         * Type: BOOLEAN
         * </P>
         */
        public static final String SUPPORTS_OBA_DISCOVERY = "supports_api_discovery";

        /**
         * Whether or not the server supports OBA realtime APIs.
         * <P>
         * Type: BOOLEAN
         * </P>
         */
        public static final String SUPPORTS_OBA_REALTIME = "supports_api_realtime";

        /**
         * Whether or not the server supports SIRI realtime APIs.
         * <P>
         * Type: BOOLEAN
         * </P>
         */
        public static final String SUPPORTS_SIRI_REALTIME = "supports_siri_realtime";

        /**
         * The Twitter URL for the region.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String TWITTER_URL = "twitter_url";

        /**
         * Whether or not the server is experimental (i.e., not production).
         * <P>
         * Type: BOOLEAN
         * </P>
         */
        public static final String EXPERIMENTAL = "experimental";

        /**
         * The StopInfo URL for the region (see #103)
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String TUTORIAL_URL = "tutorial_url";
    }

    protected interface RegionBoundsColumns {

        /**
         * The region ID
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String REGION_ID = "region_id";

        /**
         * The latitude center of the agencies coverage area
         * <P>
         * Type: REAL
         * </P>
         */
        public static final String LATITUDE = "lat";

        /**
         * The longitude center of the agencies coverage area
         * <P>
         * Type: REAL
         * </P>
         */
        public static final String LONGITUDE = "lon";

        /**
         * The height of the agencies bounding box
         * <P>
         * Type: REAL
         * </P>
         */
        public static final String LAT_SPAN = "lat_span";

        /**
         * The width of the agencies bounding box
         * <P>
         * Type: REAL
         * </P>
         */
        public static final String LON_SPAN = "lon_span";

    }

    public static class Regions implements BaseColumns, RegionsColumns {

        // Cannot be instantiated
        private Regions() {
        }

        /** The URI path portion for this table */
        public static final String PATH = "regions";

        /**
         * The content:// style URI for this table URI is of the form
         * content://<authority>/regions/<id>
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                AUTHORITY_URI, PATH);

        public static final String CONTENT_TYPE
                = "vnd.android.cursor.item/" + BuildConfig.DATABASE_AUTHORITY + ".region";

        public static final String CONTENT_DIR_TYPE
                = "vnd.android.dir/" + BuildConfig.DATABASE_AUTHORITY + ".region";

        public static final Uri buildUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri insertOrUpdate(Context context,
                int id,
                ContentValues values) {
            return insertOrUpdate(context.getContentResolver(), id, values);
        }

        public static Uri insertOrUpdate(ContentResolver cr,
                int id,
                ContentValues values) {
            final Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
            Cursor c = cr.query(uri, new String[]{}, null, null, null);
            Uri result;
            if (c != null && c.getCount() > 0) {
                cr.update(uri, values, null, null);
                result = uri;
            } else {
                values.put(_ID, id);
                result = cr.insert(CONTENT_URI, values);
            }
            if (c != null) {
                c.close();
            }
            return result;
        }

        public static ObaRegion get(Context context, int id) {
            return get(context.getContentResolver(), id);
        }

        public static ObaRegion get(ContentResolver cr, int id) {
            final String[] PROJECTION = {
                    _ID,
                    NAME,
                    OBA_BASE_URL,
                    SIRI_BASE_URL,
                    LANGUAGE,
                    CONTACT_EMAIL,
                    SUPPORTS_OBA_DISCOVERY,
                    SUPPORTS_OBA_REALTIME,
                    SUPPORTS_SIRI_REALTIME,
                    TWITTER_URL,
                    EXPERIMENTAL,
                    TUTORIAL_URL

            };

            Cursor c = cr.query(buildUri((int) id), PROJECTION, null, null, null);
            if (c != null) {
                try {
                    if (c.getCount() == 0) {
                        return null;
                    }
                    c.moveToFirst();
                    return new ObaRegionElement(id,   // id
                            c.getString(1),             // Name
                            true,                       // Active
                            c.getString(2),             // OBA Base URL
                            c.getString(3),             // SIRI Base URL
                            RegionBounds.getRegion(cr, id), // Bounds
                            c.getString(4),             // Lang
                            c.getString(5),             // Contact Email
                            c.getInt(6) > 0,            // Supports Oba Discovery
                            c.getInt(7) > 0,            // Supports Oba Realtime
                            c.getInt(8) > 0,            // Supports Siri Realtime
                            c.getString(9),              // Twitter URL
                            c.getInt(10) > 0,               // Experimental
                            c.getString(9),              // StopInfoUrl
                            c.getString(11)
                    );
                } finally {
                    c.close();
                }
            }
            return null;
        }
    }

    public static class RegionBounds implements BaseColumns, RegionBoundsColumns {

        // Cannot be instantiated
        private RegionBounds() {
        }

        /** The URI path portion for this table */
        public static final String PATH = "region_bounds";

        /**
         * The content:// style URI for this table URI is of the form
         * content://<authority>/region_bounds/<id>
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                AUTHORITY_URI, PATH);

        public static final String CONTENT_TYPE
                = "vnd.android.cursor.item/" + BuildConfig.DATABASE_AUTHORITY + ".region_bounds";

        public static final String CONTENT_DIR_TYPE
                = "vnd.android.dir/" + BuildConfig.DATABASE_AUTHORITY + ".region_bounds";

        public static final Uri buildUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static ObaRegionElement.Bounds[] getRegion(ContentResolver cr, int regionId) {
            final String[] PROJECTION = {
                    LATITUDE,
                    LONGITUDE,
                    LAT_SPAN,
                    LON_SPAN
            };
            Cursor c = cr.query(CONTENT_URI, PROJECTION,
                    "(" + RegionBounds.REGION_ID + " = " + regionId + ")",
                    null, null);
            if (c != null) {
                try {
                    ObaRegionElement.Bounds[] results = new ObaRegionElement.Bounds[c.getCount()];
                    if (c.getCount() == 0) {
                        return results;
                    }

                    int i = 0;
                    c.moveToFirst();
                    do {
                        results[i] = new ObaRegionElement.Bounds(
                                c.getDouble(0),
                                c.getDouble(1),
                                c.getDouble(2),
                                c.getDouble(3));
                        i++;
                    } while (c.moveToNext());

                    return results;
                } finally {
                    c.close();
                }
            }
            return null;
        }
    }
}
