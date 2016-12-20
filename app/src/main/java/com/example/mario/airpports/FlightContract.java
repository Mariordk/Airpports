package com.example.mario.airpports;

/**
 * Created by Mario on 20/12/2016.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class FlightContract {
    public static final String DB_NAME = "airpports.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "flight";

    public static final String DEFAULT_SORT = Column.FECHA_CONSULTA + " DESC";

    // Constantes del content provider
    // content://com.marakana.android.yamba.StatusProvider/status
    public static final String AUTHORITY = "com.example.mario.airpports.FlightProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String NUMERO_VUELO = "numero_vuelo";
        public static final String FECHA_VUELO = "fecha_vuelo";
        public static final String FECHA_CONSULTA = "fecha_consulta";
        public static final String ALTURA_MAXIMA = "altura_maxima";
        public static final String VELOCIDAD_MAXIMA = "velocidad_maxima";
    }
}
