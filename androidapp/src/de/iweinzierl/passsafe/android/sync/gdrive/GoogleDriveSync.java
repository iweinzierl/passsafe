package de.iweinzierl.passsafe.android.sync.gdrive;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import android.app.Activity;

import android.content.IntentSender;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.logging.Logger;

public class GoogleDriveSync implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    public interface Callback {
        void onSyncFinished();
    }

    public enum State {
        DOWNLOAD_REQUESTED,
        DOWNLOAD_CANCELED,
        DOWNLOAD_FINISHED,
        UPLOAD_REQUESTED,
        DATABASE_SYNC_REQUESTED,
        COMPLETED
    }

    private static final Logger LOGGER = new Logger("GoogleDriveSync");

    private final Activity activity;
    private final GoogleApiClient googleApiClient;

    private Callback callback;
    private State currentState;
    private boolean syncRequested;

    public GoogleDriveSync(final Activity activity) {
        this.activity = activity;
        this.currentState = State.DOWNLOAD_REQUESTED;

        //J-
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //J+

        if (activity instanceof Callback) {
            callback = (Callback) activity;
        }
    }

    public void sync() {
        if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
            syncRequested = true;
            connect();
        } else {
            onUpdate(currentState);
        }
    }

    public void onUpdate(final State newState) {
        currentState = newState;

        switch (currentState) {

            case DOWNLOAD_REQUESTED :

                new GoogleDriveDownload(activity, this, googleApiClient).download();
                break;

            case DOWNLOAD_CANCELED :

                callback.onSyncFinished();
                break;

            case DOWNLOAD_FINISHED :

                onUpdate(State.DATABASE_SYNC_REQUESTED);
                break;

            case UPLOAD_REQUESTED :

                // TODO
                callback.onSyncFinished();
                break;

            case DATABASE_SYNC_REQUESTED :

                // TODO
                callback.onSyncFinished();
                break;

            case COMPLETED :

                callback.onSyncFinished();
                break;
        }
    }

    private void connect() {
        LOGGER.info("Try to connect to GoogleDrive");
        googleApiClient.connect();
    }

    @Override
    public void onConnected(final Bundle bundle) {
        LOGGER.info("Connected to GDrive API");
        if (syncRequested) {
            LOGGER.debug("Synchronization requested, sync now");

            syncRequested = false;
            sync();
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {
        LOGGER.info("Suspend GDrive API");
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        LOGGER.error(String.format("Connection to GDrive API failed: %s", connectionResult.toString()));

        if (connectionResult.hasResolution() && !googleApiClient.isConnecting()) {
            try {
                LOGGER.info("Trying to resolve connection result");
                connectionResult.startResolutionForResult(activity, 1);
            } catch (IntentSender.SendIntentException e) {
                LOGGER.warn("Connection failed after trying to resolve result");
                connect();
            }
        }
    }
}
