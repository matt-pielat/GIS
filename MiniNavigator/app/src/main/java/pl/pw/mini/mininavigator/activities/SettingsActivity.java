package pl.pw.mini.mininavigator.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import pl.pw.mini.mininavigator.R;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new AppPrefsFragment())
                .commit();
    }

    public static class AppPrefsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            // Set init summary text to current value.
            EditTextPreference preference = (EditTextPreference) findPreference("pref_gisServiceAddress");
            preference.setSummary(preference.getText());
        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference preference = findPreference(key);


            if (preference instanceof EditTextPreference)
                preference.setSummary(((EditTextPreference) preference).getText());
        }
    }
}
