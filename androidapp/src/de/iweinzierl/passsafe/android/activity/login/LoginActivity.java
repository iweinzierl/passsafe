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
import de.iweinzierl.passsafe.android.util.Constants;
import de.iweinzierl.passsafe.android.util.FileUtils;

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
    public void login(final String password) {
        LOGGER.info("Received login() event");
        ((PassSafeApplication) getApplication()).setPassword(password);

        // TODO Verify password
        startActivityForResult(new Intent(this, SyncActivity.class), Constants.ACTIVITY_SYNC_REQUEST);
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

    private void checkOrCreateDatabase() {
        File databaseFile = FileUtils.getDatabaseFile(this);
        if (!databaseFile.exists()) {
            ((PassSafeApplication) getApplication()).createNewDatabase();
        }
    }
}
