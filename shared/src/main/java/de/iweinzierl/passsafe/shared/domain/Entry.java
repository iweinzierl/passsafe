package de.iweinzierl.passsafe.shared.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


public class Entry implements Serializable {

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


    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("category", category.getTitle()).
                append("title", title).append("username", username).append("password", password).toString();
    }
}
