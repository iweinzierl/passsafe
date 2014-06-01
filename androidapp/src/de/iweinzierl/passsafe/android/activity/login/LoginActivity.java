package de.iweinzierl.passsafe.android.activity.login;

import java.io.File;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.activity.list.ListActivityIntent;
import de.iweinzierl.passsafe.android.activity.sync.SyncActivity;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.preferences.ApplicationPreferences;
import de.iweinzierl.passsafe.android.util.Constants;
import de.iweinzierl.passsafe.android.util.FileUtils;
import de.iweinzierl.passsafe.android.widget.dialog.FirstAppStartDialog;

public class LoginActivity extends Activity implements LoginFragment.ActionHandler {

    private static final Logger LOGGER = new Logger("LoginActivity");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getFragmentManager().beginTransaction().replace(R.id.fragment_login, new LoginFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        displayFirstAppStartDialog();
    }

    @Override
    public void login(final String password) {
        LOGGER.info("Received login() event");
        ((PassSafeApplication) getApplication()).setPassword(password);

        if (isSyncEnabled()) {
            synchronizeDatabase();
        } else {
            checkOrCreateDatabase();
            startActivity(new ListActivityIntent(this));
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == Constants.ACTIVITY_SYNC_REQUEST) {
            LOGGER.info("Returned to Activity (request code = %d | result code = %d)", requestCode, resultCode);

            if (resultCode == Constants.ACTIVITY_SYNC_FINISHED_SUCCESSFUL) {
                checkOrCreateDatabase();
                startActivity(new ListActivityIntent(this));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void displayFirstAppStartDialog() {
        if (isFirstAppStart()) {
            //J-
            new FirstAppStartDialog.Builder(this)
                    .withCallback(new FirstAppStartDialog.Callback() {
                        @Override
                        public void onCreateNewDatabase() {
                            createNewDatabase();
                            updateFirstAppStart();
                        }

                        @Override
                        public void onSynchronizeDatabase() {
                            enableDatabaseSync();
                            updateFirstAppStart();
                        }
                    }).build();
            //J+
        }
    }

    public boolean isFirstAppStart() {
        ApplicationPreferences preferences = ((PassSafeApplication) getApplication()).getApplicationPreferences();
        return preferences.isFirstAppStart();
    }

    private void updateFirstAppStart() {
        ApplicationPreferences preferences = ((PassSafeApplication) getApplication()).getApplicationPreferences();
        preferences.setFirstAppStart(false);
    }

    private void createNewDatabase() {
        ((PassSafeApplication) getApplication()).createNewDatabase();
    }

    private void enableDatabaseSync() {
        ApplicationPreferences preferences = ((PassSafeApplication) getApplication()).getApplicationPreferences();
        preferences.enableDatabaseSync();
    }

    private void synchronizeDatabase() {
        startActivityForResult(new Intent(this, SyncActivity.class), Constants.ACTIVITY_SYNC_REQUEST);
    }

    private boolean isSyncEnabled() {
        ApplicationPreferences preferences = ((PassSafeApplication) getApplication()).getApplicationPreferences();
        return preferences.isSyncEnabled();
    }

    private void checkOrCreateDatabase() {
        File databaseFile = FileUtils.getDatabaseFile(this);
        if (!databaseFile.exists()) {
            createNewDatabase();
        }
    }
}
