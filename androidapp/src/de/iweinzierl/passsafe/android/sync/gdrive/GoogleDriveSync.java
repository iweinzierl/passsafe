package de.iweinzierl.passsafe.android.sync.gdrive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.metadata.StringMetadataField;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;

import android.app.Activity;

import android.content.IntentSender;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.util.FileUtils;

public class GoogleDriveSync implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    ResultCallback<DriveApi.MetadataBufferResult> {

    public interface Callback {
        void onSyncFinished();
    }

    private static final Logger LOGGER = new Logger("GoogleDriveSync");
    private static final String PASSSAFE_DATABASE_FILE = "passsafe.sqlite";

    private final Activity activity;
    private final GoogleApiClient googleApiClient;

    private Callback callback;
    private boolean syncRequested;

    public GoogleDriveSync(final Activity activity) {
        this.activity = activity;

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

            //J-
            PendingResult<DriveApi.MetadataBufferResult> queryResult = Drive.DriveApi.query(googleApiClient,
                new Query.Builder()
                        .addFilter(
                                Filters.and(
                                        Filters.eq(new StringMetadataField("title"), PASSSAFE_DATABASE_FILE),
                                        Filters.eq(new StringMetadataField("trashed"), "false")))
                        .build());
            //J+

            queryResult.setResultCallback(this);
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

    @Override
    public void onResult(final DriveApi.MetadataBufferResult result) {
        if (!result.getStatus().isSuccess()) {
            LOGGER.warn(String.format("Did not find a '%s' file in GoogleDrive account", PASSSAFE_DATABASE_FILE));

        } else {
            MetadataBuffer metadataBuffer = result.getMetadataBuffer();

            if (metadataBuffer.getCount() > 0) {
                LOGGER.debug("Found " + metadataBuffer.getCount() + " results for search");
                sync(metadataBuffer.get(0));
            } else {
                LOGGER.warn("No files found for synchronization");
                callback.onSyncFinished();
            }
        }
    }

    private void sync(final Metadata metadata) {
        File dbFile = FileUtils.getDatabaseFile(activity);
        LOGGER.debug(String.format("Local database file is located at: %s", dbFile.getAbsolutePath()));
        LOGGER.debug(String.format("Local database file is %s big", dbFile.length()));

        if (isDownloadRequired(dbFile, metadata)) {
            LOGGER.debug("Download of database required");
            download(dbFile, metadata);
        } else if (isUploadRequired(dbFile, metadata)) {
            LOGGER.debug("Upload of database required");
            LOGGER.error("Upload currently not implemented");
            callback.onSyncFinished();
        }
    }

    private boolean isDownloadRequired(final File dbFile, final Metadata metadata) {
        if (!dbFile.exists()) {
            return true;
        }

        long dbFileLastModified = dbFile.lastModified();
        long onlineLastModified = metadata.getModifiedDate().getTime();

        LOGGER.debug("Current local time:       " + new Date(System.currentTimeMillis()));
        LOGGER.debug("Last local  modification: " + new Date(dbFileLastModified));
        LOGGER.debug("Last online modification: " + new Date(onlineLastModified));

        return onlineLastModified > dbFileLastModified;
    }

    private boolean isUploadRequired(final File dbFile, final Metadata metadata) {
        if (!dbFile.exists()) {
            return false;
        }

        long dbFileLastModified = dbFile.lastModified();
        long onlineLastModified = metadata.getModifiedDate().getTime();

        return onlineLastModified < dbFileLastModified;
    }

    private void download(final File dbFile, final Metadata metadata) {
        LOGGER.debug("Download online database file now...");

        DriveFile file = Drive.DriveApi.getFile(googleApiClient, metadata.getDriveId());
        PendingResult<DriveApi.ContentsResult> contentsResultPendingResult = file.openContents(googleApiClient,
                DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
                    @Override
                    public void onProgress(final long downloaded, final long expected) {
                        LOGGER.debug(String.format("Downloaded = %s  | Expected = %s", downloaded, expected));
                    }
                }); // add listener here

        contentsResultPendingResult.setResultCallback(new ResultCallback<DriveApi.ContentsResult>() {
                @Override
                public void onResult(final DriveApi.ContentsResult contentsResult) {
                    LOGGER.debug("Copy online database file to disk");
                    try {
                        InputStream in = contentsResult.getContents().getInputStream();
                        OutputStream out = new FileOutputStream(dbFile);

                        org.apache.commons.io.IOUtils.copy(in, out);

                        LOGGER.info(String.format("Download finished: %s bytes", dbFile.length()));

                        callback.onSyncFinished();

                    } catch (IOException e) {
                        LOGGER.error("Unable to download database file", e);
                    }
                }
            });
    }
}
