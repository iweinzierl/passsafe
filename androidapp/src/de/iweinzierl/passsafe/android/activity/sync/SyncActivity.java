package de.iweinzierl.passsafe.android.activity.sync;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync;

public class SyncActivity extends Activity {

    public static final int GDRIVE_LOGIN_REQUEST = 1001;

    private static final Logger LOGGER = new Logger("SyncActivity");

    private GoogleDriveSync sync;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sync = new GoogleDriveSync(this);
        sync.sync();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        switch (requestCode) {

            case GDRIVE_LOGIN_REQUEST :
                onGDriveLoginResult(resultCode, data);
                break;

            default :
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onGDriveLoginResult(final int resultCode, final Intent data) {
        LOGGER.info("GoogleDrive login result: " + resultCode);
        for (String key : data.getExtras().keySet()) {
            LOGGER.info("   Extra: " + key + " = " + data.getStringExtra(key));
        }
    }
}
