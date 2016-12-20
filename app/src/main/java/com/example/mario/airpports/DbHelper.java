package com.example.mario.airpports;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
 * Created by Mario on 20/12/2016.
 */

public class DbHelper extends SQLiteOpenHelper{

    private static final String TAG = DbHelper.class.getSimpleName();
    public DbHelper(Context context) {
        super(context, FlightContract.DB_NAME, null, FlightContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s int primary key, %s text, %s text, %s int, %s int, %s int)",
                FlightContract.TABLE,
                FlightContract.Column.ID,
                FlightContract.Column.NUMERO_VUELO,
                FlightContract.Column.FECHA_VUELO,
                FlightContract.Column.FECHA_CONSULTA,
                FlightContract.Column.ALTURA_MAXIMA,
                FlightContract.Column.VELOCIDAD_MAXIMA);
        Log.d(TAG, "onCreate con SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Aqui irían las sentencias del tipo ALTER TABLE, de momento lo hacemos mas sencillo:
        db.execSQL("drop table if exists " + FlightContract.TABLE); // borra la vieja base de datos
        onCreate(db); // crea una base de datos nueva
        Log.d(TAG, "onUpgrade");
    }
}
