package de.iweinzierl.passsafe.shared.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EntryCategory implements Serializable {

    public static final EntryCategory DEFAULT_CATEGORY = new EntryCategory("default");

    private String title;

    public EntryCategory() { }

    public EntryCategory(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(title).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        EntryCategory other = (EntryCategory) obj;

        return new EqualsBuilder().appendSuper(super.equals(obj)).append(title, other.title).isEquals();
    }
}
