package de.iweinzierl.passsafe.gui.sync.gdrive;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;

public class GoogleDriveUpload {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveUpload.class);

    private final GoogleDriveSync googleDriveSync;
    private final Drive client;

    public GoogleDriveUpload(final GoogleDriveSync googleDriveSync, final Drive client) {
        this.googleDriveSync = googleDriveSync;
        this.client = client;
    }

    public void upload(final File local) {
        try {
            com.google.api.services.drive.model.File online = new GoogleDriveSearch(client).search(local.getName());
            upload(local, online);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(final File local, final com.google.api.services.drive.model.File online) {

        LOGGER.info("Upload '{}' to Google Drive", local.getAbsoluteFile());

        try {
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setTitle(local.getName());

            if (online != null) {
                fileMetadata.setId(online.getId());
            }

            FileContent mediaContent = new FileContent("application/octet-stream", local);
            DriveRequest<com.google.api.services.drive.model.File> request;

            if (online != null) {
                request = client.files().update(online.getId(), online, mediaContent);
            } else {
                request = client.files().insert(fileMetadata, mediaContent);
            }

            MediaHttpUploader uploader = request.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(true);
            // uploader.setProgressListener(new FileUploadProgressListener());

            request.execute();
        } catch (IOException e) {
            LOGGER.error("Upload of database failed", e);
            googleDriveSync.onStateChanged(GoogleDriveSync.State.UPLOAD_FAILED);
        }
    }

}
