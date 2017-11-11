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
    public static int NB_OF_FIELDS = 9;
    public static final String TABLE_SENSORS = "sensors";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SENSOR_ID = "sensor_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_DEPTH = "depth";
    public static final String COLUMN_FREQUENCY = "frequency";
    public static final String COLUMN_DATANB = "datanb";
    public static final String COLUMN_LASTCOLLECT = "lastcollect";

    public static String[] allColumns = { COLUMN_ID, COLUMN_NAME,
            COLUMN_SENSOR_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE,
            COLUMN_DEPTH, COLUMN_FREQUENCY, COLUMN_DATANB, COLUMN_LASTCOLLECT};

    //Other info
    protected static final String DATABASE_NAME = "sensors.db";
    protected static final int DATABASE_VERSION = 3;

    //Create table
    protected static final String DATABASE_CREATE = "create table "
            + TABLE_SENSORS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME + " TEXT, "
            + COLUMN_SENSOR_ID + " TEXT, " + COLUMN_LATITUDE + " REAL, " + COLUMN_LONGITUDE + " REAL,"
            + COLUMN_DEPTH + " REAL, " + COLUMN_FREQUENCY + " REAL, " + COLUMN_DATANB + " INTEGER, "
            + COLUMN_LASTCOLLECT + " INTEGER);";

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
