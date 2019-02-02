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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import pl.me.inwentaryzacja.adapters.ScannedCodeAdapter;
import pl.me.inwentaryzacja.models.ScannedCodeModel;
import pl.me.inwentaryzacja.view.DisplayMessages;
import pl.me.inwentaryzacja.view.MenuHandler;
import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.sqliteDA.DataSource;
import pl.me.inwentaryzacja.webapiDA.consumers.RecognizeEmployee;
import pl.me.inwentaryzacja.webapiDA.consumers.RecognizeProduct;

public class ScanActivity extends AppCompatActivity {

    private Button mScanButton;
    private Button mSaveButton;
    ArrayList<ScannedCodeModel> scannedCodes = new ArrayList<>();
    ArrayAdapter<ScannedCodeModel> arrayAdapter;
    ListView capturedScansListView;
    IntentIntegrator integrator;
    private SharedPreferences sharedPreferences;
    private boolean isBulkModeEnabled;
    private boolean isOfflineModeOn;
    private boolean isRecognizeProductDirectlyModeOn;
    private boolean isRecognizeEmployeeModeOn;
    private boolean isAjustQuantityModeOn;
    private DataSource datasource;
    private String employeeCode;
    private int adjustQuantityMaxValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //TODO: remove this code
        /*scannedCodes.add(new ScannedCodeModel("9771426291303", "code1", "rozpoznany", 1));
        scannedCodes.add(new ScannedCodeModel("9771426291501", "code2", null, 1));
        scannedCodes.add(new ScannedCodeModel("9771426291402", "code3", null, 1));*/

        if (savedInstanceState != null ) {
            if(savedInstanceState.containsKey("scannedCodes"))
            {
                scannedCodes = savedInstanceState.getParcelableArrayList("scannedCodes");
            }
            if(savedInstanceState.containsKey("employeeCode"))
            {
                employeeCode = savedInstanceState.getString("employeeCode");
            }
        }

        integrator = new IntentIntegrator(ScanActivity.this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isBulkModeEnabled = sharedPreferences.getBoolean("zxing_preferences_bulk_mode", false);
        isOfflineModeOn = sharedPreferences.getBoolean("offlineMode", false);
        isRecognizeProductDirectlyModeOn = sharedPreferences.getBoolean("recognizeProductsDirectly", false);
        isRecognizeEmployeeModeOn = sharedPreferences.getBoolean("recognizeEmployee", false);
        isAjustQuantityModeOn = sharedPreferences.getBoolean("adjustQuantity", false);

        try {
            adjustQuantityMaxValue = Integer.parseInt(sharedPreferences.getString("adjustQuantityMaxValue", "10"));
        } catch(NumberFormatException nfe) {
            adjustQuantityMaxValue = 10;
        }

        // Scanner
        mScanButton = (Button) findViewById(R.id.scan_button);
        mSaveButton = (Button) findViewById(R.id.save_scan_button);
        capturedScansListView = (ListView) findViewById(R.id.capturedScansListView);

        arrayAdapter = new ScannedCodeAdapter(this, scannedCodes);
        capturedScansListView.setAdapter(arrayAdapter);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrator.initiateScan();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSaveDialog();
            }
        });

        registerForContextMenu(capturedScansListView);

        if(shouldRecognizeEmployee())
        {
            mScanButton.setText(getString(R.string.btn_srecognize_employee));
        }
    }

    private void CreateSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        builder.setTitle(R.string.dialog_save_scan_title);

        final EditText input = new EditText(ScanActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(R.string.dialog_save_scan_btn_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString().trim();
                if (text.length() >= 5 && scannedCodes.size() > 0) {
                    boolean saveSuccesful = SaveScan(text, employeeCode);
                    if (saveSuccesful) {
                        Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        DisplayMessages.DisplayDatabaseErrorMessage(ScanActivity.this);
                    }

                } else {
                    String errorMsg = "";
                    if (text.length() < 5) {
                        errorMsg += getString(R.string.scan_activity_message_save_validation_name) + "\n";
                    }

                    if (scannedCodes.size() <= 0) {
                        errorMsg += getString(R.string.scan_activity_message_save_validation_items) + "\n";
                    }

                    errorMsg += getString(R.string.scan_activity_message_save_validation_try_to_save_again);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_save_scan_brn_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void CreateAdjustQuantityDialog(final String code, final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        builder.setTitle(R.string.dialog_set_quantity_title);
        builder.setCancelable(false);

        final NumberPicker numberPicker = new NumberPicker(ScanActivity.this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(adjustQuantityMaxValue);
        numberPicker.setWrapSelectorWheel(true);

        builder.setView(numberPicker);
        builder.setPositiveButton(R.string.dialog_set_quantity_btn_set_quantity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int quantity = numberPicker.getValue();
                handleScanResult(code, type, quantity);
            }
        });

        builder.show();
    }

    private boolean shouldRecognizeEmployee()
    {
        return isRecognizeEmployeeModeOn && (employeeCode == null || employeeCode.length() <= 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String scanContent = scanResult.getContents();
            String scanFormat = scanResult.getFormatName();

            if(shouldRecognizeEmployee())
            {
                recognizeEmployee(scanContent);
            }
            else
            {
                if(isAjustQuantityModeOn)
                {
                    CreateAdjustQuantityDialog(scanContent, scanFormat);
                }
                else
                {
                    handleScanResult(scanContent, scanFormat, 1);
                }
            }
        }
        else{
            if(!isBulkModeEnabled)
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.scan_activity_message_scan_result), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void recognizeEmployee(String scanContent) {
        if(isOfflineModeOn)
        {
            RecognizeEmployeeAndDisplayToast(scanContent, null, false);
        }
        else
        {
            AsyncTask recognizeEmployee = new RecognizeEmployee(ScanActivity.this, scanContent).execute();
        }
    }

    private void handleScanResult(String scanContent, String scanFormat, int quantity) {

        if(isRecognizeProductDirectlyModeOn)
        {
            if(isOfflineModeOn)
            {
                datasource = new DataSource(this);
                datasource.open();
                String description = datasource.getProductDescriptionByCode(scanContent);
                datasource.close();

                AddRecognizedScannedModelAndDisplayToast(scanContent, scanFormat, description != null ? description : null, quantity);
            }
            else
            {
                AsyncTask recognizeProduct = new RecognizeProduct(ScanActivity.this, scanContent, scanFormat, quantity).execute();
            }
        }
        else
        {
            scannedCodes.add(new ScannedCodeModel(scanContent, scanFormat, null, quantity));
            arrayAdapter.notifyDataSetChanged();
        }

        if(isBulkModeEnabled)
        {
            integrator.initiateScan();
        }

    }

    public void AddRecognizedScannedModelAndDisplayToast(String code, String type, String description, int quantity) {
        scannedCodes.add(new ScannedCodeModel(code, type, description, quantity));

        if(isBulkModeEnabled)
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    description != null ? description : getString(R.string.scan_activity_product_not_recognized), Toast.LENGTH_SHORT);
            toast.show();
        }

        arrayAdapter.notifyDataSetChanged();
    }

    public void RecognizeEmployeeAndDisplayToast(String code, String name, boolean employeeNotRecognized) {

        String message;

        if(employeeNotRecognized)
        {
            message = getString(R.string.scan_activity_employee_not_recognized);
        }
        else
        {
            message = getString(R.string.scan_activity_employee_recognized) + ": " + (name != null ? name : code);
            mScanButton.setText(getString(R.string.btn_scan));
            employeeCode = code;
        }

        Toast toast = Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.capturedScansListView) {
            menu.add(R.string.scan_activity_list_view_context_menu_delete_code);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if(title == getString(R.string.scan_activity_list_view_context_menu_delete_code)){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            scannedCodes.remove(info.position);
            arrayAdapter.notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);
    }

    private boolean SaveScan(String scanName, String employeeCode) {
        DataSource datasource = new DataSource(ScanActivity.this);
        datasource.open();
        boolean saveSuccesfull = datasource.saveScan(scanName, scannedCodes, employeeCode);
        datasource.close();
        return saveSuccesfull;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelableArrayList("scannedCodes", scannedCodes);
        savedState.putString("employeeCode", employeeCode);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(MenuHandler.handleMenu(this, item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
