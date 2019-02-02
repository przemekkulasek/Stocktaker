package pl.me.inwentaryzacja.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.models.ScannedCodeModel;

public class ScannedCodeAdapter extends ArrayAdapter<ScannedCodeModel> {

    private final ArrayList<ScannedCodeModel> scannedCodeModels;
    public LayoutInflater inflater;
    public Activity activity;

    public ScannedCodeAdapter(Activity act, ArrayList<ScannedCodeModel> scannedCodeModels) {
        super(act, 0, scannedCodeModels);

        activity = act;
        this.scannedCodeModels = scannedCodeModels;
        inflater = act.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ScannedCodeModel codeWithType = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        TextView text = (TextView) convertView.findViewById(R.id.textView1);
        String scannedCodesString = codeWithType.getCode();

        if(codeWithType.getDescription() != null && codeWithType.getDescription().length() > 0) {
            scannedCodesString += ", " + codeWithType.getDescription();
        }

        if(codeWithType.getQuantity() > 0) {
            scannedCodesString += " (" + codeWithType.getQuantity() + "szt.)";
        }

        if(codeWithType.getType() != null && codeWithType.getType().length() > 0) {
            scannedCodesString += " [" + codeWithType.getType() + "]";
        }
        text.setText(scannedCodesString);
        convertView.setLongClickable(true);

        return convertView;
    }
}
