package de.iweinzierl.passsafe.android.data;

import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class DatabaseEntry extends Entry {

    private int id;

    public DatabaseEntry(final EntryCategory category, final String title, final String username,
            final String password) {
        super(category, title, username, password);
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
