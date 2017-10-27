package ch.livelo.livelo2.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 27/10/2017.
 */

public class DataDAO {


    // Si je décide de la mettre à jour, il faudra changer cet attribut
    protected final static int VERSION = 1;
    // Le nom du fichier qui représente ma base
    protected final static String NOM = "sensors_data.db";

    protected SQLiteDatabase mDb = null;
    protected DataDB mDataDB = null;

    public DataDAO(Context context) {
        this.mDataDB = new DataDB(context);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        mDb = mDataDB.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }
    ///////////////////////////Operations////////////////////////

    public void addData(String sensor_id, double time_stamp, double pressure) {
        ContentValues values = new ContentValues();
        values.put(DataDB.COLUMN_SENSOR_ID, sensor_id);
        values.put(DataDB.COLUMN_TIME_STAMP, time_stamp);
        values.put(DataDB.COLUMN_PRESSURE, pressure);
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

    public void deleteSensorData(String sensor_id) {
        mDb.delete(DataDB.TABLE_SENSORS_DATA, DataDB.COLUMN_SENSOR_ID + " = ?", new String[] {sensor_id});
        //TODO : add a toast
    }
}
