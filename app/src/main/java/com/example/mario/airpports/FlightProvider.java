package com.example.mario.airpports;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mario on 20/12/2016.
 */

public class FlightProvider extends ContentProvider {

    private static final String TAG = FlightContract.class.getSimpleName();
    private DbHelper dbHelper;
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(FlightContract.AUTHORITY, FlightContract.TABLE , FlightContract.STATUS_DIR);
        sURIMatcher.addURI(FlightContract.AUTHORITY, FlightContract.TABLE + "/#",
                FlightContract.STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri ret = null;
        // Nos aseguramos de que la URI es correcta
        if (sURIMatcher.match(uri) != FlightContract.STATUS_DIR) {
            throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(FlightContract.TABLE, null,
                contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        // Se inserto correctamente?
        if (rowId != -1) {
            long id = contentValues.getAsLong(FlightContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "uri insertada: " + ret);
            // Notificar que los datos para la URI han cambiado
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ret;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
