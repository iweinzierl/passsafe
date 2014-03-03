package de.iweinzierl.passsafe.android.sync.gdrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
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
        Drive.DriveApi.newContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.ContentsResult>() {
                @Override
                public void onResult(final DriveApi.ContentsResult contentsResult) {
                    OutputStream outputStream = contentsResult.getContents().getOutputStream();

                    try {
                        IOUtils.copy(new FileInputStream(databaseFile), outputStream);
                        LOGGER.info("Upload of file '" + databaseFile.getName() + "' successful");

                        googleDriveSync.onUpdate(GoogleDriveSync.State.UPLOAD_FINISHED);
                    } catch (IOException e) {
                        LOGGER.error("Upload of file '" + databaseFile.getName() + "' failed", e);
                    }
                }
            });
    }
}
