package pl.me.inwentaryzacja.sqliteDA.Tables;

import android.database.sqlite.SQLiteDatabase;

public class RolesTable {

    public static final String TABLE_ROLES = "roles";
    public static final String COLUMN_NAME = "name";

    private static final String CREATE_TABLE_ROLES = "create table "
            + TABLE_ROLES
            + "(" + COLUMN_NAME + " text not null);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROLES);
    }

    public static void onUpgrade(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
    }
}
