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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.DatabaseSyncProcessor;
import de.iweinzierl.passsafe.gui.data.SqliteDataSource;
import de.iweinzierl.passsafe.gui.sync.Sync;
import de.iweinzierl.passsafe.gui.util.FileUtils;
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;
import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

public class GoogleDriveSync implements Sync {

    public enum State {
        SYNC_REQUESTED,
        DOWNLOAD_FINISHED,
        DOWNLOAD_FAILED,
        UPLOAD_REQUIRED,
        UPLOAD_FAILED,
        SYNC_FINISHED
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
    private final PassSafeDataSource localDatabase;

    private File localDatabaseFile;
    private File tempDatabase;

    public GoogleDriveSync(final Configuration configuration, final PassSafeDataSource localDatabase) throws Exception {
        this.configuration = configuration;
        this.localDatabase = localDatabase;
        this.dataStoreDir = new FileDataStoreFactory(FileUtils.getOrCreateStoreDir(configuration, DRIVE_STORE_DIR));
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.jsonFactory = new JacksonFactory();

        this.client = new Drive.Builder(httpTransport, jsonFactory, authorize()).setApplicationName(APP_NAME).build();
    }

    public static void main(final String[] args) throws Exception {
        Configuration configuration = Configuration.parse(Configuration.DEFAULT_CONFIGURATION_FILE);

        SqliteDataSource dataSource = new SqliteDataSource(FileUtils.getLocalDatabaseFile(configuration)
                    .getAbsolutePath());

        GoogleDriveSync driveSync = new GoogleDriveSync(configuration, dataSource);

        // driveSync.sync();
        driveSync.onStateChanged(State.DOWNLOAD_FINISHED);
    }

    @Override
    public void sync() throws IOException {
        localDatabaseFile = FileUtils.getLocalDatabaseFile(configuration);
        tempDatabase = FileUtils.getTempDatabaseFile(configuration);

        onStateChanged(State.SYNC_REQUESTED);
    }

    public void onStateChanged(final State nextState) {
        switch (nextState) {

            case SYNC_REQUESTED :
                startDownload();
                break;

            case DOWNLOAD_FINISHED :
                startSynchronizeDatabases();
                break;

            case DOWNLOAD_FAILED :

                // TODO display somehow a dialog to inform the user
                break;

            case UPLOAD_REQUIRED :
                startUpload();
                break;

            case UPLOAD_FAILED :

                // TODO display somehow a dialog to inform the user
                break;

            case SYNC_FINISHED :
                deleteTempDatabase();
                break;
        }
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

    private void startSynchronizeDatabases() {
        File tempDb = FileUtils.getTempDatabaseFile(configuration);

        try {
            SqliteDataSource tempDatasource = new SqliteDataSource(tempDb.getAbsolutePath());

            new DatabaseSyncProcessor(this, localDatabase, tempDatasource).sync();
            tempDatasource.close();

        } catch (SQLException | ClassNotFoundException | IOException | PassSafeSqlException e) {
            LOGGER.error("Unable to synchronize databases", e);
        }
    }

    private void startDownload() {
        new GoogleDriveDownload(this, client).download(localDatabaseFile.getName(), tempDatabase);
    }

    private void startUpload() {
        new GoogleDriveUpload(this, client).upload(localDatabaseFile);
    }

    private void deleteTempDatabase() {
        if (tempDatabase.exists()) {
            if (!tempDatabase.delete()) {
                LOGGER.warn("Unable to delete temporary database file downloaded for synchronization");
            }
        }
    }
}
