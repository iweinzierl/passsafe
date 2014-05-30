package de.iweinzierl.passsafe.shared.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;

public final class DatabaseSyncHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSyncHelper.class);

    private final DatabaseData localTable;
    private final DatabaseData upstreamTable;

    public DatabaseSyncHelper(final DatabaseData localTable, final DatabaseData upstreamTable) {
        this.localTable = localTable;
        this.upstreamTable = upstreamTable;
    }

    public List<DatabaseEntryCategory> getNewCategories() {
        if (upstreamTable.getCategories().isEmpty()) {
            return new ArrayList<DatabaseEntryCategory>(0);
        }

        return findNewCategories(localTable.getCategories(), upstreamTable.getCategories());
    }

    public List<DatabaseEntryCategory> getRemovedCategories() {
        if (upstreamTable.getCategories().isEmpty()) {
            return new ArrayList<DatabaseEntryCategory>(0);
        }

        return findRemovedCategories(localTable.getCategories(), upstreamTable.getCategories());
    }

    public List<DatabaseEntry> getEntriesWithRequiredUpdate() {
        if (upstreamTable.getEntries().isEmpty()) {
            return new ArrayList<DatabaseEntry>(0);
        }

        return findRequiredUpdates(localTable.getEntries(), upstreamTable.getEntries());
    }

    public List<DatabaseEntry> getNewEntries() {
        if (upstreamTable.getEntries().isEmpty()) {
            return new ArrayList<DatabaseEntry>(0);
        }

        return findNew(localTable.getEntries(), upstreamTable.getEntries());
    }

    public List<DatabaseEntry> getRemovedEntries() {
        if (upstreamTable.getEntries().isEmpty()) {
            return new ArrayList<DatabaseEntry>(0);
        }

        return findRemoved(localTable.getEntries(), upstreamTable.getEntries());
    }

    public boolean isUploadRequired(final List<DatabaseEntry> localEntries, final List<DatabaseEntry> upstreamEntries) {

        // search for local entries that have been inserted and that are not online yet
        A:
        for (DatabaseEntry localEntry : localEntries) {

            for (DatabaseEntry onlineEntry : upstreamEntries) {

                if (localEntry.getId() == onlineEntry.getId()
                        && localEntry.getLastModified().after(onlineEntry.getLastModified())) {

                    LOGGER.info("Updated local entry found - upload is required!");
                    return true;

                } else if (localEntry.getId() == onlineEntry.getId()) {
                    continue A;
                }
            }

            LOGGER.info("New local entry found - upload is required!");
            return true;
        }

        // search for upstream entries that have been deleted locally
        B:
        for (DatabaseEntry upstreamEntry : upstreamTable.getEntries()) {

            for (DatabaseEntry localEntry : localTable.getEntries()) {

                if (localEntry.getId() == upstreamEntry.getId()) {
                    continue B;
                }
            }

            LOGGER.info("Locally deleted entry found - upload is required!");
            return true;
        }

        C:
        for (DatabaseEntryCategory localCategory : localTable.getCategories()) {

            for (DatabaseEntryCategory upstreamCategory : upstreamTable.getCategories()) {

                if (localCategory.getId() == upstreamCategory.getId()) {
                    continue C;
                }
            }

            LOGGER.info("New local category found - upload is required!");
            return true;
        }

        D:
        for (DatabaseEntryCategory upstreamCategory : upstreamTable.getCategories()) {

            for (DatabaseEntryCategory localCategory : localTable.getCategories()) {

                if (localCategory.getId() == upstreamCategory.getId()) {
                    continue D;
                }
            }

            LOGGER.info("Locally deleted category found - upload is required!");
            return true;
        }

        return false;
    }

    private List<DatabaseEntryCategory> findNewCategories(final List<DatabaseEntryCategory> localCategories,
            final List<DatabaseEntryCategory> upstreamCategories) {

        List<DatabaseEntryCategory> newCategories = new ArrayList<DatabaseEntryCategory>();

        A:
        for (DatabaseEntryCategory upstreamCategory : upstreamCategories) {
            for (DatabaseEntryCategory localCategory : localCategories) {

                if (localCategory.getId() == upstreamCategory.getId()) {
                    continue A;
                }
            }

            newCategories.add(upstreamCategory);
        }

        return newCategories;
    }

    private List<DatabaseEntryCategory> findRemovedCategories(final List<DatabaseEntryCategory> local,
            final List<DatabaseEntryCategory> upstream) {

        List<DatabaseEntryCategory> removedCategories = new ArrayList<DatabaseEntryCategory>();

        for (DatabaseEntryCategory localCategory : local) {
            for (DatabaseEntryCategory upstreamCategory : upstream) {

                if (localCategory.getId() == upstreamCategory.getId()) {
                    if (upstreamCategory.isDeleted() && !localCategory.isDeleted()) {
                        removedCategories.add(localCategory);
                    }
                }
            }
        }

        return removedCategories;
    }

    private List<DatabaseEntry> findRequiredUpdates(final List<DatabaseEntry> local, final List<DatabaseEntry> online) {

        List<DatabaseEntry> updateRequired = new ArrayList<DatabaseEntry>();

        A:
        for (DatabaseEntry localEntry : local) {
            for (DatabaseEntry onlineEntry : online) {

                if (localEntry.getId() == onlineEntry.getId()) {
                    if (localEntry.getLastModified().before(onlineEntry.getLastModified())) {
                        updateRequired.add(onlineEntry);
                        continue A;
                    }
                }
            }
        }

        return updateRequired;
    }

    private List<DatabaseEntry> findNew(final List<DatabaseEntry> local, final List<DatabaseEntry> upstream) {

        List<DatabaseEntry> newEntries = new ArrayList<DatabaseEntry>();

        A:
        for (DatabaseEntry upstreamEntry : upstream) {
            for (DatabaseEntry localEntry : local) {

                if (localEntry.getId() == upstreamEntry.getId()) {
                    continue A;
                }
            }

            Date entryLastModified = upstreamEntry.getLastModified();
            Date tableLastSynchronized = localTable.getLastSynchronization();

            if (entryLastModified.after(tableLastSynchronized)) {
                newEntries.add(upstreamEntry);
            }
        }

        return newEntries;
    }

    private List<DatabaseEntry> findRemoved(final List<DatabaseEntry> local, final List<DatabaseEntry> upstream) {

        List<DatabaseEntry> removedEntries = new ArrayList<DatabaseEntry>();

        for (DatabaseEntry localEntry : local) {
            for (DatabaseEntry upstreamEntry : upstream) {

                if (localEntry.getId() == upstreamEntry.getId()) {
                    if (upstreamEntry.isDeleted() && !localEntry.isDeleted()) {
                        removedEntries.add(localEntry);
                    }
                }
            }
        }

        return removedEntries;
    }
}
