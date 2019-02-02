package pl.me.inwentaryzacja.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.webapiDA.models.OperationLog;

public class DisplayMessages {
    public static void DisplayWebApiErrorMessage(Activity activity)
    {
        DisplayErrorMessage(activity, activity.getString(R.string.error_message_webapi));
    }

    public static void DisplayDatabaseErrorMessage(Activity activity)
    {
        DisplayErrorMessage(activity, activity.getString(R.string.error_message_database));
    }

    public static void DisplayDatabaseErrorMessageProductsNotRecognized(Activity activity)
    {
        DisplayErrorMessage(activity, activity.getString(R.string.error_message_database_products_not_recognized));
    }

    public static void DisplayOperationLogs(Activity activity, OperationLog[] operationLogs)
    {
        StringBuilder sb = new StringBuilder();
        for(OperationLog operationLog : operationLogs)
        {
            sb.append(operationLog.toString());
        }
        DisplayErrorMessage(activity, sb.toString());
    }

    private static void DisplayErrorMessage(Activity activity, String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setMessage(errorMessage);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
