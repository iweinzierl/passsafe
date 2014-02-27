package de.iweinzierl.passsafe.android.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;

import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.android.util.FileUtils;
import de.iweinzierl.passsafe.shared.data.DatabaseData;
import de.iweinzierl.passsafe.shared.data.DatabaseSyncHelper;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class DatabaseSyncProcessor {

    private static final Logger LOGGER = new Logger("DatabaseSyncProcessor");

    private final Activity activity;
    private final GoogleDriveSync googleDriveSync;

    private final SQLiteRepository localRepository;
    private final SQLiteRepository onlineRepository;

    private final DatabaseSyncHelper databaseSyncHelper;

    public DatabaseSyncProcessor(final Activity activity, final GoogleDriveSync googleDriveSync) {
        this.activity = activity;
        this.googleDriveSync = googleDriveSync;

        localRepository = new SQLiteRepository(activity, FileUtils.getDatabaseFile(activity));
        onlineRepository = new SQLiteRepository(activity, FileUtils.getTemporaryDatabaseFile(activity));

        databaseSyncHelper = new DatabaseSyncHelper(getDatabaseData(localRepository),
                getDatabaseData(onlineRepository));
    }

    public void sync() {
        LOGGER.info("Start synchronizing databases");
        // TODO
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
