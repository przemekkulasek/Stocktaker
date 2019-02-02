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

public class RecognizeEmployee extends AsyncTask<String, Void, JSONObject> {
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String code;
    private String employeeDescription;
    private boolean errorOccured;

    public RecognizeEmployee(Activity activity, String code) {
        super();
        mActivity = activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        this.code = code;
        errorOccured = false;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String strWebService = sharedPreferences.getString("webServiceAddress", "");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("code", code));

        JSONHttpClient jsonHttpClient = new JSONHttpClient();
        try
        {
            employeeDescription = jsonHttpClient.Get(strWebService + ApiControllerUrl.EMPLOYEE, nameValuePairs, String.class);
        } catch (Exception ex) {
            errorOccured = true;
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(mActivity.getString(R.string.webapi_recognizeemployee_recognizinginprogress));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {


        if((this.mActivity instanceof ScanActivity))
        {
            ScanActivity scanActivity = (ScanActivity) this.mActivity;
            scanActivity.RecognizeEmployeeAndDisplayToast(code, employeeDescription, employeeDescription == null ? true : false);
            if(errorOccured)
            {
                DisplayMessages.DisplayWebApiErrorMessage(this.mActivity);
            }
        }

        super.onPostExecute(jsonObject);
        progressDialog.dismiss();

    }
}
