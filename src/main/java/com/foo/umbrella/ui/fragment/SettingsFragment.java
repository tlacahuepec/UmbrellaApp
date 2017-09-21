package com.foo.umbrella.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.foo.umbrella.R;

import java.util.Map;

/**
 * Created by tlacahuepec on 9/20/17.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_ZIP_CODE = "pref_zip_code";
    public static final String KEY_PREF_TEMP_UNIT = "pref_temp_unit";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        // we want to watch the preference values' changes
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Map<String, ?> preferencesMap = sharedPreferences.getAll();
        // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
            String key = preferenceEntry.getKey();
            setPreferencesSummary(key);
        }
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    //Listeners
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setPreferencesSummary(key);
    }

    private void setPreferencesSummary(String key) {
        Preference preference = findPreference(key);
        if (key.equals(KEY_PREF_ZIP_CODE)) {
            EditTextPreference editTextPreference = ((EditTextPreference) preference);
            preference.setSummary(editTextPreference.getText());
        } else if (key.equals(KEY_PREF_TEMP_UNIT)) {
            ListPreference listPref = (ListPreference) preference;
            preference.setSummary(listPref.getEntry());
        }
    }

}