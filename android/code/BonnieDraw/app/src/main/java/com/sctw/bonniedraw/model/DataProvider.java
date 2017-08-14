package com.sctw.bonniedraw.model;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.sctw.bonniedraw.model.DatabaseContract.CONTENT_AUTHORITY;
import static com.sctw.bonniedraw.model.DatabaseContract.DummyTable;

/**
 * Created by Professor on 8/21/15.
 */
public class DataProvider extends ContentProvider {
    private static final int URI_DUMMY = 0;
    private UriMatcher uriMatcher;
    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        uriMatcher = buildUriMatcher();
        databaseHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case URI_DUMMY:
                cursor = sqLiteDatabase.query(DummyTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Uri doesn't match any defined Uris on query");
        }
        Context context = getContext();
        if (context != null) cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_DUMMY:
                return DummyTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Uri doesn't match any defined Uris on getType");
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case URI_DUMMY:
                id = sqLiteDatabase.insertWithOnConflict(DummyTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            default:
                throw new UnsupportedOperationException("Uri doesn't match any defined Uris on insert");
        }
        Uri insertedUri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        notifyChange(id, insertedUri);
        return insertedUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int rowsInserted = 0;
        if (values.length > 0) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            long id;
            switch (uriMatcher.match(uri)) {
                case URI_DUMMY:
                    sqLiteDatabase.beginTransaction();
                    try {
                        for (ContentValues contentValues : values) {
                            id = sqLiteDatabase.insertWithOnConflict(DummyTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                            if (id != -1) rowsInserted++;
                        }
                        sqLiteDatabase.setTransactionSuccessful();
                    } finally {
                        sqLiteDatabase.endTransaction();
                    }
                    notifyChange(rowsInserted, uri);
                    break;
                default:
                    throw new UnsupportedOperationException("Uri doesn't match any defined Uris on bulkInsert");
            }
        }
        return rowsInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_DUMMY:
                return sqLiteDatabase.delete(DummyTable.TABLE_NAME, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Uri doesn't match any defined Uris on delete");
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_DUMMY:
                return sqLiteDatabase.update(DummyTable.TABLE_NAME, values, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Uri doesn't match any defined Uris on update");
        }
    }

    private void notifyChange(int rows, Uri... uris) {
        Context context = getContext();
        if (context != null && rows > 0) {
            ContentResolver contentResolver = context.getContentResolver();
            for (Uri uri : uris)
                contentResolver.notifyChange(uri, null);
        }
    }

    private void notifyChange(long id, Uri... uris) {
        Context context = getContext();
        if (context != null && id >= 0) {
            ContentResolver contentResolver = context.getContentResolver();
            for (Uri uri : uris)
                contentResolver.notifyChange(uri, null);
        }
    }

    private UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, DummyTable.TABLE_NAME, URI_DUMMY);
        return uriMatcher;
    }
}
