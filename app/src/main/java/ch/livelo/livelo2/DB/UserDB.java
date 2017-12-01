package ch.livelo.livelo2.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Remi on 28/11/2017.
 */

public class UserDB extends SQLiteOpenHelper {

    //Attributes
    // TODO ajouter les permissions?
    public static int NB_OF_FIELDS = 3;
    public static final String TABLE_USER = "user";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TOKEN = "token";

    public static String[] allColumns = { COLUMN_ID, COLUMN_EMAIL, COLUMN_EMAIL, COLUMN_TOKEN};

    //Other info
    protected static final String DATABASE_NAME = "user.db";
    protected static final int DATABASE_VERSION = 3;

    //Create table
    protected static final String DATABASE_CREATE = "create table " + TABLE_USER + "("
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_PASSWORD + " TEXT, "
            + COLUMN_ID + " TEXT, "
            + COLUMN_TOKEN + " TEXT);";

    public UserDB(Context context){
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }



}