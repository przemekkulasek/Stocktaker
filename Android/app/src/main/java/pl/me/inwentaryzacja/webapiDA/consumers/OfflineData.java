package pl.me.inwentaryzacja.webapiDA.consumers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import java.util.ArrayList;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.activities.MainActivity;
import pl.me.inwentaryzacja.activities.UpdateActivity;
import pl.me.inwentaryzacja.models.CodeProductMapModel;
import pl.me.inwentaryzacja.models.LocationModel;
import pl.me.inwentaryzacja.sqliteDA.DataSource;
import pl.me.inwentaryzacja.view.DisplayMessages;
import pl.me.inwentaryzacja.webapiDA.JSONHttpClient;
import pl.me.inwentaryzacja.webapiDA.ApiControllerUrl;
import pl.me.inwentaryzacja.webapiDA.models.Location;
import pl.me.inwentaryzacja.webapiDA.models.Product;

public class OfflineData extends AsyncTask<String, Void, JSONObject> {
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private Product[] products;
    private Location[] locations;
    private String[] roles;
    private DataSource datasource;
    private boolean syncLocations;
    private boolean syncRoles;
    private boolean syncCodeProductMap;
    private SharedPreferences sharedPreferences;
    private boolean errorOccured;

    public OfflineData(Activity activity, DataSource datasource, boolean syncLocations, boolean syncRoles, boolean syncCodeProductMap) {
        super();
        mActivity = activity;
        this.datasource = datasource;
        this.syncLocations = syncLocations;
        this.syncRoles = syncRoles;
        this.syncCodeProductMap = syncCodeProductMap;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        errorOccured = false;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String strWebService = sharedPreferences.getString("webServiceAddress", "");

        JSONHttpClient jsonHttpClient = new JSONHttpClient();
        try
        {
            if(syncLocations)
            {
                locations = jsonHttpClient.Get(strWebService + ApiControllerUrl.OFFLINE_DATA_LOCATIONS, null, Location[].class);
            }

            if(syncRoles)
            {
                roles = jsonHttpClient.Get( strWebService + ApiControllerUrl.OFFLINE_DATA_ROLES, null, String[].class);
            }

            if(syncCodeProductMap)
            {
                products = jsonHttpClient.Get(strWebService + ApiControllerUrl.OFFLINE_DATA, null, Product[].class);
            }
        } catch (Exception ex) {
            errorOccured = true;
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(mActivity.getString(R.string.webapi_getting_offline_data));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if(!errorOccured)
        {
            ArrayList<LocationModel> locationModels  = new ArrayList<>();

            if(syncLocations)
            {
                for(Location location : locations)
                {
                    locationModels.add(new LocationModel(location.getCode(), location.getDescription()));
                }
            }

            ArrayList<String> roleStrings  = new ArrayList<>();
            if(syncRoles)
            {
                for(String role : roles)
                {
                    roleStrings.add(role);
                }
            }

            ArrayList<CodeProductMapModel> codeProductMapModels  = new ArrayList<>();
            if(syncCodeProductMap)
            {
                for(Product product : products)
                {
                    codeProductMapModels.add(new CodeProductMapModel(product.getCode(), product.getDescription()));
                }
            }

            boolean saveSuccessful = datasource.saveOfflineData(codeProductMapModels, roleStrings, locationModels);

            if(!saveSuccessful)
            {
                DisplayMessages.DisplayDatabaseErrorMessage(this.mActivity);
            }
            else
            {
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
            }
        }
        else
        {
            if((this.mActivity instanceof UpdateActivity))
            {
                DisplayMessages.DisplayWebApiErrorMessage(this.mActivity);
            }
        }

        super.onPostExecute(jsonObject);
        progressDialog.dismiss();

    }
}
