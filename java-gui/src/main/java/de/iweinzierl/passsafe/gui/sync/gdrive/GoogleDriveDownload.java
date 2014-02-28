package de.iweinzierl.passsafe.gui.sync.gdrive;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.common.base.Strings;

import de.iweinzierl.passsafe.gui.configuration.Configuration;

public class GoogleDriveDownload {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveDownload.class);

    private final GoogleDriveSync googleDriveSync;
    private final Configuration configuration;
    private final Drive client;

    public GoogleDriveDownload(final GoogleDriveSync googleDriveSync, final Configuration configuration,
            final Drive client) {

        this.googleDriveSync = googleDriveSync;
        this.configuration = configuration;
        this.client = client;
    }

    public void download(final String filename, final java.io.File destination) {
        try {
            File online = find(filename);
            java.io.File local = getLocalFile(filename);

            DateTime onlineModificationDate = getOnlineModificationDate(online);

            long onlineModificationTime = onlineModificationDate != null ? onlineModificationDate.getValue() : 0;
            long localModificationDate = getLocalModificationDate(local);

            if (onlineModificationTime > localModificationDate) {
                long diff = onlineModificationTime - localModificationDate;
                LOGGER.info("File '{}' needs to be downloaded from GoogleDrive: {} sec younger", filename, diff / 1000);
                download(online, destination);
            } else {
                long diff = localModificationDate - onlineModificationTime;
                LOGGER.info("File '{}' needs to be uploaded to GoogleDrive: {} sec younger", filename, diff / 1000);
                googleDriveSync.onStateChanged(GoogleDriveSync.State.UPLOAD_REQUIRED);
            }
        } catch (IOException ioe) {
            LOGGER.error("Downloading of file {} failed", filename, ioe);
            googleDriveSync.onStateChanged(GoogleDriveSync.State.DOWNLOAD_FAILED);
        }
    }

    private File find(final String filename) throws IOException {
        String q = String.format("title = '%s' and trashed = false", filename);

        final Drive.Files.List request = client.files().list();

        FileList fileList = request.setQ(q).execute();

        do {

            List<File> items = fileList.getItems();

            if (items != null && !items.isEmpty()) {
                return items.get(0);
            }

            request.setPageToken(fileList.getNextPageToken());
        } while (!Strings.isNullOrEmpty(request.getPageToken()));

        return null;
    }

    private DateTime getOnlineModificationDate(final File online) throws IOException {
        if (online == null) {
            return null;
        }

        return online.getModifiedDate();
    }

    private java.io.File getLocalFile(final String filename) {
        java.io.File passSafeDirectory = new java.io.File(configuration.getBaseFolder());
        return new java.io.File(passSafeDirectory, filename);
    }

    private long getLocalModificationDate(final java.io.File file) {
        return file.exists() ? file.lastModified() : 0;
    }

    private void download(final File file, final java.io.File destination) throws IOException {
        LOGGER.info("Download '{}' to '{}'", file.getTitle(), destination.getAbsoluteFile());

        HttpRequest request = client.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()));
        HttpResponse response = request.execute();

        IOUtils.copy(response.getContent(), new FileOutputStream(destination));
    }
}
