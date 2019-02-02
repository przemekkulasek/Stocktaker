package pl.me.inwentaryzacja.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.models.ProductModel;
import pl.me.inwentaryzacja.models.ScanGroupModel;

public class ScannedDataAdapter extends BaseExpandableListAdapter {
    private final SparseArray<ScanGroupModel> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public ScannedDataAdapter(Activity act, SparseArray<ScanGroupModel> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ProductModel children = (ProductModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }

        String diplayTextString = children.getDescription() != null ? children.getDescription() : children.getCode();

        if(children.getQuantity() > 0) {
            diplayTextString += " (" + children.getQuantity() + "szt.)";
        }

        TextView text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(diplayTextString);

        text.setLongClickable(true);
        /*convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, String.valueOf(children.getScanId()),
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        convertView.setLongClickable(true);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        ScanGroupModel scanGroup = (ScanGroupModel) getGroup(groupPosition);

        ((CheckedTextView) convertView).setText(scanGroup.savedScanModel.getName());
        ((CheckedTextView) convertView).setChecked(isExpanded);

        int icon = android.R.drawable.checkbox_on_background;
        if(!scanGroup.isRecognized())
        {
            icon = android.R.drawable.checkbox_off_background;
        }

        Drawable drawableIcon = ContextCompat.getDrawable(activity, icon);
        ((CheckedTextView) convertView).setCompoundDrawablesWithIntrinsicBounds(null, null, drawableIcon, null);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
