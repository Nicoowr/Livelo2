package ch.livelo.livelo2.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by Nico on 27/10/2017.
 */

public class SensorDAO {


    // Si je décide de la mettre à jour, il faudra changer cet attribut
    protected final static int VERSION = 1;
    // Le nom du fichier qui représente ma base
    protected final static String NOM = "sensors.db";

    protected SQLiteDatabase mDb = null;
    protected SensorDB mSensorDB = null;

    public SensorDAO(Context context) {
        this.mSensorDB = new SensorDB(context);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        mDb = mSensorDB.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    //////////////////////Operations/////////////////////////

    public void addSensor(String name, String sensor_id, double latitude, double longitude, double depth) {
        ContentValues values = new ContentValues();
        values.put(SensorDB.COLUMN_NAME, name);
        values.put(SensorDB.COLUMN_SENSOR_ID, sensor_id);
        values.put(SensorDB.COLUMN_LATITUDE, latitude);
        values.put(SensorDB.COLUMN_LONGITUDE, longitude);
        values.put(SensorDB.COLUMN_DEPTH, depth);
        long insertId = mDb.insert(DataDB.TABLE_SENSORS_DATA, null,
                values);
        /*Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();*/
        return;
    }

    public void deleteSensor(String sensor_id) {
        mDb.delete(SensorDB.TABLE_SENSORS, SensorDB.COLUMN_SENSOR_ID + " = ?", new String[] {sensor_id});
        //TODO : add a toast
    }
}
