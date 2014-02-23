package de.iweinzierl.passsafe.android.activity.settings;

import android.app.Activity;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.logging.Logger;

public class SettingsActivity extends Activity {

    private static final Logger LOGGER = new Logger("SettingsActivity");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
