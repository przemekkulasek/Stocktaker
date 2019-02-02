package pl.me.inwentaryzacja.webapiDA.consumers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.models.ProductModel;
import pl.me.inwentaryzacja.sqliteDA.DataSource;
import pl.me.inwentaryzacja.view.DisplayMessages;
import pl.me.inwentaryzacja.webapiDA.JSONHttpClient;
import pl.me.inwentaryzacja.webapiDA.ApiControllerUrl;
import pl.me.inwentaryzacja.webapiDA.models.OperationLog;
import pl.me.inwentaryzacja.webapiDA.models.Product;
import pl.me.inwentaryzacja.webapiDA.models.ProductWithQuantity;

public class Finishing extends AsyncTask<String, Void, JSONObject> {
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private  List<ProductModel> productModels;
    private SharedPreferences sharedPreferences;
    String employeeCode;
    byte[] sign;
    OperationLog[] operationLogs;
    private boolean errorOccured;

    public Finishing(Activity activity, ArrayList<ProductModel> productModels, String employeeCode, byte[] sign) {
        super();
        mActivity = activity;
        this.productModels = productModels;
        this.sign = sign;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        this.employeeCode = employeeCode;
        errorOccured = false;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String strWebService = sharedPreferences.getString("webServiceAddress", "");
        String strUserRole = sharedPreferences.getString("userRole", "Default");
        String strLocation = sharedPreferences.getString("location", "Default");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Location", strLocation));
        nameValuePairs.add(new BasicNameValuePair("Role", strUserRole));
        nameValuePairs.add(new BasicNameValuePair("Date", new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        nameValuePairs.add(new BasicNameValuePair("EmployeeCode", employeeCode));
        nameValuePairs.add(new BasicNameValuePair("Sign", sign != null ? sign.toString() : ""));

        List<ProductWithQuantity> productWithQuantities = new ArrayList<>();

        for(ProductModel pm : productModels)
        {
            ProductWithQuantity product = new ProductWithQuantity();
            product.setCode(pm.getCode());
            product.setDescription(pm.getDescription());
            product.setQuantity(pm.getQuantity());
            productWithQuantities.add(product);
        }

        JSONHttpClient jsonHttpClient = new JSONHttpClient();
        try
        {
            operationLogs = jsonHttpClient.PostObject(strWebService + ApiControllerUrl.FINISHING, nameValuePairs, productWithQuantities.toArray(new Product[productWithQuantities.size()]), Product[].class);
        }
        catch(Exception ex)
        {
            errorOccured = true;
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(mActivity.getString(R.string.webapi_finishing_finishinginprocess));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        progressDialog.dismiss();
        if(!errorOccured)
        {
            DisplayMessages.DisplayOperationLogs(this.mActivity, operationLogs);
        }
        else
        {
            DisplayMessages.DisplayWebApiErrorMessage(this.mActivity);
        }
    }
}
