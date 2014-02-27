package de.iweinzierl.passsafe.shared.data;

import java.util.Date;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;

public class DatabaseData {

    private final Date lastSynchronization;

    private final List<DatabaseEntry> entries;
    private final List<DatabaseEntryCategory> categories;

    public DatabaseData(final Date lastSynchronization, final List<DatabaseEntry> entries,
            final List<DatabaseEntryCategory> categories) {

        this.lastSynchronization = lastSynchronization;
        this.entries = ImmutableList.copyOf(entries);
        this.categories = ImmutableList.copyOf(categories);
    }

    public Date getLastSynchronization() {
        return lastSynchronization;
    }

    public List<DatabaseEntry> getEntries() {
        return entries;
    }

    public List<DatabaseEntryCategory> getCategories() {
        return categories;
    }
}
