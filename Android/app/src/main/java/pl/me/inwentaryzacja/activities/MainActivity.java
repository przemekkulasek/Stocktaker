package pl.me.inwentaryzacja.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.me.inwentaryzacja.models.CodeProductMapModel;
import pl.me.inwentaryzacja.view.DisplayMessages;
import pl.me.inwentaryzacja.view.MenuHandler;
import pl.me.inwentaryzacja.models.ProductModel;
import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.adapters.ScannedDataAdapter;
import pl.me.inwentaryzacja.sqliteDA.DataSource;
import pl.me.inwentaryzacja.models.ScanGroupModel;
import pl.me.inwentaryzacja.webapiDA.consumers.Finishing;
import pl.me.inwentaryzacja.webapiDA.consumers.RecognizeProducts;
import pl.me.inwentaryzacja.webapiDA.models.Product;

public class MainActivity extends AppCompatActivity {

    private DataSource datasource;
    SparseArray<ScanGroupModel> groups = new SparseArray<ScanGroupModel>();
    ExpandableListView scannedDataListView;
    ScannedDataAdapter scannedDataAdapter;
    private SharedPreferences sharedPreferences;
    private boolean isOfflineModeOn;
    private boolean isSignOffModeOn;
    private String employeeCodeFromPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannedDataListView = (ExpandableListView) findViewById(R.id.scannedDataListView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isOfflineModeOn = sharedPreferences.getBoolean("offlineMode", false);
        isSignOffModeOn = sharedPreferences.getBoolean("useSignOff", false);
        employeeCodeFromPreferences = sharedPreferences.getString("employeeCode", null);

        datasource = new DataSource(this);
        datasource.open();

        refreshDataAndUpdateView();

        registerForContextMenu(scannedDataListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.scannedDataListView) {
            ListView lv = (ListView) v;
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)menuInfo;

            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            //int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

            if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                ScanGroupModel scanGroup = (ScanGroupModel) lv.getItemAtPosition(groupPosition);

                if(!scanGroup.isRecognized())
                {
                    menu.add(R.string.main_list_view_context_menu_recognize_products);
                }
                else
                {
                    menu.add(R.string.main_list_view_context_menu_stocktaking);
                }
                menu.add(R.string.main_list_view_context_menu_add_element_to_scan);
                menu.add(R.string.main_list_view_context_menu_delete_scan);
            }
            else if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
            {
                menu.add(R.string.main_list_view_context_menu_delete_element);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String title = item.getTitle().toString();
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                .getMenuInfo();
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            ScanGroupModel scanGroup = (ScanGroupModel) scannedDataListView.getItemAtPosition(groupPosition);

            if(title == getString(R.string.main_list_view_context_menu_recognize_products)) {
                if(isOfflineModeOn)
                {
                    ArrayList<CodeProductMapModel> codeProductMapModels = datasource.getCodeProductMapsByProductCodes(scanGroup.getNotRecognizedProductCodes());

                    Product[] products = new Product[codeProductMapModels.size()];
                    int counter = 0;
                    for(CodeProductMapModel codeProductMapModel : codeProductMapModels)
                    {
                        Product p = new Product();
                        p.setCode(codeProductMapModel.getCode());
                        p.setDescription(codeProductMapModel.getDescription());
                        products[counter] = p;
                        counter++;
                    }

                    boolean successfulUpdate = datasource.recognizeProducts(scanGroup.savedScanModel.getId(), scanGroup.getNotRecognizedProductCodes(), products);
                    if(successfulUpdate)
                    {
                        refreshDataAndUpdateView();
                    }
                    else
                    {
                        DisplayMessages.DisplayDatabaseErrorMessageProductsNotRecognized(this);
                    }
                }
                else
                {
                    AsyncTask recognizeProducts = new RecognizeProducts(this, datasource, scanGroup.savedScanModel.getId(), scanGroup.getNotRecognizedProductCodes()).execute();
                }


            }
            else if(title == getString(R.string.main_list_view_context_menu_stocktaking)) {
                if(isSignOffModeOn)
                {
                    Intent intent = new Intent(this, SignatureActivity.class);
                    intent.putParcelableArrayListExtra("products", scanGroup.children);
                    intent.putExtra("employeeCode", getEmployeeCode(scanGroup));
                    this.startActivity(intent);
                }
                else
                {
                    AsyncTask finishing = new Finishing(this, scanGroup.children, getEmployeeCode(scanGroup), null).execute();
                }

            }
            else if(title == getString(R.string.main_list_view_context_menu_delete_scan)) {
                boolean successfulDelete = datasource.deleteScan(scanGroup.savedScanModel.getId());

                if(successfulDelete)
                {
                    refreshDataAndUpdateView();
                }
                else
                {
                    DisplayMessages.DisplayDatabaseErrorMessage(this);
                }
            }
            else if(title == getString(R.string.main_list_view_context_menu_add_element_to_scan)){
                CreateAddProductDialog(scanGroup.savedScanModel.getId());
            }
        }
        else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
        {
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
            ProductModel product = (ProductModel)scannedDataListView.getExpandableListAdapter().getChild(groupPosition, childPosition);

            if(title == getString(R.string.main_list_view_context_menu_delete_element)) {
                boolean successfulDelete = datasource.deleteProduct(product.getId());
                if(successfulDelete)
                {
                    refreshDataAndUpdateView();
                }
            }
        }

        return super.onContextItemSelected(item);
    }

    private String getEmployeeCode(ScanGroupModel scanGroup) {
        String recognizedEmployeeCode = scanGroup.savedScanModel.getEmployeeCode();
        return (recognizedEmployeeCode != null ? recognizedEmployeeCode : employeeCodeFromPreferences);
    }

    private void CreateAddProductDialog(final int scanId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_code_title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton(R.string.dialog_add_code_btn_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString().trim();
                if (text.length() >= 5) {
                    boolean saveSuccesful = datasource.addProduct(text, scanId);
                    if(saveSuccesful)
                    {
                        refreshDataAndUpdateView();
                    }
                    else
                    {
                        DisplayMessages.DisplayDatabaseErrorMessage(MainActivity.this);
                    }
                } else {
                    String errorMsg = "";
                    if (text.length() < 5) {
                        errorMsg += getString(R.string.main_activity_message_save_validation_code) + "\n";
                    }

                    errorMsg += getString(R.string.main_activity_message_save_validation_try_to_add_again);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_add_code_brn_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void refreshDataAndUpdateView() {
        groups = datasource.getAllScans();
        scannedDataAdapter = new ScannedDataAdapter(this,
                groups);

        scannedDataAdapter.notifyDataSetChanged();
        scannedDataListView.setAdapter(scannedDataAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(MenuHandler.handleMenu(this, item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
