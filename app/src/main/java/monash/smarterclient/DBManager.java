package monash.smarterclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "elecusage";
    private final Context context;
    private static final String TEXT_TYPE = "TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBStructure.tableEntry.TABLE_NAME + " (" +
                    DBStructure.tableEntry._ID + " INTEGER PRIMARY KEY," +
                    DBStructure.tableEntry.COLUMN_AIRCONDUSAGE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_FRIDGEUSAGE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_RESID + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_TEMPERATURE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_USAGEDATE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_USAGEHOUR + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_USAGEID + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_WASHINGMACHUSAGE + TEXT_TYPE +
                    " );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBStructure.tableEntry.TABLE_NAME;

    private MySQLiteOpenHelper myDBHelper;
    private SQLiteDatabase db;

    public DBManager(Context ctx) {
        this.context = ctx;
        myDBHelper = new MySQLiteOpenHelper(context);
    }

    public class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    public DBManager open() throws SQLException {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDBHelper.close();
    }

    public long inserUsage(String airCond, String fridge, String resid, String temp, String date,
                           String usageHour, String usageID, String washingMach) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.tableEntry.COLUMN_AIRCONDUSAGE, airCond);
        values.put(DBStructure.tableEntry.COLUMN_FRIDGEUSAGE, fridge);
        values.put(DBStructure.tableEntry.COLUMN_RESID, resid);
        values.put(DBStructure.tableEntry.COLUMN_TEMPERATURE, temp);
        values.put(DBStructure.tableEntry.COLUMN_USAGEDATE, date);
        values.put(DBStructure.tableEntry.COLUMN_USAGEHOUR, usageHour);
        values.put(DBStructure.tableEntry.COLUMN_USAGEID, usageID);
        values.put(DBStructure.tableEntry.COLUMN_WASHINGMACHUSAGE, washingMach);
        return db.insert(DBStructure.tableEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllUsage() {
        return db.query(DBStructure.tableEntry.TABLE_NAME, columns, null, null, null, null, null);
    }

    private String[] columns = {
            DBStructure.tableEntry.COLUMN_AIRCONDUSAGE,
            DBStructure.tableEntry.COLUMN_FRIDGEUSAGE,
            DBStructure.tableEntry.COLUMN_RESID,
            DBStructure.tableEntry.COLUMN_TEMPERATURE,
            DBStructure.tableEntry.COLUMN_USAGEDATE,
            DBStructure.tableEntry.COLUMN_USAGEHOUR,
            DBStructure.tableEntry.COLUMN_USAGEID,
            DBStructure.tableEntry.COLUMN_WASHINGMACHUSAGE};

    private int deleteUsage(String rowID){
        String[] selectionArgs = {String.valueOf(rowID)};
        String selection = DBStructure.tableEntry.COLUMN_USAGEID + "LIKE?";
        return db.delete(DBStructure.tableEntry.TABLE_NAME, selection, selectionArgs);
    }
}
