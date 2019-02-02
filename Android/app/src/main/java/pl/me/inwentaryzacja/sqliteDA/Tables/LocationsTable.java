package pl.me.inwentaryzacja.sqliteDA.Tables;

import android.database.sqlite.SQLiteDatabase;

public class LocationsTable {

    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_TABLE_LOCATIONS = "create table "
            + TABLE_LOCATIONS
            + "(" + COLUMN_CODE + " text not null, "
            + COLUMN_DESCRIPTION + " text not null);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    public static void onUpgrade(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
    }
}
