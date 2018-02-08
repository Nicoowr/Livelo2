package ch.livelo.livelo2.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nico on 27/10/2017.
 */

public class SensorDB extends SQLiteOpenHelper {

    //Attributes
    public static int NB_OF_FIELDS = 10;
    public static final String TABLE_SENSORS = "sensors";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SENSOR_ID = "Sensor_id";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";
    public static final String COLUMN_DEPTH = "Depth";
    public static final String COLUMN_PERIOD = "Period";
    public static final String COLUMN_LASTSTART = "laststart";
    public static final String COLUMN_DATANB = "datanb";
    public static final String COLUMN_LASTCOLLECT = "lastcollect";
    public static final String COLUMN_LASTCOLLECTDATANB = "lastcollectdatanb";

    public static String[] allColumns = { COLUMN_ID, COLUMN_NAME,
            COLUMN_SENSOR_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE,
            COLUMN_DEPTH, COLUMN_PERIOD, COLUMN_LASTSTART, COLUMN_DATANB, COLUMN_LASTCOLLECT, COLUMN_LASTCOLLECTDATANB};

    //Other info
    protected static final String DATABASE_NAME = "sensors.db";
    protected static final int DATABASE_VERSION = 1;

    //Create table
    protected static final String DATABASE_CREATE = "create table "
            + TABLE_SENSORS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_SENSOR_ID + " TEXT, "
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL,"
            + COLUMN_DEPTH + " REAL, "
            + COLUMN_PERIOD + " REAL, "
            + COLUMN_LASTSTART + " REAL, "
            + COLUMN_DATANB + " INTEGER, "
            + COLUMN_LASTCOLLECT + " INTEGER, "
            + COLUMN_LASTCOLLECTDATANB + " INTEGER);";

    public SensorDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 //       Log.w(DataDB.class.getName(),
 //               "Upgrading database from version " + oldVersion + " to "
 //                       + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        onCreate(db);
    }



}
