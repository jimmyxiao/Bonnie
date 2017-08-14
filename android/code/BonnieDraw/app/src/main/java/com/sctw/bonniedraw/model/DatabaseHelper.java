package com.sctw.bonniedraw.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sctw.bonniedraw.model.DatabaseContract.DATABASE_NAME;
import static com.sctw.bonniedraw.model.DatabaseContract.DATABASE_VERSION;
import static com.sctw.bonniedraw.model.DatabaseContract.DummyTable;


/**
 * Created by Professor on 8/21/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null) databaseHelper = new DatabaseHelper(context);
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DummyTable.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DummyTable.dropTable());
    }
}
