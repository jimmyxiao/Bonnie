package com.sctw.bonniedraw.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Professor on 8/18/15.
 */
public class DatabaseContract {
    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CONTENT_AUTHORITY = "tw.com.agrowood.myapplication.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class DummyTable implements BaseColumns {
        public static final String TABLE_NAME = "dummy";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + TABLE_NAME;

        public static Uri buildUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        public static String createTable() {
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY UNIQUE)";
        }

        public static String dropTable() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }
}
