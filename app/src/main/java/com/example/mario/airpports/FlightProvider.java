package com.example.mario.airpports;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case FlightContract.STATUS_DIR:
                where = selection;
                break;
            case FlightContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = FlightContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        String orderBy = (TextUtils.isEmpty(sortOrder))
                ? FlightContract.DEFAULT_SORT
                : sortOrder;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FlightContract.TABLE, projection, where, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAG, "registros recuperados: " + cursor.getCount());
        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case FlightContract.STATUS_DIR:
                Log.d(TAG, "gotType: vnd.android.cursor.dir/vnd.com.example.mario.airpports.provider.flight");
                return "vnd.android.cursor.dir/vnd.com.example.mario.airpports.provider.flight";
            case FlightContract.STATUS_ITEM:
                Log.d(TAG, "gotType: vnd.android.cursor.item/vnd.com.example.mario.airpports.provider.flight");
                return "vnd.android.cursor.item/vnd.com.example.mario.airpports.provider.flight";
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

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
        long rowId = db.insert(FlightContract.TABLE, null,
                contentValues);
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case FlightContract.STATUS_DIR:
                where = selection;
                break;
            case FlightContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = FlightContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(FlightContract.TABLE, where, selectionArgs);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros borrados: " + ret);
        return ret;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case FlightContract.STATUS_DIR:
                where = selection;
                break;
            case FlightContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = FlightContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(FlightContract.TABLE, values, where, selectionArgs);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros actualizados: " + ret);
        return ret;

    }
}
