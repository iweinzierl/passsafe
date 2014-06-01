package de.iweinzierl.passsafe.android.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences {

    private static final String SHARED_PREFERENCES_NAME = "de.inselhome.passsafe.prefs";

    private static final String FIRST_APP_START = "dip.app.firststart";
    private static final String SYNC_ENABLED = "dip.app.sync.enabled";

    private final SharedPreferences sharedPreferences;

    public ApplicationPreferences(final Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstAppStart() {
        return sharedPreferences.getBoolean(FIRST_APP_START, true);
    }

    public void setFirstAppStart(final boolean firstAppStart) {
        sharedPreferences.edit().putBoolean(FIRST_APP_START, firstAppStart).commit();
    }

    public boolean isSyncEnabled() {
        return sharedPreferences.getBoolean(SYNC_ENABLED, false);
    }

    public void enableDatabaseSync() {
        sharedPreferences.edit().putBoolean(SYNC_ENABLED, true).commit();
    }
}
