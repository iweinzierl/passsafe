package de.iweinzierl.passsafe.android.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

import de.iweinzierl.passsafe.shared.domain.EntryCategory;

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

        public DatabaseEntryCategory build() {
            Preconditions.checkNotNull(category.getTitle());

            return category;
        }
    }

    private int id;

    private DatabaseEntryCategory() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
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

        return new EqualsBuilder().append(id, other.id).append(getTitle(), other.getTitle()).isEquals();
    }
}
