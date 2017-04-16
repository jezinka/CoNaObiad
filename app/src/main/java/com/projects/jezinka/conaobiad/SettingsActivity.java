package com.projects.jezinka.conaobiad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    final static String PREFS_FIRST_DAY = "PREFS_FIRST_DAY";
    final static String PREFS_DAYS_NO = "PREFS_DAYS_NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

            SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    Preference pref = findPreference(key);

                    if (pref instanceof EditTextPreference) {
                        EditTextPreference editTextPreference = (EditTextPreference) pref;
                        pref.setSummary(editTextPreference.getText());
                    }

                    MainActivity.preferenceChanged = true;
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(listener);

            addPreferencesFromResource(R.xml.preferences);
        }
    }
}