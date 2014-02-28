package de.iweinzierl.passsafe.shared.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;

public final class DatabaseSyncHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSyncHelper.class);

    private final DatabaseData localTable;
    private final DatabaseData upstreamTable;

    public DatabaseSyncHelper(final DatabaseData localTable, final DatabaseData upstreamTable) {
        this.localTable = localTable;
        this.upstreamTable = upstreamTable;
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

        return findRemoved(localTable.getLastSynchronization(), localTable.getEntries(), upstreamTable.getEntries());
    }

    private List<DatabaseEntry> findRequiredUpdates(final List<DatabaseEntry> local, final List<DatabaseEntry> online) {

        List<DatabaseEntry> updateRequired = new ArrayList<DatabaseEntry>();

        A:
        for (DatabaseEntry localEntry : local) {
            for (DatabaseEntry onlineEntry : online) {

                if (localEntry.getId() == onlineEntry.getId() && localEntry.getTitle().equals(onlineEntry.getTitle())) {
                    if (localEntry.getLastModified().getTime() < onlineEntry.getLastModified().getTime()) {
                        updateRequired.add(onlineEntry);
                        continue A;
                    }
                } else if (localEntry.getId() == onlineEntry.getId()
                        && !localEntry.getTitle().equals(onlineEntry.getTitle())) {

                    LOGGER.warn("Found entries with same ID but different title: '{}' and '{}'" + localEntry.getTitle(),
                        onlineEntry.getTitle());
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

                if (localEntry.getId() == upstreamEntry.getId()
                        && localEntry.getTitle().equals(upstreamEntry.getTitle())) {

                    continue A;

                } else if (localEntry.getId() == upstreamEntry.getId()
                        && !localEntry.getTitle().equals(upstreamEntry.getTitle())) {

                    LOGGER.warn("Found entries with same ID but different title: '{}' and '{}'" + localEntry.getTitle(),
                        upstreamEntry.getTitle());
                }
            }

            newEntries.add(upstreamEntry);
        }

        return newEntries;
    }

    private List<DatabaseEntry> findRemoved(final Date lastSynchronization, final List<DatabaseEntry> local,
            final List<DatabaseEntry> upstream) {

        List<DatabaseEntry> removedEntries = new ArrayList<DatabaseEntry>();

        A:
        for (DatabaseEntry localEntry : local) {
            for (DatabaseEntry upstreamEntry : upstream) {

                if (localEntry.getId() == upstreamEntry.getId()
                        && localEntry.getTitle().equals(upstreamEntry.getTitle())) {

                    continue A;

                } else if (localEntry.getId() == upstreamEntry.getId()
                        && !localEntry.getTitle().equals(upstreamEntry.getTitle())) {

                    LOGGER.warn("Found entries with same ID but different title: '{}' and '{}'" + localEntry.getTitle(),
                        upstreamEntry.getTitle());
                }
            }

            if (localEntry.getLastModified().getTime() < lastSynchronization.getTime()) {
                removedEntries.add(localEntry);
            }
        }

        return removedEntries;
    }
}
