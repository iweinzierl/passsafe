package de.iweinzierl.passsafe.gui.sync.gdrive;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.common.base.Strings;

public class GoogleDriveDownload {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveDownload.class);

    private final GoogleDriveSync googleDriveSync;
    private final Drive client;

    public GoogleDriveDownload(final GoogleDriveSync googleDriveSync, final Drive client) {
        this.googleDriveSync = googleDriveSync;
        this.client = client;
    }

    public void download(final String filename, final java.io.File destination) {
        try {
            File online = find(filename);

            if (online != null) {
                download(online, destination);
            } else {
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

    private void download(final File file, final java.io.File destination) throws IOException {
        LOGGER.info("Download '{}' to '{}'", file.getTitle(), destination.getAbsoluteFile());

        HttpRequest request = client.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()));
        HttpResponse response = request.execute();

        IOUtils.copy(response.getContent(), new FileOutputStream(destination));
    }
}
