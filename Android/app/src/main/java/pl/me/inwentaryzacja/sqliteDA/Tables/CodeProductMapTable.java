package pl.me.inwentaryzacja.sqliteDA.Tables;

import android.database.sqlite.SQLiteDatabase;

public class CodeProductMapTable {

    public static final String TABLE_CODEPRODUCTMAP = "codeproductmap";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_TABLE_CODEPRODUCTMAP = "create table "
            + TABLE_CODEPRODUCTMAP
            + "(" + COLUMN_CODE + " text not null, "
            + COLUMN_DESCRIPTION + " text not null);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CODEPRODUCTMAP);
    }

    public static void onUpgrade(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CODEPRODUCTMAP);
    }
}
