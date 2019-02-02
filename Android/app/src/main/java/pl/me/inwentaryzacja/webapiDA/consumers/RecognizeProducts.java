package pl.me.inwentaryzacja.webapiDA.consumers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.activities.MainActivity;
import pl.me.inwentaryzacja.sqliteDA.DataSource;
import pl.me.inwentaryzacja.view.DisplayMessages;
import pl.me.inwentaryzacja.webapiDA.JSONHttpClient;
import pl.me.inwentaryzacja.webapiDA.ApiControllerUrl;
import pl.me.inwentaryzacja.webapiDA.models.Product;

public class RecognizeProducts extends AsyncTask<String, Void, JSONObject> {
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private Product[] products;
    private String[] productIds;
    private DataSource datasource;
    private int scanId;
    private SharedPreferences sharedPreferences;
    private boolean errorOccured;

    public RecognizeProducts(Activity activity, DataSource datasource, int scanId, String[] productIds) {
        super();
        mActivity = activity;
        this.productIds = productIds;
        this.datasource = datasource;
        this.scanId = scanId;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        errorOccured = false;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String strWebService = sharedPreferences.getString("webServiceAddress", "");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for(String productId : productIds)
        {
            nameValuePairs.add(new BasicNameValuePair("codes", productId));
        }

        JSONHttpClient jsonHttpClient = new JSONHttpClient();

        try {
            products = jsonHttpClient.Get(strWebService + ApiControllerUrl.PRODUCTS, nameValuePairs, Product[].class);
        } catch (Exception ex) {
            errorOccured = true;
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(mActivity.getString(R.string.webapi_recognizeproducts_recognizinginprogress));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        boolean successfulUpdate = datasource.recognizeProducts(scanId, productIds, products);
        if(this.mActivity instanceof MainActivity)
        {
            if(!errorOccured)
            {
                MainActivity mainActivity = (MainActivity) this.mActivity;
                mainActivity.refreshDataAndUpdateView();
            }
            else if(errorOccured)
            {
                DisplayMessages.DisplayWebApiErrorMessage(this.mActivity);
            }
            else if (!successfulUpdate)
            {
                DisplayMessages.DisplayDatabaseErrorMessage(this.mActivity);
            }
        }

        super.onPostExecute(jsonObject);
        progressDialog.dismiss();

    }
}
