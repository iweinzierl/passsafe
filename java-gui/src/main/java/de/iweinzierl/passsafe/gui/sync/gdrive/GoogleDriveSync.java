package de.iweinzierl.passsafe.gui.sync.gdrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.sync.Sync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GoogleDriveSync implements Sync {

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

    public GoogleDriveSync(Configuration configuration) throws Exception {
        this.configuration = configuration;
        this.dataStoreDir = new FileDataStoreFactory(getOrCreateStoreDir(DRIVE_STORE_DIR));
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.jsonFactory = new JacksonFactory();

        this.client = new Drive.Builder(httpTransport, jsonFactory, authorize()).setApplicationName(APP_NAME).build();
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.parse(Configuration.DEFAULT_CONFIGURATION_FILE);

        GoogleDriveSync driveSync = new GoogleDriveSync(configuration);
        driveSync.sync("passsafe.sqlite");
    }

    private java.io.File getOrCreateStoreDir(String dirName) {
        String baseFolder = configuration.getBaseFolder();
        java.io.File driveStoreDir = new java.io.File(baseFolder, dirName);
        if (!driveStoreDir.exists()) {
            driveStoreDir.mkdir();
        }

        return driveStoreDir;
    }

    @Override
    public void sync(String filename) throws IOException {
        DateTime onlineModificationDate = getOnlineModificationDate(filename);

        long onlineModificationTime = onlineModificationDate != null ? onlineModificationDate.getValue() : 0;
        long localModificationDate = getLocalModificationDate(filename);

        if (onlineModificationTime > localModificationDate) {
            long diff = onlineModificationTime - localModificationDate;
            LOGGER.info("File '{}' needs to be downloaded from GoogleDrive: {} sec younger", filename, diff / 1000);
            download(filename);
        } else {
            long diff = localModificationDate - onlineModificationTime;
            LOGGER.info("File '{}' needs to be uploaded to GoogleDrive: {} sec younger", filename, diff / 1000);
            upload(filename);
        }
    }

    private File find(String filename) throws IOException {
        String q = String.format("title = '%s' and trashed = false", filename);

        final Drive.Files.List request = client.files().list();

        FileList fileList = request.setQ(q).execute();

        do {

            List<File> items = fileList.getItems();

            if (items != null && !items.isEmpty()) {
                //return items.get(0);
                for (File file : items) {
                    LOGGER.debug("Title = {}", file.getTitle());
                }
            }

            request.setPageToken(fileList.getNextPageToken());
        } while (!Strings.isNullOrEmpty(request.getPageToken()));

        return null;
    }

    private DateTime getOnlineModificationDate(String filename) throws IOException {
        File online = find(filename);
        if (online == null) {
            return null;
        }

        return online.getModifiedDate();
    }

    private long getLocalModificationDate(String filename) {
        java.io.File passSafeDirectory = new java.io.File(configuration.getBaseFolder());
        java.io.File file = new java.io.File(passSafeDirectory, filename);

        return file.exists() ? file.lastModified() : 0;
    }

    private void download(String filename) throws IOException {
        java.io.File output = new java.io.File(configuration.getBaseFolder(), filename);

        LOGGER.info("Download '{}' to '{}'", filename, output.getAbsoluteFile());

        File file = find(filename);
        HttpRequest request = client.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()));
        HttpResponse response = request.execute();

        IOUtils.copy(response.getContent(), new FileOutputStream(output));
    }

    private void upload(String filename) throws IOException {
        java.io.File file = new java.io.File(configuration.getBaseFolder(), filename);

        LOGGER.info("Upload '{}' to Google Drive", file.getAbsoluteFile());

        File fileMetadata = new File();
        fileMetadata.setTitle(file.getName());

        FileContent mediaContent = new FileContent("application/octet-stream", file);

        Drive.Files.Insert insert = client.files().insert(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(true);
        //uploader.setProgressListener(new FileUploadProgressListener());

        insert.execute();
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
