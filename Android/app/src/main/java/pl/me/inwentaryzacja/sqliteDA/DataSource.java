package pl.me.inwentaryzacja.sqliteDA;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import pl.me.inwentaryzacja.models.CodeProductMapModel;
import pl.me.inwentaryzacja.models.LocationModel;
import pl.me.inwentaryzacja.models.ScannedCodeModel;
import pl.me.inwentaryzacja.sqliteDA.Tables.CodeProductMapTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.LocationsTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.ProductsTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.RolesTable;
import pl.me.inwentaryzacja.sqliteDA.Tables.ScansTable;
import pl.me.inwentaryzacja.models.ScanGroupModel;
import pl.me.inwentaryzacja.models.ProductModel;
import pl.me.inwentaryzacja.models.ScanModel;
import pl.me.inwentaryzacja.webapiDA.models.Product;

public class DataSource {

    private SQLiteDatabase database;
    private DataHelper dbHelper;

    private String[] allColumnsScans = { ScansTable.COLUMN_ID,
            ScansTable.COLUMN_NAME, ScansTable.COLUMN_DATE, ScansTable.COLUMN_EMPLOYEE_CODE};

    private String[] allColumnsProducts = { ProductsTable.COLUMN_ID, ProductsTable.COLUMN_CODE,
            ProductsTable.COLUMN_DESCRIPTION, ProductsTable.COLUMN_CODE_TYPE, ProductsTable.COLUMN_CODE_QUANTITY,
            ProductsTable.COLUMN_SCAN_ID };

    private String[] allColumnsLocations = {LocationsTable.COLUMN_CODE,
            LocationsTable.COLUMN_DESCRIPTION };

    private String[] allColumnsRoles = {RolesTable.COLUMN_NAME};

    private String[] allColumnsCodeProductMap = {CodeProductMapTable.COLUMN_CODE,
            CodeProductMapTable.COLUMN_DESCRIPTION };

    public DataSource(Context context) {
        dbHelper = new DataHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private ScanModel cursorToScanModel(Cursor cursor) {
        ScanModel scanModel = new ScanModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return scanModel;
    }

    private ProductModel cursorToProductModel(Cursor cursor) {
        ProductModel productModel = new ProductModel(cursor.getInt(0), cursor.getInt(5), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
        return productModel;
    }

    private LocationModel cursorToLocationModel(Cursor cursor) {
        LocationModel locationModel = new LocationModel(cursor.getString(0), cursor.getString(1));
        return locationModel;
    }

    private CodeProductMapModel cursorToCodeProductMapModel(Cursor cursor) {
        CodeProductMapModel codeProductMapModel = new CodeProductMapModel(cursor.getString(0), cursor.getString(1));
        return codeProductMapModel;
    }

    public SparseArray<ScanGroupModel> getAllScans() {
        SparseArray<ScanGroupModel> scanGroups = new SparseArray<>();

        Cursor cursorScan = database.query(ScansTable.TABLE_SCANS,
                allColumnsScans, null, null, null, null, null);

        cursorScan.moveToFirst();
        while (!cursorScan.isAfterLast()) {

            ScanModel scanModel = cursorToScanModel(cursorScan);
            ScanGroupModel scanGroup = new ScanGroupModel(scanModel);
            scanGroups.append(scanModel.getId(), scanGroup);
            cursorScan.moveToNext();
        }

        cursorScan.close();

        Cursor cursorProduct = database.query(ProductsTable.TABLE_PRODUCTS,
                allColumnsProducts, null, null, null, null, null);

        cursorProduct.moveToFirst();
        while (!cursorProduct.isAfterLast()) {
            ProductModel productModel = cursorToProductModel(cursorProduct);
            ScanGroupModel scanGroup = scanGroups.get(productModel.getScanId());
            if(scanGroup != null)
            {
                scanGroup.children.add(productModel);
            }

            cursorProduct.moveToNext();
        }

        cursorProduct.close();

        //TODO: This is not the best solution. But we need to have index i instead of scan id
        SparseArray<ScanGroupModel> scanGroupsWithFixedKeys = new SparseArray<>();
        for(int i = 0; i < scanGroups.size(); i++) {
            int key = scanGroups.keyAt(i);
            ScanGroupModel obj = scanGroups.get(key);
            scanGroupsWithFixedKeys.append(i, obj);
        }

        return scanGroupsWithFixedKeys;
    }

    public boolean saveScan(String scanName, ArrayList<ScannedCodeModel> scannedCodes, String employeeCode)
    {
        try
        {
            int insert = 0;
            database.beginTransaction();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdf.format(new Date());

            ContentValues cvScan = new ContentValues();
            cvScan.put(ScansTable.COLUMN_NAME, scanName);
            cvScan.put(ScansTable.COLUMN_DATE, strDate);
            cvScan.put(ScansTable.COLUMN_EMPLOYEE_CODE, employeeCode);
            long insertId = database.insert(ScansTable.TABLE_SCANS, null,
                    cvScan);

            for(ScannedCodeModel scannedCode : scannedCodes)
            {
                ContentValues cvProduct = new ContentValues();
                cvProduct.put(ProductsTable.COLUMN_CODE, scannedCode.getCode());
                cvProduct.put(ProductsTable.COLUMN_CODE_TYPE, scannedCode.getType());
                cvProduct.put(ProductsTable.COLUMN_DESCRIPTION, scannedCode.getDescription());
                cvProduct.put(ProductsTable.COLUMN_CODE_QUANTITY, scannedCode.getQuantity());
                cvProduct.put(ProductsTable.COLUMN_SCAN_ID, insertId);

                insert += database.insert(ProductsTable.TABLE_PRODUCTS, null,
                        cvProduct);
            }

            database.setTransactionSuccessful();

            return (insert > 0);
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            database.endTransaction();
        }
    }

    public boolean deleteScan(int id)
    {
        try
        {
            database.beginTransaction();

            database.delete(ProductsTable.TABLE_PRODUCTS, ProductsTable.COLUMN_SCAN_ID + "=" + id, null);
            int deleteScan = database.delete(ScansTable.TABLE_SCANS, ScansTable.COLUMN_ID + "=" + id, null);

            database.setTransactionSuccessful();

            return (deleteScan > 0);
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            database.endTransaction();
        }
    }

    public boolean recognizeProducts(int scanId, String[] productIds, Product[] products)
    {
        try
        {
            database.beginTransaction();

            int update = 0;
            for(int i=0; i< productIds.length; i++)
            {
                for (Product product : products) {
                    if(productIds[i].equals(product.getCode()))
                    {
                        ContentValues cvProduct = new ContentValues();
                        cvProduct.put(ProductsTable.COLUMN_DESCRIPTION, product.getDescription());

                        update += database.update(ProductsTable.TABLE_PRODUCTS, cvProduct,
                                ProductsTable.COLUMN_SCAN_ID + "=" + scanId + " AND " + ProductsTable.COLUMN_CODE + "=" + productIds[i], null);
                    }
                }
            }

            database.setTransactionSuccessful();

            return (update > 0);
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            database.endTransaction();
        }
    }

    public boolean saveOfflineData(ArrayList<CodeProductMapModel> codeproductmap, ArrayList<String> roles, ArrayList<LocationModel> locations)
    {
        try
        {
            database.beginTransaction();

            int insert = 0;
            if(codeproductmap != null && codeproductmap.size() > 0)
            {
                database.delete(CodeProductMapTable.TABLE_CODEPRODUCTMAP, null, null);

                for(CodeProductMapModel codeProductMapModel : codeproductmap)
                {
                    ContentValues cvCodeProductMap = new ContentValues();
                    cvCodeProductMap.put(CodeProductMapTable.COLUMN_CODE, codeProductMapModel.getCode());
                    cvCodeProductMap.put(CodeProductMapTable.COLUMN_DESCRIPTION, codeProductMapModel.getDescription());

                    insert += database.insert(CodeProductMapTable.TABLE_CODEPRODUCTMAP, null,
                            cvCodeProductMap);
                }
            }

            if(locations != null && locations.size() > 0)
            {
                database.delete(LocationsTable.TABLE_LOCATIONS, null, null);

                for(LocationModel locationModel : locations)
                {
                    ContentValues cvLocation = new ContentValues();
                    cvLocation.put(LocationsTable.COLUMN_CODE, locationModel.getCode());
                    cvLocation.put(LocationsTable.COLUMN_DESCRIPTION, locationModel.getDescription());

                    insert += database.insert(LocationsTable.TABLE_LOCATIONS, null,
                            cvLocation);
                }
            }

            if(roles != null && roles.size() > 0)
            {
                database.delete(RolesTable.TABLE_ROLES, null, null);

                for(String role : roles)
                {
                    ContentValues cvRole = new ContentValues();
                    cvRole.put(RolesTable.COLUMN_NAME, role);

                    insert += database.insert(RolesTable.TABLE_ROLES, null,
                            cvRole);
                }
            }

            database.setTransactionSuccessful();
            return (insert > 0);
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            database.endTransaction();
        }
    }

    public ArrayList<String> getAllUserRoles()
    {
        ArrayList<String> userRoles = new ArrayList<>();

        Cursor cursorRole = database.query(RolesTable.TABLE_ROLES,
                allColumnsRoles, null, null, null, null, null);

        cursorRole.moveToFirst();
        while (!cursorRole.isAfterLast()) {

            String role = cursorRole.getString(0);
            userRoles.add(role);
            cursorRole.moveToNext();
        }

        cursorRole.close();

        return userRoles;
    }

    public ArrayList<LocationModel> getAllLocations()
    {
        ArrayList<LocationModel> locations = new ArrayList<>();

        Cursor cursorLocation = database.query(LocationsTable.TABLE_LOCATIONS,
                allColumnsLocations, null, null, null, null, null);

        cursorLocation.moveToFirst();
        while (!cursorLocation.isAfterLast()) {

            LocationModel locationModel = cursorToLocationModel(cursorLocation);
            locations.add(locationModel);
            cursorLocation.moveToNext();
        }

        cursorLocation.close();

        return locations;
    }

    public ArrayList<CodeProductMapModel> getCodeProductMapsByProductCodes(String[] codes)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        String prefix = "";
        for(String code : codes)
        {
            sb.append(prefix);
            prefix = ",";
            sb.append("'");
            sb.append(code);
            sb.append("'");
        }
        sb.append(")");

        String query = "SELECT code, description FROM " + CodeProductMapTable.TABLE_CODEPRODUCTMAP + " WHERE " + CodeProductMapTable.COLUMN_CODE + " IN " + sb.toString();
        ArrayList<CodeProductMapModel> codeProductMapModels = new ArrayList<>();

        Cursor cursorCodeProductMap = database.rawQuery(query, null);

        cursorCodeProductMap.moveToFirst();
        while (!cursorCodeProductMap.isAfterLast()) {

            CodeProductMapModel codeProductMapModel = cursorToCodeProductMapModel(cursorCodeProductMap);
            codeProductMapModels.add(codeProductMapModel);
            cursorCodeProductMap.moveToNext();
        }

        cursorCodeProductMap.close();

        return codeProductMapModels;
    }

    public String getProductDescriptionByCode(String code)
    {
        Cursor cursorDescription = database.query(CodeProductMapTable.TABLE_CODEPRODUCTMAP,
                new String[]{CodeProductMapTable.COLUMN_DESCRIPTION}, CodeProductMapTable.COLUMN_CODE + " = '" + code + "'", null, null, null, null);

        cursorDescription.moveToFirst();
        if(cursorDescription.getCount() <= 0)
        {
            return null;
        }
        String desc =  cursorDescription.getString(0);
        cursorDescription.close();
        return desc;
    }

    public boolean deleteProduct(int id)
    {
        try
        {
            database.beginTransaction();

            int deleteProduct = database.delete(ProductsTable.TABLE_PRODUCTS, ProductsTable.COLUMN_ID + "=" + id, null);

            database.setTransactionSuccessful();

            return (deleteProduct > 0);
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            database.endTransaction();
        }
    }

    public boolean addProduct(String code, int scanId)
    {
        try
        {
            database.beginTransaction();

            ContentValues cvProduct = new ContentValues();
            cvProduct.put(ProductsTable.COLUMN_CODE, code);
            cvProduct.put(ProductsTable.COLUMN_CODE_TYPE, "MANUAL");
            cvProduct.put(ProductsTable.COLUMN_SCAN_ID, scanId);

            long insertProduct = database.insert(ProductsTable.TABLE_PRODUCTS, null,
                    cvProduct);

            database.setTransactionSuccessful();

            return (insertProduct > 0);
        }
        catch(Exception ex)
        {
            return false;
        }
        finally
        {
            database.endTransaction();
        }
    }
}
