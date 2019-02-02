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
import pl.me.inwentaryzacja.activities.ScanActivity;
import pl.me.inwentaryzacja.view.DisplayMessages;
import pl.me.inwentaryzacja.webapiDA.JSONHttpClient;
import pl.me.inwentaryzacja.webapiDA.ApiControllerUrl;
import pl.me.inwentaryzacja.webapiDA.models.Product;

public class RecognizeProduct extends AsyncTask<String, Void, JSONObject> {
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private Product product;
    private SharedPreferences sharedPreferences;
    private String productCode;
    private String type;
    private boolean errorOccured;
    private int quantity;

    public RecognizeProduct(Activity activity, String productCode, String type, int quantity) {
        super();
        mActivity = activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        this.productCode = productCode;
        this.type = type;
        errorOccured = false;
        this.quantity = quantity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String strWebService = sharedPreferences.getString("webServiceAddress", "");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("code", productCode));

        JSONHttpClient jsonHttpClient = new JSONHttpClient();
        try {
            product = jsonHttpClient.Get(strWebService + ApiControllerUrl.PRODUCTS, nameValuePairs, Product.class);
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


        if((this.mActivity instanceof ScanActivity))
        {
            ScanActivity scanActivity = (ScanActivity) this.mActivity;
            scanActivity.AddRecognizedScannedModelAndDisplayToast(productCode, type, product != null ? product.getDescription() : null, quantity);

            if(errorOccured)
            {
                DisplayMessages.DisplayWebApiErrorMessage(this.mActivity);
            }
        }

        super.onPostExecute(jsonObject);
        progressDialog.dismiss();

    }
}
