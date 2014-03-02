package de.iweinzierl.passsafe.android.data;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;

import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.shared.data.DatabaseData;
import de.iweinzierl.passsafe.shared.data.DatabaseSyncHelper;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class DatabaseSyncProcessor {

    private static final Logger LOGGER = new Logger("DatabaseSyncProcessor");

    private final GoogleDriveSync googleDriveSync;
    private final SQLiteRepository localRepository;
    private final DatabaseSyncHelper databaseSyncHelper;

    public DatabaseSyncProcessor(final Activity activity, final GoogleDriveSync googleDriveSync, final File local,
            final File upstream) {
        this.googleDriveSync = googleDriveSync;

        localRepository = new SQLiteRepository(activity, local);

        SQLiteRepository onlineRepository = new SQLiteRepository(activity, upstream);

        databaseSyncHelper = new DatabaseSyncHelper(getDatabaseData(localRepository),
                getDatabaseData(onlineRepository));
    }

    public boolean sync() {
        LOGGER.info("Start synchronizing databases");

        boolean updated = updateExistingEntries(databaseSyncHelper.getEntriesWithRequiredUpdate());
        boolean inserted = insertEntries(databaseSyncHelper.getNewEntries());
        boolean removed = removeEntries(databaseSyncHelper.getRemovedEntries());

        if (updated || inserted || removed) {
            updateSynchronizationDate();
        }

        boolean uploadRequired = databaseSyncHelper.isUploadRequired();

        if (uploadRequired) {
            googleDriveSync.onUpdate(GoogleDriveSync.State.UPLOAD_REQUESTED);
        }

        googleDriveSync.onUpdate(GoogleDriveSync.State.COMPLETED);

        return uploadRequired;
    }

    private boolean updateExistingEntries(final List<DatabaseEntry> entriesWithRequiredUpdate) {
        for (DatabaseEntry entry : entriesWithRequiredUpdate) {
            localRepository.update(entry);
        }

        return !entriesWithRequiredUpdate.isEmpty();
    }

    private boolean insertEntries(final List<DatabaseEntry> newEntries) {
        for (DatabaseEntry entry : newEntries) {
            localRepository.save(entry);
        }

        return !newEntries.isEmpty();
    }

    private boolean removeEntries(final List<DatabaseEntry> removedEntries) {
        for (DatabaseEntry entry : removedEntries) {
            localRepository.delete(entry);
        }

        return !removedEntries.isEmpty();
    }

    private void updateSynchronizationDate() {
        localRepository.updateSynchronizationDate();
    }

    private DatabaseData getDatabaseData(final SQLiteRepository repository) {
        return new DatabaseData(new Date(), getEntries(repository), getCategories(repository));
    }

    private List<DatabaseEntry> getEntries(final SQLiteRepository repository) {
        List<Entry> entries = repository.listEntries();
        List<DatabaseEntry> databaseEntries = new ArrayList<DatabaseEntry>(entries.size());

        for (Entry entry : entries) {
            databaseEntries.add((DatabaseEntry) entry);
        }

        return databaseEntries;

    }

    private List<DatabaseEntryCategory> getCategories(final SQLiteRepository repository) {
        List<EntryCategory> categories = repository.listCategories();
        List<DatabaseEntryCategory> databaseCategories = new ArrayList<DatabaseEntryCategory>(categories.size());

        for (EntryCategory category : categories) {
            databaseCategories.add((DatabaseEntryCategory) category);
        }

        return databaseCategories;
    }
}
