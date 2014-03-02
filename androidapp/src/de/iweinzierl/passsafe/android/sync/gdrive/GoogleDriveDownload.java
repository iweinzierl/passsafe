package de.iweinzierl.passsafe.android.sync.gdrive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

import android.content.Context;

import de.iweinzierl.passsafe.android.logging.Logger;

public class GoogleDriveDownload implements ResultCallback<DriveApi.MetadataBufferResult> {

    private static final String PASSSAFE_DATABASE_FILE = "passsafe.sqlite";

    private static final Logger LOGGER = new Logger("GoogleDriveDownload");

    private final Context context;
    private final GoogleDriveSync googleDriveSync;
    private final GoogleApiClient googleApiClient;
    private final File destination;

    public GoogleDriveDownload(final Context context, final GoogleDriveSync googleDriveSync,
            final GoogleApiClient googleApiClient, final File destination) {

        this.context = context;
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

    /**
     * Called after GoogleDrive search for PassSafe database file is finished.
     *
     * @param  result  The search result.
     */
    @Override
    public void onResult(final DriveApi.MetadataBufferResult result) {
        if (!result.getStatus().isSuccess()) {
            LOGGER.warn(String.format("Did not find a '%s' file in GoogleDrive account", PASSSAFE_DATABASE_FILE));

        } else {
            MetadataBuffer metadataBuffer = result.getMetadataBuffer();

            if (metadataBuffer.getCount() > 0) {
                LOGGER.debug("Found " + metadataBuffer.getCount() + " results for search");
                download(destination, metadataBuffer.get(0));
            } else {
                LOGGER.warn("No files found for synchronization");
                googleDriveSync.onUpdate(GoogleDriveSync.State.DOWNLOAD_CANCELED);
            }
        }
    }

    private void download(final File dbFile, final Metadata metadata) {
        LOGGER.debug("Download online database file now...");

        DriveFile file = Drive.DriveApi.getFile(googleApiClient, metadata.getDriveId());
        PendingResult<DriveApi.ContentsResult> contentsResultPendingResult = file.openContents(googleApiClient,
                DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
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
}
