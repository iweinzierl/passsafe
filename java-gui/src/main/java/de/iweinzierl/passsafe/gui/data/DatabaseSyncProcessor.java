package de.iweinzierl.passsafe.gui.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.shared.data.DatabaseData;
import de.iweinzierl.passsafe.shared.data.DatabaseSyncHelper;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class DatabaseSyncProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSyncProcessor.class);

    private final SqliteDataSource localDatasource;
    private final SqliteDataSource onlineDatasource;

    public DatabaseSyncProcessor(final SqliteDataSource localDatasource, final SqliteDataSource onlineDatasource) {
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

        updateExistingEntries(helper.getEntriesWithRequiredUpdate());
        insertEntries(helper.getNewEntries());
        removeEntries(helper.getRemovedEntries());

        return helper.isUploadRequired();
    }

    private void updateExistingEntries(final List<DatabaseEntry> entriesWithRequiredUpdate) {
        LOGGER.info("Found {} entries that need to be updated", entriesWithRequiredUpdate.size());

        for (DatabaseEntry entry : entriesWithRequiredUpdate) {
            localDatasource.updateEntry(entry);
        }
    }

    private void insertEntries(final List<DatabaseEntry> newEntries) {
        LOGGER.info("Found {} entries that need to be inserted", newEntries.size());

        for (DatabaseEntry entry : newEntries) {
            localDatasource.addEntry(entry.getCategory(), entry);
        }
    }

    private void removeEntries(final List<DatabaseEntry> removedEntries) {
        LOGGER.info("Found {} entries that need to be removed", removedEntries.size());

        for (DatabaseEntry entry : removedEntries) {
            localDatasource.removeEntry(entry);
        }
    }

    private static DatabaseData getDatabaseData(final SqliteDataSource dataSource) {
        List<EntryCategory> categories = dataSource.getCategories();

        List<DatabaseEntryCategory> databaseCategories = new ArrayList<>();
        List<DatabaseEntry> databaseEntries = new ArrayList<>();

        for (EntryCategory category : categories) {
            databaseCategories.add((DatabaseEntryCategory) category);

            databaseEntries.addAll(getDatabaseEntries(dataSource, category));
        }

        return new DatabaseData(new Date(), databaseEntries, databaseCategories);
    }

    private static Collection<DatabaseEntry> getDatabaseEntries(final SqliteDataSource dataSource,
            final EntryCategory category) {
        List<Entry> allEntries = dataSource.getAllEntries(category);
        List<DatabaseEntry> databaseEntries = new ArrayList<>(allEntries.size());

        for (Entry entry : allEntries) {
            databaseEntries.add((DatabaseEntry) entry);
        }

        return databaseEntries;
    }
}
