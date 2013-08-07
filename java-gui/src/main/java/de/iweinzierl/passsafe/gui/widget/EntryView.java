package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.resources.Images;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.gui.widget.secret.SwitchablePasswordField;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

// TODO make text fields smaller
public class EntryView extends JPanel {

    private class ClipboardMenu extends JMenuItem {

        private ClipboardMenu(JComponent component) {
            super(new ClipboardMenuAction(component));
        }
    }

    private class ClipboardMenuAction implements Action {

        private JComponent component;

        private ClipboardMenuAction(JComponent component) {
            this.component = component;
        }

        @Override
        public Object getValue(String key) {
            switch (key) {
                case "Name":
                    return Messages.getMessage(Messages.ENTRYVIEW_MENU_COPYTOCLIPBOARD);
            }

            return null;
        }

        @Override
        public void putValue(String key, Object value) {
            // nothing to do here
        }

        @Override
        public void setEnabled(boolean b) {
            // nothing to do here
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // nothing to do here
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // nothing to do here
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = getToolkit().getSystemClipboard();
            String text = null;
            if (component instanceof JTextField) {
                text = ((JTextField) component).getText();
            } else if (component instanceof SwitchablePasswordField) {
                text = ((SwitchablePasswordField) component).getPassword();
            }

            StringSelection stringSelection = new StringSelection(text);
            clipboard.setContents(stringSelection, null);
        }
    }

    private ApplicationController controller;

    private Entry entry;

    private JTextField titleField;
    private SwitchablePasswordField usernameField;
    private SwitchablePasswordField passwordField;

    public EntryView(ApplicationController controller) {
        super();
        this.controller = controller;

        titleField = new JTextField();
        usernameField = new SwitchablePasswordField();
        passwordField = new SwitchablePasswordField();

        initialize();
    }

    private void initialize() {
        setPreferredSize(new Dimension(200, 100));
        setLayout(new GridLayout(3, 3));

        titleField.setEditable(false);
        usernameField.setEditable(false);
        passwordField.setEditable(false);

        add(new JLabel(Messages.getMessage(Messages.ENTRYVIEW_LABEL_TITLE)));
        add(titleField);
        add(createStandardButtons(titleField));

        add(new JLabel(Messages.getMessage(Messages.ENTRYVIEW_LABEL_USERNAME)));
        add(usernameField);
        add(createSecretButtons(usernameField));

        add(new JLabel(Messages.getMessage(Messages.ENTRYVIEW_LABEL_PASSWORD)));
        add(passwordField);
        add(createSecretButtons(passwordField));

        setPreferredSize(new Dimension(300, 150));
        setMinimumSize(new Dimension(300, 150));

        initializeClipboardFunctions(titleField);
        initializeClipboardFunctions(usernameField);
        initializeClipboardFunctions(passwordField);
    }

    private void initializeClipboardFunctions(JComponent textField) {
        JPopupMenu componentPopupMenu = textField.getComponentPopupMenu();
        if (componentPopupMenu == null) {
            componentPopupMenu = new JPopupMenu(Messages.getMessage(Messages.ENTRYVIEW_MENU_TITLE));
            textField.setComponentPopupMenu(componentPopupMenu);
        }

        componentPopupMenu.add(new ClipboardMenu(textField));
    }

    public void apply(Entry entry) {
        if (entry != null) {
            this.entry = entry;
            titleField.setText(entry.getTitle());
            usernameField.setPassword(entry.getUsername());
            passwordField.setPassword(entry.getPassword());
        }
    }

    public void reset() {
        titleField.setText("");
        usernameField.setPassword("");
        passwordField.setPassword("");
    }

    private JPanel createStandardButtons(final JTextField textField) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JButton edit = UiUtils.createImageButton(Images.ENTRYVIEW_BUTTON_EDIT);
        final JButton save = UiUtils.createImageButton(Images.ENTRYVIEW_BUTTON_SAVE);
        final JButton cancel = UiUtils.createImageButton(Images.ENTRYVIEW_BUTTON_CANCEL);

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

    private JPanel createSecretButtons(final SwitchablePasswordField secretField) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JButton edit = UiUtils.createImageButton(Images.ENTRYVIEW_BUTTON_EDIT);
        final JButton save = UiUtils.createImageButton(Images.ENTRYVIEW_BUTTON_SAVE);
        final JButton cancel = UiUtils.createImageButton(Images.ENTRYVIEW_BUTTON_CANCEL);
        final JButton visible = new JButton(Messages.getMessage(Messages.ENTRYVIEW_BUTTON_VISIBLE));
        final JButton invisible = new JButton(Messages.getMessage(Messages.ENTRYVIEW_BUTTON_INVISIBLE));

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
                secretField.setPassword(secretField.getOrigPassword());
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        visible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.showPassword();
                invisible.setVisible(true);
                visible.setVisible(false);
            }
        });

        invisible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.hidePassword();
                invisible.setVisible(false);
                visible.setVisible(true);
            }
        });

        save.setEnabled(false);
        cancel.setEnabled(false);
        invisible.setVisible(false);

        panel.add(visible);
        panel.add(invisible);
        panel.add(edit);
        panel.add(save);
        panel.add(cancel);

        return panel;
    }
}
