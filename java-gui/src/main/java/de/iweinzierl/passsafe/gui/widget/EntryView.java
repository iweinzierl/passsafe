package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO make text fields smaller
public class EntryView extends JPanel {

    private ApplicationController controller;

    private Entry entry;

    private JTextField titleField;
    private JPasswordField usernameField;
    private JPasswordField passwordField;

    public EntryView(ApplicationController controller) {
        super();
        this.controller = controller;

        titleField = new JTextField();
        usernameField = new JPasswordField();
        passwordField = new JPasswordField();

        initialize();
    }

    private void initialize() {
        setPreferredSize(new Dimension(200, 100));
        setLayout(new GridLayout(3, 3));

        titleField.setEditable(false);
        usernameField.setEditable(false);
        passwordField.setEditable(false);

        add(new JLabel("Titel:"));
        add(titleField);
        add(createStandardButtons(titleField));

        add(new JLabel("User:"));
        add(usernameField);
        add(createSecretButtons(usernameField));

        add(new JLabel("Passwort:"));
        add(passwordField);
        add(createSecretButtons(passwordField));

        setPreferredSize(new Dimension(300, 150));
        setMinimumSize(new Dimension(300, 150));
    }

    public void apply(Entry entry) {
        if (entry != null) {
            this.entry = entry;
            titleField.setText(entry.getTitle());
            usernameField.setText(entry.getUsername());
            passwordField.setText(entry.getPassword());
        }
    }

    public void reset() {
        titleField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }

    private JPanel createStandardButtons(final JTextField textField) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JButton edit = new JButton("Bearbeiten");
        final JButton save = new JButton("Speichern");
        final JButton cancel = new JButton("Cancel");

        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setEditable(true);
                edit.setEnabled(false);
                save.setEnabled(true);
                cancel.setEnabled(true);
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO save entry
                textField.setEditable(false);
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setEditable(false);
                textField.setText("Canceled"); // TODO set correct entry text
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        save.setEnabled(false);
        cancel.setEnabled(false);

        panel.add(edit);
        panel.add(save);
        panel.add(cancel);

        return panel;
    }

    private JPanel createSecretButtons(final JPasswordField secretField) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JButton edit = new JButton("Bearbeiten");
        final JButton save = new JButton("Speichern");
        final JButton cancel = new JButton("Cancel");
        final JButton visible = new JButton("Visible");

        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.setEditable(true);
                edit.setEnabled(false);
                save.setEnabled(true);
                cancel.setEnabled(true);
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO save entry
                secretField.setEditable(false);
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.setEditable(false);
                secretField.setText("Canceled"); // TODO set correct entry text
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        visible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO display secret
                // TODO change buttons (visible button <-> invisible button)
            }
        });

        save.setEnabled(false);
        cancel.setEnabled(false);

        panel.add(visible);
        panel.add(edit);
        panel.add(save);
        panel.add(cancel);

        return panel;
    }
}
