package de.iweinzierl.passsafe.android.sync.gdrive;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import android.content.Context;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.logging.Logger;

public class GoogleDriveSync implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private static final Logger LOGGER = new Logger("GoogleDriveSync");

    private final Context context;

    public GoogleDriveSync(final Context context) {
        this.context = context;
    }

    public void sync() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(Drive.API)
                                                                              .addScope(Drive.SCOPE_FILE)
                                                                              .addConnectionCallbacks(this)
                                                                              .addOnConnectionFailedListener(this)
                                                                              .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(final Bundle bundle) {
        LOGGER.info("Connected to GDrive API");
    }

    @Override
    public void onConnectionSuspended(final int i) {
        LOGGER.info("Suspend GDrive API");
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        LOGGER.error(String.format("Connection to GDrive API failed: %s", connectionResult.toString()));
    }
}
