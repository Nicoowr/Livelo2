package ch.livelo.livelo2.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nico on 27/10/2017.
 */

public class DataDB extends SQLiteOpenHelper {

    //Attributes
    public static final String TABLE_SENSORS_DATA = "sensors_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SENSOR_ID = "sensor_id";
    public static final String COLUMN_TIME_STAMP = "time_stamp";
    public static final String COLUMN_PRESSURE = "pressure";


    //Other info
    private static final String DATABASE_NAME = "sensors_data.db";
    private static final int DATABASE_VERSION = 1;

    //Create table
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SENSORS_DATA + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_SENSOR_ID + " TEXT, "
            + COLUMN_TIME_STAMP + " REAL, " + COLUMN_PRESSURE + " REAL); ";

    public DataDB(Context context){
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS_DATA);
        onCreate(db);
    }



}
