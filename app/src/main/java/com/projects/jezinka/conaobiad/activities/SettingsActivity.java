package com.projects.jezinka.conaobiad.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.projects.jezinka.conaobiad.R;

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

    private static int takeFromPreferences(String key, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String daysNoPrefs = sharedPref.getString(key, "7");
        return Integer.parseInt(daysNoPrefs);
    }

    public static int getPlanLength(Context context) {
        return takeFromPreferences(SettingsActivity.PREFS_DAYS_NO, context);
    }

    public static int getFirstDay(Context context) {
        return takeFromPreferences(SettingsActivity.PREFS_FIRST_DAY, context);
    }
}