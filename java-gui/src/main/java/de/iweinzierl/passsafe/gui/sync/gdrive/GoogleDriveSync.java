package de.iweinzierl.passsafe.gui.sync.gdrive;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.DriveScopes;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.DatabaseSyncProcessor;
import de.iweinzierl.passsafe.gui.data.SqliteDataSource;
import de.iweinzierl.passsafe.gui.sync.Sync;
import de.iweinzierl.passsafe.gui.util.FileUtils;
import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

public class GoogleDriveSync implements Sync {

    public enum State {
        DOWNLOAD_FINISHED
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveSync.class);

    private static final String APP_NAME = "de.iweinzierl.PassSafe";

    private static final String DRIVE_STORE_DIR = "gdrive";

    private static final String CLIENT_ID = "641661793300.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "msRYKVpI1T1NKS436VPBATvy";

    private final HttpTransport httpTransport;
    private final FileDataStoreFactory dataStoreDir;
    private final JsonFactory jsonFactory;

    private final Drive client;
    private final Configuration configuration;

    public GoogleDriveSync(final Configuration configuration) throws Exception {
        this.configuration = configuration;
        this.dataStoreDir = new FileDataStoreFactory(getOrCreateStoreDir(DRIVE_STORE_DIR));
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.jsonFactory = new JacksonFactory();

        this.client = new Drive.Builder(httpTransport, jsonFactory, authorize()).setApplicationName(APP_NAME).build();
    }

    public static void main(final String[] args) throws Exception {
        Configuration configuration = Configuration.parse(Configuration.DEFAULT_CONFIGURATION_FILE);

        GoogleDriveSync driveSync = new GoogleDriveSync(configuration);

        // driveSync.sync("passsafe.sqlite");
        driveSync.onStateChanged(State.DOWNLOAD_FINISHED);
    }

    private File getOrCreateStoreDir(final String dirName) {
        String baseFolder = configuration.getBaseFolder();
        File driveStoreDir = new File(baseFolder, dirName);
        if (!driveStoreDir.exists()) {
            driveStoreDir.mkdir();
        }

        return driveStoreDir;
    }

    @Override
    public void sync(final String filename) throws IOException {
        new GoogleDriveDownload(this, configuration, client).download(filename);
    }

    public void onStateChanged(final State nextState) {
        switch (nextState) {

            case DOWNLOAD_FINISHED :
                synchronizeDatabases();
        }
    }

    public void uploadRequired(final File local, final com.google.api.services.drive.model.File online) {
        try {
            upload(local, online);
        } catch (IOException e) {
            LOGGER.error("Upload of file {} failed", local.getName(), e);
        }
    }

    private void synchronizeDatabases() {
        File localDb = FileUtils.getLocalDatabaseFile(configuration);
        File tempDb = FileUtils.getTempDatabaseFile(configuration);

        try {
            SqliteDataSource localDatasource = new SqliteDataSource(localDb.getAbsolutePath());
            SqliteDataSource tempDatasource = new SqliteDataSource(tempDb.getAbsolutePath());

            new DatabaseSyncProcessor(localDatasource, tempDatasource).sync();
        } catch (SQLException | ClassNotFoundException | IOException | PassSafeSqlException e) {
            LOGGER.error("Unable to synchronize databases", e);
        }
    }

    private void upload(final File local, final com.google.api.services.drive.model.File online) throws IOException {

        // TODO override existing file
        LOGGER.info("Upload '{}' to Google Drive", local.getAbsoluteFile());

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
    }

    private Credential authorize() throws Exception {

        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(CLIENT_ID);
        details.setClientSecret(CLIENT_SECRET);

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setInstalled(details);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
                clientSecrets, DriveScopes.all()).setDataStoreFactory(dataStoreDir).build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
