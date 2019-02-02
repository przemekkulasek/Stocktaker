package pl.me.inwentaryzacja.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.webkit.URLUtil;

import java.util.ArrayList;
import java.util.List;

import pl.me.inwentaryzacja.R;
import pl.me.inwentaryzacja.models.LocationModel;
import pl.me.inwentaryzacja.sqliteDA.DataSource;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        final ListPreference userRoleListPreference = (ListPreference) findPreference("userRole");
        setUserRoleListPreferenceData(userRoleListPreference);

        userRoleListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                setUserRoleListPreferenceData(userRoleListPreference);
                return false;
            }
        });

        final ListPreference locationListPreference = (ListPreference) findPreference("location");
        setLocationListPreferenceData(locationListPreference);

        locationListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                setLocationListPreferenceData(locationListPreference);
                return false;
            }
        });


        final SwitchPreference frontLightSwitchPreference = (SwitchPreference) findPreference("frontLight");

        frontLightSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object checkState) {
                boolean isFrontLightEnabled = ((Boolean) checkState).booleanValue();

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isFrontLightEnabled) {
                    editor.putString("zxing_preferences_front_light_mode", "ON");
                } else {
                    editor.putString("zxing_preferences_front_light_mode", "OFF");
                }

                editor.commit();

                return true;
            }
        });

        final EditTextPreference webServiceAddressPreference = (EditTextPreference) findPreference("webServiceAddress");
        webServiceAddressPreference.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String urlString = (String)newValue;
                        boolean modified = false;
                        if(!URLUtil.isHttpUrl(urlString))
                        {
                            urlString = "http://" + urlString;
                            modified= true;
                        }
                        if(!urlString.endsWith("/"))
                        {
                            urlString += "/";
                            modified = true;
                        }

                        if(modified)
                        {
                            EditTextPreference editTextPreference = (EditTextPreference)preference;
                            editTextPreference.setText(urlString);
                            return false;
                        }

                        return true;
                    }
                }
        );

    }

    protected void setUserRoleListPreferenceData(ListPreference lp) {
        DataSource datasource = new DataSource(this.getActivity());
        datasource.open();
        ArrayList<String> userRoles = datasource.getAllUserRoles();
        datasource.close();

        List<CharSequence> userRolesCharSequenceList = new ArrayList<>();
        for(String userRole : userRoles)
        {
            userRolesCharSequenceList.add(userRole);
        }

        lp.setEntries(userRolesCharSequenceList.toArray(new CharSequence[userRolesCharSequenceList.size()]));
        lp.setEntryValues(userRolesCharSequenceList.toArray(new CharSequence[userRolesCharSequenceList.size()]));
    }

    protected void setLocationListPreferenceData(ListPreference lp) {
        DataSource datasource = new DataSource(this.getActivity());
        datasource.open();
        ArrayList<LocationModel> locations = datasource.getAllLocations();
        datasource.close();

        List<CharSequence> userRolesCharSequenceList = new ArrayList<>();
        List<CharSequence> userRolesCharSequenceValueList = new ArrayList<>();
        for(LocationModel location : locations)
        {
            userRolesCharSequenceList.add(location.getDescription());
            userRolesCharSequenceValueList.add(location.getCode());
        }

        lp.setEntries(userRolesCharSequenceList.toArray(new CharSequence[userRolesCharSequenceList.size()]));
        lp.setEntryValues(userRolesCharSequenceValueList.toArray(new CharSequence[userRolesCharSequenceValueList.size()]));
    }
}
