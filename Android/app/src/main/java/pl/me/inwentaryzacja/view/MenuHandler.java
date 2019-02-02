package pl.me.inwentaryzacja.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.activities.MainActivity;
import pl.me.inwentaryzacja.activities.ScanActivity;
import pl.me.inwentaryzacja.activities.SettingsActivity;
import pl.me.inwentaryzacja.activities.UpdateActivity;

public class MenuHandler {

    public static boolean handleMenu(AppCompatActivity context, MenuItem item)
    {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_manage_scanned_data: {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                return true;
            }
            case R.id.action_new_single_scan: {
                Intent intent = new Intent(context, ScanActivity.class);
                context.startActivity(intent);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
                return true;
            }
            case R.id.action_update: {
                Intent intent = new Intent(context, UpdateActivity.class);
                context.startActivity(intent);
                return true;
            }
            case R.id.action_webapi: {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String strWebService = sharedPreferences.getString("webServiceAddress", "");
                Uri uriUrl = Uri.parse(strWebService);

                if(strWebService.length() > 0 && uriUrl != null && uriUrl.getHost().length() > 0)
                {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    context.startActivity(launchBrowser);
                }
                else
                {
                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.action_webapi_message), Toast.LENGTH_LONG);
                    toast.show();
                }
                return true;
            }
        }

        return false;
    }
}
