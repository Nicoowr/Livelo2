package ch.livelo.livelo2.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Nico on 27/10/2017.
 */

public class DataDAO {



    protected SQLiteDatabase mDb = null;
    protected SQLiteStatement statement = null;
    protected DataDB mDataDB = null;



    static public String createInsert(final String tableName, final String[] columnNames) {
        if (tableName == null || columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException();
        }
        final StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ").append(tableName);//.append(" (");
        /*for (String column : columnNames) {
            s.append(column).append(" ,");
        }*/
        int length = s.length();
        s.delete(length - 2, length);
        s.append("VALUES( ");
        for (int i = 0; i < columnNames.length; i++) {
            s.append(" ? ,");
        }
        length = s.length();
        s.delete(length - 2, length);
        s.append(")");
        return s.toString();
    }


    public DataDAO(Context context) {
        this.mDataDB = new DataDB(context);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        mDb = mDataDB.getWritableDatabase();

        //Prepare the insert helper
        String INSERT_QUERY = "INSERT INTO "+ mDataDB.TABLE_SENSORS_DATA + " (" + mDataDB.COLUMN_SENSOR_ID +", "
                + mDataDB.COLUMN_TIME_STAMP + ", " + mDataDB.COLUMN_PRESSURE + ")" +" VALUES (?,?,?);";//createInsert(mDataDB.getDatabaseName(), new String[]{mDataDB.COLUMN_SENSOR_ID, mDataDB.COLUMN_TIME_STAMP, mDataDB.COLUMN_PRESSURE});
        statement = mDb.compileStatement(INSERT_QUERY);


        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }
    ///////////////////////////Operations////////////////////////

    public void transactionBegins(){
        mDb.beginTransaction();
    };

    public void transactionEnds(){
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    public void addData(String sensor_id, long time_stamp, int pressure) {

        try {
                statement.clearBindings();
                statement.bindString(1, sensor_id);
                statement.bindLong(2, time_stamp);
                statement.bindLong(3, (long) pressure);// I can't bind integer ?!
                statement.execute();
            }catch(Exception e) {
            e.printStackTrace();
        }




        /*ContentValues values = new ContentValues();
        values.put(DataDB.COLUMN_SENSOR_ID, sensor_id);
        values.put(DataDB.COLUMN_TIME_STAMP, time_stamp);
        values.put(DataDB.COLUMN_PRESSURE, pressure);
        long insertId = mDb.insert(DataDB.TABLE_SENSORS_DATA, null,
                values);*/
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
