package de.iweinzierl.passsafe.shared.domain;

import java.util.Date;

public class DatabaseEntry extends Entry {

    public static class Builder {

        private DatabaseEntry entry;

        public Builder() {
            entry = new DatabaseEntry();
        }

        public Builder withEntry(final Entry entry) {
            if (entry.getCategory() instanceof DatabaseEntryCategory) {
                withCategory((DatabaseEntryCategory) entry.getCategory());
            }

            if (entry instanceof DatabaseEntry) {
                withLastModified(((DatabaseEntry) entry).getLastModified());
            }

            withTitle(entry.getTitle());
            withUrl(entry.getUrl());
            withUsername(entry.getUsername());
            withPassword(entry.getPassword());
            withComment(entry.getComment());
            withDeleted(entry.isDeleted());

            return this;
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

        public Builder withLastModified(final Date lastModified) {
            entry.setLastModified(lastModified);
            return this;
        }

        public Builder withDeleted(final boolean deleted) {
            entry.setDeleted(deleted);
            return this;
        }

        public DatabaseEntry build() {
            return entry;
        }
    }

    private int id;

    private Date lastModified;

    private DatabaseEntry() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }
}
