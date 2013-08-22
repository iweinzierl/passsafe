package de.iweinzierl.passsafe.shared.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class EntryCategory {

    public static final EntryCategory DEFAULT_CATEGORY = new EntryCategory("default");

    private String title;


    public EntryCategory(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        EntryCategory other = (EntryCategory) obj;

        return new EqualsBuilder().appendSuper(super.equals(obj)).append(title, other.title).isEquals();
    }
}
