package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;

public class EntryView extends JPanel {

    private ApplicationController controller;

    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    public EntryView(ApplicationController controller) {
        super();
        this.controller = controller;

        titleLabel = new JLabel();
        usernameLabel = new JLabel();
        passwordLabel = new JLabel();

        initialize();
    }

    private void initialize() {
        setPreferredSize(new Dimension(200, 100));
        setLayout(new GridLayout(3, 1));

        add(titleLabel);
        add(usernameLabel);
        add(passwordLabel);
    }

    public void apply(Entry entry) {
        if (entry != null) {
            titleLabel.setText(entry.getTitle());
            usernameLabel.setText(entry.getUsername());
            passwordLabel.setText(entry.getPassword());
        }
    }

    public void reset() {
        titleLabel.setText("");
        usernameLabel.setText("");
        passwordLabel.setText("");
    }
}
