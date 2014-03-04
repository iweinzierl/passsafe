package de.iweinzierl.passsafe.android.sync.gdrive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;

import de.iweinzierl.passsafe.android.logging.Logger;

public class GoogleDriveDownload {

    private static final String PASSSAFE_DATABASE_FILE = "passsafe.sqlite";

    private static final Logger LOGGER = new Logger("GoogleDriveDownload");

    private final GoogleDriveSync googleDriveSync;
    private final GoogleApiClient googleApiClient;
    private final File destination;

    public GoogleDriveDownload(final GoogleDriveSync googleDriveSync, final GoogleApiClient googleApiClient,
            final File destination) {

        this.googleDriveSync = googleDriveSync;
        this.googleApiClient = googleApiClient;
        this.destination = destination;
    }

    /**
     * Download PassSafe database file from GoogleDrive online account. If download is finished
     * {@link de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync#onUpdate(de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync.State)}
     * is called.
     */
    public void download() {
        new GoogleDriveSearch(googleApiClient, new GoogleDriveSearch.Callback() {
                @Override
                public void onSearchSucceeded(final Metadata metadata) {
                    download(destination, metadata);
                }

                @Override
                public void onSearchFailed() {
                    googleDriveSync.onUpdate(GoogleDriveSync.State.DOWNLOAD_CANCELED);
                }
            }, PASSSAFE_DATABASE_FILE).search();
    }

    private void download(final File dbFile, final Metadata metadata) {
        LOGGER.debug("Download online database file now...");

        Drive.DriveApi.requestSync(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(final Status status) {
                    LOGGER.info("GoogleDrive sync request finished: %s", status.isSuccess());

                    DriveFile file = Drive.DriveApi.getFile(googleApiClient, metadata.getDriveId());
                    PendingResult<DriveApi.ContentsResult> contentsResultPendingResult = file.openContents(
                            googleApiClient, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
                                @Override
                                public void onProgress(final long downloaded, final long expected) {
                                    LOGGER.debug(String.format("Downloaded %s of %s", downloaded, expected));
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

                                    googleDriveSync.onUpdate(GoogleDriveSync.State.DOWNLOAD_FINISHED);

                                } catch (IOException e) {
                                    LOGGER.error("Unable to download database file", e);
                                }
                            }
                        });
                }
            });
    }
}
