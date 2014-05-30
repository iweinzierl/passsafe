package de.iweinzierl.passsafe.android.data;

import java.io.File;

import java.util.ArrayList;
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
    private final SQLiteRepository onlineRepository;
    private final DatabaseSyncHelper databaseSyncHelper;

    public DatabaseSyncProcessor(final Activity activity, final GoogleDriveSync googleDriveSync, final File local,
            final File upstream) {
        this.googleDriveSync = googleDriveSync;

        localRepository = new SQLiteRepository(activity, local);

        onlineRepository = new SQLiteRepository(activity, upstream);

        databaseSyncHelper = new DatabaseSyncHelper(getDatabaseData(localRepository),
                getDatabaseData(onlineRepository));
    }

    public boolean sync() {
        LOGGER.info("Start synchronizing databases");

        LOGGER.debug("Online repository contains %d entries in %d categories", onlineRepository.listEntries().size(),
            onlineRepository.listCategories().size());

        LOGGER.debug("Local repository contains %d entries in %d categories", localRepository.listEntries().size(),
            localRepository.listCategories().size());

        insertCategories(databaseSyncHelper.getNewCategories());
        removeCategories(databaseSyncHelper.getRemovedCategories());

        updateExistingEntries(databaseSyncHelper.getEntriesWithRequiredUpdate());
        insertEntries(databaseSyncHelper.getNewEntries());
        removeEntries(databaseSyncHelper.getRemovedEntries());

        updateSynchronizationDate();

        boolean uploadRequired = databaseSyncHelper.isUploadRequired(getEntries(localRepository),
                getEntries(onlineRepository));

        if (uploadRequired) {
            googleDriveSync.onUpdate(GoogleDriveSync.State.UPLOAD_REQUESTED);
        }

        googleDriveSync.onUpdate(GoogleDriveSync.State.COMPLETED);

        return uploadRequired;
    }

    private boolean insertCategories(final List<DatabaseEntryCategory> newCategories) {
        LOGGER.info("Found %d categories that need to be inserted", newCategories.size());
        for (DatabaseEntryCategory category : newCategories) {
            localRepository.save(category);
        }

        return !newCategories.isEmpty();
    }

    private boolean removeCategories(final List<DatabaseEntryCategory> removedCategories) {
        LOGGER.info("Found %d categories that need to be removed", removedCategories.size());
        for (DatabaseEntryCategory category : removedCategories) {
            localRepository.delete(category);
        }

        return !removedCategories.isEmpty();
    }

    private boolean updateExistingEntries(final List<DatabaseEntry> entriesWithRequiredUpdate) {
        LOGGER.info("Found %d entries that need an update", entriesWithRequiredUpdate.size());
        for (DatabaseEntry entry : entriesWithRequiredUpdate) {
            localRepository.update(entry);
        }

        return !entriesWithRequiredUpdate.isEmpty();
    }

    private boolean insertEntries(final List<DatabaseEntry> newEntries) {
        LOGGER.info("Found %d entries that need to be inserted", newEntries.size());
        for (DatabaseEntry entry : newEntries) {
            localRepository.save(entry);
        }

        return !newEntries.isEmpty();
    }

    private boolean removeEntries(final List<DatabaseEntry> removedEntries) {
        LOGGER.info("Found %d entries that need to be removed", removedEntries.size());
        for (DatabaseEntry entry : removedEntries) {
            localRepository.delete(entry);
        }

        return !removedEntries.isEmpty();
    }

    private void updateSynchronizationDate() {
        localRepository.updateSynchronizationDate();
    }

    private DatabaseData getDatabaseData(final SQLiteRepository repository) {
        return new DatabaseData(repository.getSynchronizationDate(), getEntries(repository), getCategories(repository));
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
