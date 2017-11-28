package ch.livelo.livelo2.DB;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.icu.lang.UScript;
        import android.util.Log;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by Remi on 28/11/2017.
 */

public class UserDAO {


    protected SQLiteDatabase mDb = null;
    protected UserDB mUserDB = null;




    public UserDAO(Context context) {
        this.mUserDB = new UserDB(context);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        mDb = mUserDB.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    //////////////////////Operations/////////////////////////

    public void addUser(String email, String password) {
        ContentValues values = new ContentValues();
        values.put(UserDB.COLUMN_EMAIL, email);
        values.put(UserDB.COLUMN_PASSWORD, password);
        values.put(UserDB.COLUMN_TOKEN, "");
        long insertId = mDb.insert(UserDB.TABLE_USER, null,
                values);
        /*Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();*/
        return;
    }


    public void updateToken(String token){

        ContentValues cv = new ContentValues();
        cv.put(UserDB.COLUMN_TOKEN, token);

        mDb.update(UserDB.TABLE_USER, cv, UserDB.COLUMN_ID + "='"+ "1" + "'", null);
    }

    public boolean logOut() {
        ContentValues cv = new ContentValues();
        cv.put(UserDB.COLUMN_EMAIL, "");
        cv.put(UserDB.COLUMN_PASSWORD, "");
        cv.put(UserDB.COLUMN_TOKEN, "");
        mDb.update(UserDB.TABLE_USER, cv, UserDB.COLUMN_ID + "='"+ "1" + "'", null);
        return true;
    }

    public String getEmail(){
        mDb = this.getDb();
        String Query = "Select * from " + UserDB.TABLE_USER + " where " + UserDB.COLUMN_ID
                + " = '" + "1" + "'";
        Cursor cursor = mDb.rawQuery(Query, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    public String getPassword(){
        mDb = this.getDb();
        String Query = "Select * from " + UserDB.TABLE_USER + " where " + UserDB.COLUMN_ID
                + " = '" + "1" + "'";
        Cursor cursor = mDb.rawQuery(Query, null);
        cursor.moveToFirst();
        return cursor.getString(2);
    }


    public boolean exists() {
        mDb = this.getDb();
        String Query = "Select * from " + UserDB.TABLE_USER + " where " + UserDB.COLUMN_ID
                + " = '" + "1" + "'";
        Cursor cursor = mDb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }else {
            cursor.close();
            return true;
        }
    }
}
