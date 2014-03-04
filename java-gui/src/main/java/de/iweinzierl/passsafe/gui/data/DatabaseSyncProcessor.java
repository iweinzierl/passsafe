package de.iweinzierl.passsafe.gui.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.gui.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.shared.data.DatabaseData;
import de.iweinzierl.passsafe.shared.data.DatabaseSyncHelper;
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class DatabaseSyncProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSyncProcessor.class);

    private final GoogleDriveSync googleDriveSync;
    private final PassSafeDataSource localDatasource;
    private final PassSafeDataSource onlineDatasource;

    public DatabaseSyncProcessor(final GoogleDriveSync googleDriveSync, final PassSafeDataSource localDatasource,
            final SqliteDataSource onlineDatasource) {
        this.googleDriveSync = googleDriveSync;
        this.localDatasource = localDatasource;
        this.onlineDatasource = onlineDatasource;
    }

    /**
     * Starts synchronizing the local database with the downloaded database. At the end, it returns true if an upload is
     * required or false if not.
     *
     * @return  true if an upload of the synchronized database is required or false if not.
     */
    public boolean sync() {
        DatabaseSyncHelper helper = new DatabaseSyncHelper(getDatabaseData(localDatasource),
                getDatabaseData(onlineDatasource));

        insertCategories(helper.getNewCategories());
        removeCategories(helper.getRemovedCategories());

        updateExistingEntries(helper.getEntriesWithRequiredUpdate());
        insertEntries(helper.getNewEntries());
        removeEntries(helper.getRemovedEntries());

        updateSynchronizationDate();

        boolean uploadRequired = helper.isUploadRequired(getDatabaseEntries(localDatasource),
                getDatabaseEntries(onlineDatasource));

        if (uploadRequired) {
            googleDriveSync.onStateChanged(GoogleDriveSync.State.UPLOAD_REQUIRED);
        }

        googleDriveSync.onStateChanged(GoogleDriveSync.State.SYNC_FINISHED);

        return uploadRequired;
    }

    private boolean insertCategories(final List<DatabaseEntryCategory> newCategories) {
        LOGGER.info("Found {} categories that need to be inserted", newCategories.size());
        for (DatabaseEntryCategory category : newCategories) {
            localDatasource.addCategory(category);
        }

        return !newCategories.isEmpty();
    }

    private boolean removeCategories(final List<DatabaseEntryCategory> removedCategories) {
        LOGGER.info("Found {} categories that need to be removed", removedCategories.size());
        for (DatabaseEntryCategory category : removedCategories) {
            localDatasource.removeCategory(category);
        }

        return !removedCategories.isEmpty();
    }

    /**
     * Update existing entries with the information in the <i>entriesWithRequiredUpdate</i> list.
     *
     * @param   entriesWithRequiredUpdate  The updated entries that must be persisted to local database.
     *
     * @return  true if local entries were updated, otherwise false.
     */
    private boolean updateExistingEntries(final List<DatabaseEntry> entriesWithRequiredUpdate) {
        LOGGER.info("Found {} entries that need to be updated", entriesWithRequiredUpdate.size());

        for (DatabaseEntry entry : entriesWithRequiredUpdate) {
            localDatasource.updateEntry(entry);
        }

        return !entriesWithRequiredUpdate.isEmpty();
    }

    /**
     * Insert new entries into local database.
     *
     * @param   newEntries  The new entries that must be inserted into local database.
     *
     * @return  true if local entries were inserted, otherwise false.
     */
    private boolean insertEntries(final List<DatabaseEntry> newEntries) {
        LOGGER.info("Found {} entries that need to be inserted", newEntries.size());

        for (DatabaseEntry entry : newEntries) {
            localDatasource.addEntry(entry.getCategory(), entry);
        }

        return !newEntries.isEmpty();
    }

    /**
     * Remove all entries contained in the <i>removedEntries</i> list.
     *
     * @param   removedEntries  The entries that must be removed from local database.
     *
     * @return  true if local entries were removed, otherwise false.
     */
    private boolean removeEntries(final List<DatabaseEntry> removedEntries) {
        LOGGER.info("Found {} entries that need to be removed", removedEntries.size());

        for (DatabaseEntry entry : removedEntries) {
            localDatasource.removeEntry(entry);
        }

        return !removedEntries.isEmpty();
    }

    /**
     * Update the synchronization date in the passsafe_metadata table to the current date time.
     */
    private void updateSynchronizationDate() {
        localDatasource.updateSynchronizationDate();
    }

    private static DatabaseData getDatabaseData(final PassSafeDataSource dataSource) {
        List<DatabaseEntryCategory> databaseCategories = new ArrayList<>();
        List<DatabaseEntry> databaseEntries = getDatabaseEntries(dataSource);

        return new DatabaseData(dataSource.getLastSynchronizationDate(), databaseEntries, databaseCategories);
    }

    private static List<DatabaseEntry> getDatabaseEntries(final PassSafeDataSource dataSource) {
        List<DatabaseEntry> databaseEntries = new ArrayList<>();

        for (EntryCategory category : dataSource.getCategories()) {
            databaseEntries.addAll(getDatabaseEntries(dataSource, category));
        }

        return databaseEntries;
    }

    private static Collection<DatabaseEntry> getDatabaseEntries(final PassSafeDataSource dataSource,
            final EntryCategory category) {
        List<Entry> allEntries = dataSource.getAllEntries(category);
        List<DatabaseEntry> databaseEntries = new ArrayList<>(allEntries.size());

        for (Entry entry : allEntries) {
            databaseEntries.add((DatabaseEntry) entry);
        }

        return databaseEntries;
    }
}
