package de.iweinzierl.passsafe.android.activity.settings;

import android.content.SharedPreferences;

import android.os.Bundle;

import android.preference.PreferenceFragment;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Logger LOGGER = new Logger("SettingsFragment");

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        LOGGER.info("SharedPreference changed: " + key);
    }
}
