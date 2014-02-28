package de.iweinzierl.passsafe.gui.sync.gdrive;

import java.io.IOException;

import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.common.base.Strings;

public class GoogleDriveSearch {

    private final Drive client;

    public GoogleDriveSearch(final Drive client) {
        this.client = client;
    }

    public File search(final String filename) throws IOException {
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
}
