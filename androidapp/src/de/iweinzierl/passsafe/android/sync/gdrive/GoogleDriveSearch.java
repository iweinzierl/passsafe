package de.iweinzierl.passsafe.android.sync.gdrive;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.metadata.StringMetadataField;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;

import de.iweinzierl.passsafe.android.logging.Logger;

public class GoogleDriveSearch implements ResultCallback<DriveApi.MetadataBufferResult> {

    public interface Callback {

        void onSearchSucceeded(Metadata metadata);

        void onSearchFailed();
    }

    private static final Logger LOGGER = new Logger("GoogleDriveSearch");

    private final GoogleApiClient googleApiClient;
    private final Callback callback;
    private final String fileName;

    public GoogleDriveSearch(final GoogleApiClient googleApiClient, final Callback callback, final String fileName) {
        this.googleApiClient = googleApiClient;
        this.callback = callback;
        this.fileName = fileName;
    }

    public void search() {
        //J-
        PendingResult<DriveApi.MetadataBufferResult> queryResult = Drive.DriveApi.query(googleApiClient,
                new Query.Builder()
                        .addFilter(
                                Filters.and(
                                        Filters.eq(new StringMetadataField("title"), fileName),
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
            LOGGER.warn(String.format("Did not find a '%s' file in GoogleDrive account", fileName));

        } else {
            MetadataBuffer metadataBuffer = result.getMetadataBuffer();

            if (metadataBuffer.getCount() > 0) {
                LOGGER.debug("Found " + metadataBuffer.getCount() + " results for search");
                callback.onSearchSucceeded(metadataBuffer.get(0));
            } else {
                LOGGER.warn("No files found for synchronization");
                callback.onSearchFailed();
            }
        }
    }
}
