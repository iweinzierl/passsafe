package de.iweinzierl.passsafe.android.sync.gdrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;

import android.app.Activity;

import de.iweinzierl.passsafe.android.logging.Logger;

public class GoogleDriveUpload {

    private static final Logger LOGGER = new Logger("GoogleDriveUpload");

    private final Activity activity;
    private final GoogleDriveSync googleDriveSync;
    private final GoogleApiClient googleApiClient;

    public GoogleDriveUpload(final Activity activity, final GoogleDriveSync googleDriveSync,
            final GoogleApiClient googleApiClient) {
        this.activity = activity;
        this.googleDriveSync = googleDriveSync;
        this.googleApiClient = googleApiClient;
    }

    public void upload(final File databaseFile) {
        new GoogleDriveSearch(googleApiClient, new GoogleDriveSearch.Callback() {
                @Override
                public void onSearchSucceeded(final Metadata metadata) {
                    upload(databaseFile, metadata);
                }

                @Override
                public void onSearchFailed() {
                    googleDriveSync.onUpdate(GoogleDriveSync.State.DOWNLOAD_CANCELED);
                }
            }, databaseFile.getName()).search();
    }

    private void upload(final File databaseFile, final Metadata metadata) {
        LOGGER.debug("Going to upload database file '%s' to GoogleDrive", databaseFile.getName());

        final DriveId driveId = metadata.getDriveId();
        final DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, driveId);
        final PendingResult<DriveApi.ContentsResult> pendingResult = driveFile.openContents(googleApiClient,
                DriveFile.MODE_WRITE_ONLY, null);

        LOGGER.debug("Synchronize upstream file to prepare upload");

        final DriveApi.ContentsResult upstreamFile = pendingResult.await();

        LOGGER.debug("Upload file '%s' to GoogleDrive", databaseFile.getName());

        try {
            Contents contents = upstreamFile.getContents();

            OutputStream outputStream = contents.getOutputStream();
            IOUtils.copy(new FileInputStream(databaseFile), outputStream);

            LOGGER.debug("Updated content of file '" + databaseFile.getName() + "' successfully");

            driveFile.commitAndCloseContents(googleApiClient, contents);

            LOGGER.info("Upload of file '%s' finished", databaseFile.getName());

        } catch (IOException ioe) {
            LOGGER.error("Upload of file '%s' failed", ioe, databaseFile.getName());
        }
    }
}
