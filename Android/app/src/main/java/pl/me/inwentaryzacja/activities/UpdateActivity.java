package pl.me.inwentaryzacja.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.models.CodeProductMapModel;
import pl.me.inwentaryzacja.models.LocationModel;
import pl.me.inwentaryzacja.sqliteDA.DataSource;
import pl.me.inwentaryzacja.view.MenuHandler;
import pl.me.inwentaryzacja.webapiDA.consumers.OfflineData;

public class UpdateActivity extends AppCompatActivity {

    private Button mUpdateButton;
    private DataSource datasource;
    private CheckBox mChkUpdateRoles;
    private CheckBox mChkUpdateLocations;
    private CheckBox mChkUpdateCodeProductMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        datasource = new DataSource(this);
        datasource.open();

        mUpdateButton = (Button) findViewById(R.id.btnUpdate);

        mChkUpdateRoles = (CheckBox) findViewById(R.id.chkUpdateRoles);
        mChkUpdateLocations = (CheckBox) findViewById(R.id.chkUpdateLocations);
        mChkUpdateCodeProductMap = (CheckBox) findViewById(R.id.chkUpdateCodeProductMap);

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetOfflineDataFromWebApi();
            }
        });
    }

    private void GetOfflineDataFromWebApi() {
        AsyncTask recognizeProducts = new OfflineData(this, datasource, mChkUpdateLocations.isChecked(), mChkUpdateRoles.isChecked(), mChkUpdateCodeProductMap.isChecked()).execute();
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
