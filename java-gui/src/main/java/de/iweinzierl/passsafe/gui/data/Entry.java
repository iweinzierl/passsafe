package de.iweinzierl.passsafe.gui.data;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class Entry {

    private String title;
    private String username;
    private String password;


    public Entry(String title, String username, String password) {
        this.title = title;
        this.username = username;
        this.password = password;
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
        return new ToStringBuilder(this).appendSuper(super.toString()).append("title", title)
                .append("username", username).append("password", password).toString();
    }
}
