package pl.me.inwentaryzacja.sqliteDA;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import pl.me.inwentaryzacja.sqliteDA.Tables.CodeProductMapTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.LocationsTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.ProductsTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.RolesTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.ScansTable;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "iip.db";
    private static final int DATABASE_VERSION = 12;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ScansTable.onCreate(db);
        ProductsTable.onCreate(db);
        LocationsTable.onCreate(db);
        RolesTable.onCreate(db);
        CodeProductMapTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DataHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        ProductsTable.onUpgrade(db);
        ScansTable.onUpgrade(db);
        LocationsTable.onUpgrade(db);
        RolesTable.onUpgrade(db);
        CodeProductMapTable.onUpgrade(db);
        onCreate(db);
    }
}
