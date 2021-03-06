package de.iweinzierl.passsafe.shared.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DatabaseEntryCategory extends EntryCategory {

    public static class Builder {

        private DatabaseEntryCategory category;

        public Builder() {
            category = new DatabaseEntryCategory();
        }

        public Builder withId(final int id) {
            category.setId(id);
            return this;
        }

        public Builder withTitle(final String title) {
            category.setTitle(title);
            return this;
        }

        public Builder withLastModified(final Date lastModified) {
            category.setLastModified(lastModified);
            return this;
        }

        public Builder withDeleted(final boolean deleted) {
            category.setDeleted(deleted);
            return this;
        }

        public DatabaseEntryCategory build() {
            return category;
        }
    }

    private int id;

    private Date lastModified;

    private DatabaseEntryCategory() {
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(getTitle()).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DatabaseEntryCategory)) {
            return false;
        }

        DatabaseEntryCategory other = (DatabaseEntryCategory) obj;

        return new EqualsBuilder().append(id, other.id).append(getTitle(), other.getTitle())
                                  .append(isDeleted(), other.isDeleted()).isEquals();
    }
}
