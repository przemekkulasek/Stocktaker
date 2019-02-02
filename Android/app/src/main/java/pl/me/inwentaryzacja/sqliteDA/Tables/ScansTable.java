package pl.me.inwentaryzacja.sqliteDA.Tables;

import android.database.sqlite.SQLiteDatabase;

public class ScansTable {

    public static final String TABLE_SCANS = "scans";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_EMPLOYEE_CODE = "employeecode";

    private static final String CREATE_TABLE_SCANS = "create table "
            + TABLE_SCANS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " text, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_EMPLOYEE_CODE + " text);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCANS);
    }

    public static void onUpgrade(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCANS);
    }


}
