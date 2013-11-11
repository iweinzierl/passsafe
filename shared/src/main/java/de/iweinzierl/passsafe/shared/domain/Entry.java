package de.iweinzierl.passsafe.shared.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Entry implements Serializable {

    private EntryCategory category;

    private String title;
    private String url;
    private String username;
    private String password;
    private String comment;

    public Entry(final EntryCategory category, final String title, final String username, final String password) {
        this(category, title, null, username, password);
    }

    public Entry(final EntryCategory category, final String title, final String url, final String username,
            final String password) {

        this(category, title, url, username, password, null);
    }

    public Entry(final EntryCategory category, final String title, final String url, final String username,
            final String password, final String comment) {
        this.category = category;
        this.title = title;
        this.url = url;
        this.username = username;
        this.password = password;
        this.comment = comment;
    }

    public EntryCategory getCategory() {
        return category;
    }

    public void setCategory(final EntryCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("category", category.getTitle())
                                        .append("title", title).append("url", url).append("username", username)
                                        .append("password", password).append("comment", comment).toString();
    }
}
