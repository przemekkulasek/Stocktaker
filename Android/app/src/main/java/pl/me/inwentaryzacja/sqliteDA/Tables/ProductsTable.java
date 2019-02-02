package pl.me.inwentaryzacja.sqliteDA.Tables;

import android.database.sqlite.SQLiteDatabase;

public class ProductsTable {
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE_TYPE = "codetype";
    public static final String COLUMN_CODE_QUANTITY = "quantity";
    public static final String COLUMN_SCAN_ID = "scanid";

    private static final String CREATE_TABLE_PRODUCTS = "create table "
            + TABLE_PRODUCTS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CODE + " text, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_CODE_TYPE + " text, "
            + COLUMN_CODE_QUANTITY + " integer, "
            + COLUMN_SCAN_ID + " integer);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    public static void onUpgrade(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
    }
}
