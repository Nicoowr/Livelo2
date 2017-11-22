package ch.livelo.livelo2.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 27/10/2017.
 */

public class SensorDAO {


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
        values.put(SensorDB.COLUMN_FREQUENCY, 0);
        values.put(SensorDB.COLUMN_DATANB, 0);
        values.put(SensorDB.COLUMN_LASTCOLLECT, 0);
        long insertId = mDb.insert(SensorDB.TABLE_SENSORS, null,
                values);
        return;
    }

    public void addSensor(Sensor sensor) {
        ContentValues values = new ContentValues();
        values.put(SensorDB.COLUMN_NAME, sensor.getName());
        values.put(SensorDB.COLUMN_SENSOR_ID, sensor.getId());
        values.put(SensorDB.COLUMN_LATITUDE, sensor.getLatitude());
        values.put(SensorDB.COLUMN_LONGITUDE, sensor.getLongitude());
        values.put(SensorDB.COLUMN_DEPTH, sensor.getDepth());
        values.put(SensorDB.COLUMN_FREQUENCY, 0);
        values.put(SensorDB.COLUMN_DATANB, 0);
        values.put(SensorDB.COLUMN_LASTCOLLECT, 0);
        long insertId = mDb.insert(SensorDB.TABLE_SENSORS, null,
                values);
        return;
    }

    public void updateSensor(String sensor_id, double latitude, double longitude, double depth, double frequency, long dataNb, long lastCollect){
        //-1 means no change
        Sensor sensor = this.getSensor(sensor_id);
        ContentValues cv = new ContentValues();
        if(latitude != -1)
            cv.put(SensorDB.COLUMN_LATITUDE, latitude);
        if(longitude != -1)
            cv.put(SensorDB.COLUMN_LONGITUDE, longitude);
        if(depth != -1)
            cv.put(SensorDB.COLUMN_DEPTH, depth);
        if(frequency != -1)
            cv.put(SensorDB.COLUMN_FREQUENCY,frequency);
        if(dataNb != -1)
            cv.put(SensorDB.COLUMN_DATANB,dataNb + sensor.getDataNb());
        if(dataNb == 0)
            cv.put(SensorDB.COLUMN_DATANB,dataNb);//Case where you reinitialize
        if(lastCollect != -1)
            cv.put(SensorDB.COLUMN_LASTCOLLECT,lastCollect);

        mDb.update(SensorDB.TABLE_SENSORS, cv, SensorDB.COLUMN_SENSOR_ID + "='"+sensor_id + "'", null);

    }

    public long deleteSensor(String sensor_id) {
        long lsuppr = mDb.delete(SensorDB.TABLE_SENSORS, SensorDB.COLUMN_SENSOR_ID + " = ?", new String[]{sensor_id});
        //TODO : add a toast
        return lsuppr;
    }

    public long deleteAll(){
        long lsuppr = mDb.delete(SensorDB.TABLE_SENSORS, null, null);
        return lsuppr;
    }

    public Sensor getSensor(String sensor_id){
        mDb = this.getDb();
        String Query = "Select * from " + SensorDB.TABLE_SENSORS + " where " + SensorDB.COLUMN_SENSOR_ID
                + " = '" + sensor_id + "'";
        Cursor cursor = mDb.rawQuery(Query, null);

        cursor.moveToFirst();
        Sensor sensor = cursorToComment(cursor);
        cursor.close();

        return sensor;
    }

    public List<Sensor> getAllSensors() {
        List<Sensor> sensors = new ArrayList<Sensor>();

        Cursor cursor = mDb.query(SensorDB.TABLE_SENSORS,
                SensorDB.allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sensor sensor = cursorToComment(cursor);
            sensors.add(sensor);
            Log.d("Column ID", sensor.getName());
            cursor.moveToNext();
        }

        cursor.close();
        return sensors;
    }

    /************** Returns sensor at cursor position *****************/
    private Sensor cursorToComment(Cursor cursor) {
        Sensor sensor = new Sensor();
        sensor.setName(cursor.getString(1));
        sensor.setId(cursor.getString(2));
        sensor.setLatitude(cursor.getDouble(3));
        sensor.setLongitude(cursor.getDouble(4));
        sensor.setDepth(cursor.getDouble(5));
        sensor.setFrequency(cursor.getDouble(6));
        sensor.setDataNb(cursor.getLong(7));
        sensor.setLastCollect(cursor.getLong(8));
        return sensor;
    }

    /************** Check existence *****************/
    public boolean exists(Sensor sensor) {
        mDb = this.getDb();
        String Query = "Select * from " + SensorDB.TABLE_SENSORS + " where " + SensorDB.COLUMN_SENSOR_ID
                + " = '" + sensor.getId() + "'";
        Cursor cursor = mDb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
