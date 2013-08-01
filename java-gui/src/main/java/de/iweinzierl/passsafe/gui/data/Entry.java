package de.iweinzierl.passsafe.gui.data;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class Entry {

    private EntryCategory category;

    private String title;
    private String username;
    private String password;


    public Entry(EntryCategory category, String title, String username, String password) {
        this.category = category;
        this.title = title;
        this.username = username;
        this.password = password;
    }

    public EntryCategory getCategory() {
        return category;
    }

    public void setCategory(EntryCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("category", category.getTitle()).
                append("title", title).append("username", username).append("password", password).toString();
    }
}
