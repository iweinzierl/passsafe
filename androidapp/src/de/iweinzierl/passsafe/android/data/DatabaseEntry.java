package de.iweinzierl.passsafe.android.data;

import com.google.common.base.Preconditions;

import de.iweinzierl.passsafe.shared.domain.Entry;

public class DatabaseEntry extends Entry {

    public static class Builder {

        private DatabaseEntry entry;

        public Builder() {
            entry = new DatabaseEntry();
        }

        public Builder withId(final int id) {
            entry.setId(id);
            return this;
        }

        public Builder withCategory(final DatabaseEntryCategory category) {
            entry.setCategory(category);
            return this;
        }

        public Builder withTitle(final String title) {
            entry.setTitle(title);
            return this;
        }

        public Builder withUrl(final String url) {
            entry.setUrl(url);
            return this;
        }

        public Builder withUsername(final String username) {
            entry.setUsername(username);
            return this;
        }

        public Builder withPassword(final String password) {
            entry.setPassword(password);
            return this;
        }

        public Builder withComment(final String comment) {
            entry.setComment(comment);
            return this;
        }

        public DatabaseEntry build() {
            Preconditions.checkNotNull(entry.getTitle());
            Preconditions.checkNotNull(entry.getPassword());

            return entry;
        }
    }

    private int id;

    private DatabaseEntry() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
