package pl.me.inwentaryzacja.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import pl.me.inwentaryzacja.models.ProductModel;
import pl.me.inwentaryzacja.view.CaptureSignatureView;
import pl.me.inwentaryzacja.view.MenuHandler;
import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.webapiDA.consumers.Finishing;

public class SignatureActivity extends AppCompatActivity {

    private Button mSaveSignatureButton;
    ArrayList<ProductModel> products;
    String employeeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        Intent intnt = getIntent();
        if(intnt != null && intnt.hasExtra("products"))
        {
            products = (ArrayList<ProductModel>) intnt
                    .getSerializableExtra("products");
        }

        if(intnt != null && intnt.hasExtra("employeeCode"))
        {
            employeeCode = intnt.getStringExtra("employeeCode");
        }


        LinearLayout mContent = (LinearLayout) findViewById(R.id.signatureLayout);
        final CaptureSignatureView mSig = new CaptureSignatureView(this, null);
        mContent.addView(mSig, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        mSaveSignatureButton = (Button) findViewById(R.id.save_signature_button);

        mSaveSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask finishing = new Finishing(SignatureActivity.this, products, employeeCode, mSig.getBytes()).execute();
                mSaveSignatureButton.setEnabled(false);
            }
        });
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
